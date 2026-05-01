package com.itniuma.bitinn.mapper.interaction;

import com.itniuma.bitinn.pojo.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowMapper {

    void insert(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    void delete(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    UserFollow findByFollowerAndFollowing(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    List<Integer> findFollowingIdsByFollowerId(@Param("followerId") Integer followerId);

    Integer countFollowers(@Param("userId") Integer userId);

    Integer countFollowing(@Param("userId") Integer userId);
}
