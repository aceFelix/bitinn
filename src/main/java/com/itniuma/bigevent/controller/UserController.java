package com.itniuma.bigevent.controller;

import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.pojo.User;
import com.itniuma.bigevent.service.UserService;
import com.itniuma.bigevent.utils.JwtUtil;
import com.itniuma.bigevent.utils.Md5Util;
import com.itniuma.bigevent.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aceFelix
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{3,15}$") String username,
                           @Pattern(regexp = "^\\S{3,15}$") String password) {
        User user = userService.findByUsername(username);
        if (user == null) {
            userService.register(username, password);
            return Result.success();
        }
        return Result.error("用户已存在");
    }

    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{3,15}$") String username,
                        @Pattern(regexp = "^\\S{3,15}$") String password) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (Md5Util.getMD5String(password).equals(user.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.genToken(claims);
            stringRedisTemplate.opsForValue().set(token, token, 1, TimeUnit.HOURS);
            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    @GetMapping({"/userInfo", "/info"})
    public Result<User> userInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params,
                            @RequestHeader(name = "Authorization") String token) {
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("参数错误");
        }

        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        if (!Md5Util.getMD5String(oldPwd).equals(user.getPassword())) {
            return Result.error("原密码错误");
        }

        if (!newPwd.equals(rePwd)) {
            return Result.error("两次密码不一致");
        }

        userService.updatePwd(newPwd);
        stringRedisTemplate.delete(token);
        return Result.success();
    }
}
