package com.itniuma.bitinn.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itniuma.bitinn.enums.UserRole;
import com.itniuma.bitinn.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // 主键ID
    @NotNull
    private Integer id;
    // 用户名
    private String username;
    // 密码
    @JsonIgnore
    private String password;
    // 昵称
    @NotNull
    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;
    // 邮箱
    @NotNull
    @Email
    private String email;
    // 用户头像地址
    private String userPic;
    // 用户简介
    private String bio;
    // 手机号
    private String phone;
    // 角色
    private UserRole role;
    // 账号状态
    private UserStatus status;
    // 最后登录时间
    private LocalDateTime lastLoginTime;
    // 最后登录IP
    private String lastLoginIp;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}
