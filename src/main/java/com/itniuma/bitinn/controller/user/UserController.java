package com.itniuma.bitinn.controller.user;

import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.User;
import com.itniuma.bitinn.service.user.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result register(
            @Pattern(regexp = "^\\S{3,16}$", message = "用户名必须是3-16位非空字符") @NotBlank String username,
            @Pattern(regexp = "^\\S{6,20}$", message = "密码必须是6-20位非空字符") @NotBlank String password,
            @Email(message = "邮箱格式不正确") @NotBlank String email
    ) {
        return userService.register(username, password, email);
    }

    @PostMapping("/login")
    public Result<String> login(
            @Pattern(regexp = "^\\S{3,16}$", message = "用户名必须是3-16位非空字符") @NotBlank String username,
            @Pattern(regexp = "^\\S{6,20}$", message = "密码必须是6-20位非空字符") @NotBlank String password
    ) {
        return userService.login(username, password);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        return userService.getUserInfo();
    }

    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody @Validated User user) {
        return userService.updateUserInfo(user);
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @NotBlank String avatarUrl) {
        return userService.updateAvatar(avatarUrl);
    }

    @PatchMapping("/updatePwd")
    public Result updatePassword(
            @Pattern(regexp = "^\\S{6,20}$", message = "原密码格式不正确") @NotBlank String oldPwd,
            @Pattern(regexp = "^\\S{6,20}$", message = "新密码必须是6-20位非空字符") @NotBlank String newPwd,
            @Pattern(regexp = "^\\S{6,20}$", message = "确认密码格式不正确") @NotBlank String rePwd
    ) {
        return userService.updatePassword(oldPwd, newPwd, rePwd);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }
}
