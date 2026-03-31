package com.itniuma.bigevent.service.impl;

import com.itniuma.bigevent.mapper.UserMapper;
import com.itniuma.bigevent.pojo.User;
import com.itniuma.bigevent.service.UserService;
import com.itniuma.bigevent.utils.Md5Util;
import com.itniuma.bigevent.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户服务实现
 * @author aceFelix
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    // 根据用户名查询用户
    @Override
    public User findByUsername(String username) {
        // 调用mapp层查询用户
        User user = userMapper.findByUsername(username);
        return user;
    }
    // 注册用户
    @Override
    public void register(String username, String password) {
        // Md5Util对密码进行加密
        password = Md5Util.getMD5String(password);
        // 调用mapper层注册用户
        userMapper.register(username,password);
    }

    // 更新用户
    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    // 更新用户头像
    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get(); // 从ThreadLocal中获取用户业务数据
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    // 更新用户密码
    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get(); // 从ThreadLocal中获取用户业务数据
        Integer id = (Integer) map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);
    }
}
