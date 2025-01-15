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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 注册
     * @param username
     * @param password
     * @return Result
     */
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{3,15}$")String username, @Pattern(regexp = "^\\S{3,15}$")String password){
        // 查询用户是否已注册
        // 调用service层查询用户
        User user = userService.findByUsername(username);
        //用户存在，返回错误结果
        if (user == null){
            // 用户不存在，注册
            // 调用service层注册
            userService.register(username,password);
            return Result.success();
        }else{
            return Result.error("用户已存在");
        }

    }

    /**
     * 登录
     * @param username
     * @param password
     * @return Result
     */
    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{3,15}$")String username, @Pattern(regexp = "^\\S{3,15}$")String password){
        //查询用户是否存在
        User user = userService.findByUsername(username);
        //不存在，返回错误结果
        if (user == null){
            return Result.error("用户不存在");
        }
        // 存在，判断密码是否正确
        if (Md5Util.getMD5String(password).equals(user.getPassword())){
            // 密码正确，生成token
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",user.getId());
            claims.put("username",user.getUsername());
            final String token = JwtUtil.genToken(claims);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    /**
     * 获取用户信息
     * @return user
     */
    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name = "Authorization") String token*/){
        // 根据用户名查询用户
        /*User user = userService.findByUsername(JwtUtil.parseToken(token).get("username").toString());*/
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }

    /**
     * 修改用户信息
     * @param user
     * @return result
     */
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    /**
     * 修改用户头像
     * @param avatarUrl
     * @return result
     */
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    /**
     * 修改用户密码
     * @param params
     * @return result
     */
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params){
        // 校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("参数错误");
        }
        // 校验元密码是否正确
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        if(!Md5Util.getMD5String(oldPwd).equals(user.getPassword())){
            return Result.error("原密码错误");
        }
        // newPwd和rePwd是否一致
        if(!newPwd.equals(rePwd)){
            return Result.error("两次密码不一致");
        }
        userService.updatePwd(newPwd);
        return Result.success();
    }
}
