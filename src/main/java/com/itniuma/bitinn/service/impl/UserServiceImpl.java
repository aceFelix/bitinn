package com.itniuma.bitinn.service.impl;

import com.itniuma.bitinn.mapper.UserMapper;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.User;
import com.itniuma.bitinn.service.UserService;
import com.itniuma.bitinn.utils.JwtUtil;
import com.itniuma.bitinn.utils.ThreadLocalUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String REGISTER_LOCK_PREFIX = "register:lock:";
    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String REGISTER_IDEMPOTENT_PREFIX = "register:idempotent:";
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_EXPIRE_SECONDS = 10;
    private static final int LOGIN_FAIL_EXPIRE_MINUTES = 30;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Result register(String username, String password, String email) {
        String lockKey = REGISTER_LOCK_PREFIX + username;
        String idempotentKey = REGISTER_IDEMPOTENT_PREFIX + username + ":" + email;

        Boolean acquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        if (acquired == null || !acquired) {
            return Result.error("请求处理中，请稍后再试");
        }

        try {
            String cachedResult = stringRedisTemplate.opsForValue().get(idempotentKey);
            if (cachedResult != null) {
                return Result.error("请勿重复提交注册请求");
            }

            stringRedisTemplate.opsForValue().set(idempotentKey, "1", 60, TimeUnit.SECONDS);

            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword);
            user.setNickname(username);
            user.setEmail(email);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            try {
                userMapper.insert(user);
            } catch (DuplicateKeyException e) {
                String message = e.getMessage();
                if (message != null && message.contains("username")) {
                    return Result.error("用户名已存在");
                } else if (message != null && message.contains("email")) {
                    return Result.error("邮箱已被注册");
                }
                return Result.error("用户名或邮箱已存在");
            }

            return Result.success(null, "注册成功");
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public Result<String> login(String username, String password) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr != null ? Integer.parseInt(failCountStr) : 0;
        
        if (failCount >= MAX_LOGIN_FAIL_COUNT) {
            return Result.error("登录失败次数过多，请30分钟后再试");
        }

        User user = userMapper.findByUsername(username);
        if (user == null) {
            incrementLoginFailCount(failKey);
            return Result.error("用户名或密码错误");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            incrementLoginFailCount(failKey);
            int remaining = MAX_LOGIN_FAIL_COUNT - failCount - 1;
            if (remaining > 0) {
                return Result.error("用户名或密码错误，剩余尝试次数：" + remaining);
            }
            return Result.error("用户名或密码错误");
        }

        stringRedisTemplate.delete(failKey);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        String token = JwtUtil.genToken(claims);

        stringRedisTemplate.opsForValue().set(token, token, 12, TimeUnit.HOURS);

        return Result.success(token, "登录成功");
    }

    private void incrementLoginFailCount(String failKey) {
        String count = stringRedisTemplate.opsForValue().get(failKey);
        if (count == null) {
            stringRedisTemplate.opsForValue().set(failKey, "1", LOGIN_FAIL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } else {
            stringRedisTemplate.opsForValue().increment(failKey);
        }
    }

    @Override
    public Result<User> getUserInfo() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        User user = userMapper.findByUsername((String) claims.get("username"));
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setPassword(null);

        return Result.success(user);
    }

    @Override
    public Result updateUserInfo(User user) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        user.setId(userId);
        user.setUpdateTime(LocalDateTime.now());

        userMapper.update(user);

        return Result.success(null, "更新成功");
    }

    @Override
    public Result updateAvatar(String avatarUrl) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        userMapper.updateAvatar(userId, avatarUrl);

        return Result.success(null, "头像更新成功");
    }

    @Override
    public Result updatePassword(String oldPwd, String newPwd, String rePwd) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return Result.error("原密码错误");
        }

        if (!newPwd.equals(rePwd)) {
            return Result.error("两次输入的新密码不一致");
        }

        String encodedPassword = passwordEncoder.encode(newPwd);
        userMapper.updatePassword(userId, encodedPassword);

        return Result.success(null, "密码修改成功");
    }
}
