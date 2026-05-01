package com.itniuma.bitinn.service.user;

import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.User;

public interface UserService {

    Result register(String username, String password, String email);

    Result<String> login(String username, String password);

    Result<User> getUserInfo();

    Result updateUserInfo(User user);

    Result updateAvatar(String avatarUrl);

    Result updatePassword(String oldPwd, String newPwd, String rePwd);

    Result<User> getUserById(Integer id);
}
