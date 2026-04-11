package com.itniuma.bitinn.mapper;

import com.itniuma.bitinn.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    void insert(Article article);

    void update(Article article);

    void deleteById(Integer id);

    Article findById(Integer id);

    List<Article> list(@Param("categoryId") Integer categoryId,
                       @Param("state") String state,
                       @Param("userId") Integer userId);

    List<Article> listWithAuthor(@Param("categoryId") Integer categoryId,
                                 @Param("state") String state,
                                 @Param("userId") Integer userId,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    Long countList(@Param("categoryId") Integer categoryId,
                   @Param("state") String state,
                   @Param("userId") Integer userId);

    void incrementViewCount(@Param("id") Integer id);

    void insertArticleTag(@Param("articleId") Integer articleId, @Param("tagId") Integer tagId);

    void deleteArticleTagsByArticleId(Integer articleId);
}
