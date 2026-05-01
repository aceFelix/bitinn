package com.itniuma.bitinn.mapper.interaction;

import com.itniuma.bitinn.pojo.ArticleFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleFavoriteMapper {

    void insert(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    void delete(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    ArticleFavorite findByArticleAndUser(@Param("articleId") Integer articleId, @Param("userId") Integer userId);

    List<Integer> findFavoritedArticleIdsByUserId(@Param("userId") Integer userId);

    Long countByArticleId(@Param("articleId") Integer articleId);
}
