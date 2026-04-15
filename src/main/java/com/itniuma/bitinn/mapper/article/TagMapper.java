package com.itniuma.bitinn.mapper;

import com.itniuma.bitinn.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {

    List<Tag> findByArticleId(Integer articleId);

    List<Tag> listByUser(Integer userId);

    List<Tag> list();
}
