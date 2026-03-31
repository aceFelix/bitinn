package com.itniuma.bigevent.service;
import com.itniuma.bigevent.pojo.User;

import java.util.Map;

/**
 * 用户服务接口
 * @author aceFelix
 */
public interface UserService {
    //根据用户名查询用户
    User findByUsername(String username);
    //注册
    void register(String username, String password);
    // 更新
    void update(User user);
    // 更新头像
    void updateAvatar(String avatarUrl);
    // 修改密码
    void updatePwd(String newPwd);
}
