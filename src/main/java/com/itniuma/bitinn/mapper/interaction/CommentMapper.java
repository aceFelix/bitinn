package com.itniuma.bitinn.mapper.interaction;

import com.itniuma.bitinn.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    void insert(Comment comment);

    void delete(@Param("id") Integer id);

    List<Comment> findByArticleId(@Param("articleId") Integer articleId);

    Long countByArticleId(@Param("articleId") Integer articleId);

    Comment findById(@Param("id") Integer id);
}
