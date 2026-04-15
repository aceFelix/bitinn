package com.itniuma.bitinn.service.user.impl;

import com.itniuma.bitinn.mapper.user.UserMapper;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.User;
import com.itniuma.bitinn.service.user.UserService;
import com.itniuma.bitinn.utils.JwtUtil;
import com.itniuma.bitinn.utils.RedisCacheHelper;
import com.itniuma.bitinn.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisCacheHelper redisCache;

    private static final String REGISTER_LOCK_PREFIX = "register:lock:";
    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String REGISTER_IDEMPOTENT_PREFIX = "register:idempotent:";
    private static final String USER_CACHE_PREFIX = "user:info:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_EXPIRE_SECONDS = 10;
    private static final int LOGIN_FAIL_EXPIRE_MINUTES = 30;
    private static final int USER_CACHE_HOURS = 2;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, RedisCacheHelper redisCache) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisCache = redisCache;
    }

    @Override
    public Result register(String username, String password, String email) {
        String lockKey = REGISTER_LOCK_PREFIX + username;
        String idempotentKey = REGISTER_IDEMPOTENT_PREFIX + username + ":" + email;

        Boolean acquired = redisCache.setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (acquired == null || !acquired) {
            return Result.error("请求处理中，请稍后再试");
        }

        try {
            String cachedResult = redisCache.get(idempotentKey);
            if (cachedResult != null) {
                return Result.error("请勿重复提交注册请求");
            }

            redisCache.set(idempotentKey, "1", 60, TimeUnit.SECONDS);

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
            redisCache.delete(lockKey);
        }
    }

    @Override
    public Result<String> login(String username, String password) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        String failCountStr = redisCache.get(failKey);
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

        redisCache.delete(failKey);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        String token = JwtUtil.genToken(claims);

        // 不再存 token 白名单到 Redis（改为 JWT 本地解析 + 黑名单机制）
        // 仅在需要主动失效 token 时加入黑名单（如修改密码、登出）

        cacheUser(user);

        return Result.success(token, "登录成功");
    }

    private void incrementLoginFailCount(String failKey) {
        String count = redisCache.get(failKey);
        if (count == null) {
            redisCache.set(failKey, "1", LOGIN_FAIL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } else {
            redisCache.increment(failKey);
        }
    }

    private void cacheUser(User user) {
        user.setPassword(null);
        String cacheKey = USER_CACHE_PREFIX + user.getUsername();
        redisCache.set(cacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
    }

    private User getCachedUser(String username) {
        String cacheKey = USER_CACHE_PREFIX + username;
        return redisCache.get(cacheKey, User.class);
    }

    private void invalidateUserCache(String username) {
        String cacheKey = USER_CACHE_PREFIX + username;
        redisCache.delete(cacheKey);

        // 同时清除按 ID 缓存的用户信息
        // 需要先查出用户ID
        User user = userMapper.findByUsername(username);
        if (user != null) {
            redisCache.delete("user:info:id:" + user.getId());
        }
    }

    @Override
    public Result<User> getUserInfo() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        User user = getCachedUser(username);
        if (user != null) {
            return Result.success(user);
        }

        user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setPassword(null);
        cacheUser(user);

        return Result.success(user);
    }

    @Override
    public Result updateUserInfo(User user) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        user.setId(userId);
        user.setUpdateTime(LocalDateTime.now());

        userMapper.update(user);
        invalidateUserCache(username);

        return Result.success(null, "更新成功");
    }

    @Override
    public Result updateAvatar(String avatarUrl) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        userMapper.updateAvatar(userId, avatarUrl);
        invalidateUserCache(username);

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
        invalidateUserCache(username);

        // 将当前 token 加入黑名单（12小时过期，与 token 有效期一致）
        String currentToken = org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes() != null
                ? ((org.springframework.web.context.request.ServletRequestAttributes)
                        org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
                        .getRequest().getHeader("Authorization")
                : null;
        if (currentToken != null && !currentToken.isEmpty()) {
            redisCache.set(TOKEN_BLACKLIST_PREFIX + currentToken, "1", 12, TimeUnit.HOURS);
        }

        return Result.success(null, "密码修改成功");
    }

    @Override
    public Result<User> getUserById(Integer id) {
        // 先查缓存
        String cacheKey = "user:info:id:" + id;
        User user = redisCache.get(cacheKey, User.class);
        if (user != null) {
            return Result.success(user);
        }

        user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        // 写入缓存
        redisCache.set(cacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
        return Result.success(user);
    }
}
