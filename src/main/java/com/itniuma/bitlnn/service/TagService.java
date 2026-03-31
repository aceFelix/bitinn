package com.itniuma.bitlnn.service;

import com.itniuma.bitlnn.pojo.Tag;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author aceFelix
 */
public interface TagService {
    void add(Tag tag);

    List<Tag> list();

    Tag detail(Integer id);

    void update(Tag tag);

    void delete(Integer id);

    List<Tag> findByArticleId(Integer articleId);

    void addArticleTags(Integer articleId, List<Integer> tagIds);

    void updateArticleTags(Integer articleId, List<Integer> tagIds);
}
