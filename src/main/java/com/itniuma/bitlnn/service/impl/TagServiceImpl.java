package com.itniuma.bitlnn.service.impl;

import com.itniuma.bitlnn.mapper.TagMapper;
import com.itniuma.bitlnn.pojo.Tag;
import com.itniuma.bitlnn.service.TagService;
import com.itniuma.bitlnn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 标签服务实现
 *
 * @author aceFelix
 */
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    private Integer getCurrentUserId() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        return (Integer) claims.get("id");
    }

    @Override
    public void add(Tag tag) {
        Tag existingTag = tagMapper.findByTagName(tag.getTagName());
        if (existingTag != null) {
            throw new RuntimeException("标签名称已存在");
        }
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        tag.setCreateUser(getCurrentUserId());
        if (tag.getTagColor() == null || tag.getTagColor().isEmpty()) {
            tag.setTagColor("#409EFF");
        }
        tagMapper.add(tag);
    }

    @Override
    public List<Tag> list() {
        return tagMapper.list(getCurrentUserId());
    }

    @Override
    public Tag detail(Integer id) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        return tag;
    }

    @Override
    public void update(Tag tag) {
        Tag existingTag = tagMapper.findByTagName(tag.getTagName());
        if (existingTag != null && !existingTag.getId().equals(tag.getId())) {
            throw new RuntimeException("标签名称已存在");
        }
        tag.setCreateUser(getCurrentUserId());
        tag.setUpdateTime(LocalDateTime.now());
        if (tagMapper.update(tag) == 0) {
            throw new RuntimeException("标签不存在或无权限修改");
        }
    }

    @Override
    public void delete(Integer id) {
        if (tagMapper.delete(id, getCurrentUserId()) == 0) {
            throw new RuntimeException("标签不存在或无权限删除");
        }
    }

    @Override
    public List<Tag> findByArticleId(Integer articleId) {
        return tagMapper.findByArticleId(articleId);
    }

    @Override
    @Transactional
    public void addArticleTags(Integer articleId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        tagMapper.addArticleTags(articleId, tagIds, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateArticleTags(Integer articleId, List<Integer> tagIds) {
        tagMapper.deleteArticleTags(articleId);
        if (tagIds != null && !tagIds.isEmpty()) {
            tagMapper.addArticleTags(articleId, tagIds, LocalDateTime.now());
        }
    }
}
