package com.itniuma.bitinn.mapper.interaction;

import com.itniuma.bitinn.pojo.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleLikeMapper {

    void insert(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    void delete(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    ArticleLike findByArticleAndUser(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    List<Integer> findLikedArticleIdsByUserId(@Param("userId") Integer userId);

    Long countByArticleId(@Param("articleId") Integer articleId);
}
