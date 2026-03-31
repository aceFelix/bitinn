package com.itniuma.bitlnn.mapper;
        
import com.itniuma.bitlnn.pojo.User;
import org.apache.ibatis.annotations.*;

/**
 * @author aceFelix
 */
@Mapper
public interface UserMapper {
    @Select("select * from user where username=#{username}")
    User findByUsername(String username);

    @Insert("insert into user(username,password,create_time,update_time) values(#{username},#{password},now(),now())")
    void register(@Param("username") String username, @Param("password") String password);

    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    void updateAvatar(@Param("avatarUrl") String avatarUrl, @Param("id") Integer id);

    @Update("update user set password=#{md5String},update_time=now() where id=#{id}")
    void updatePwd(@Param("md5String") String md5String, @Param("id") Integer id);
}
