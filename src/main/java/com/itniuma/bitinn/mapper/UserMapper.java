package com.itniuma.bitinn.mapper;

import com.itniuma.bitinn.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

    void insert(User user);

    void update(User user);

    void updateAvatar(@Param("id") Integer id, @Param("userPic") String userPic);

    void updatePassword(@Param("id") Integer id, @Param("password") String password);
}
