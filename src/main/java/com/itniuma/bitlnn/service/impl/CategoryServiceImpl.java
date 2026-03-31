package com.itniuma.bitlnn.service.impl;

import com.itniuma.bitlnn.mapper.CategoryMapper;
import com.itniuma.bitlnn.pojo.Category;
import com.itniuma.bitlnn.service.CategoryService;
import com.itniuma.bitlnn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章分类服务实现
 *
 * @author aceFelix
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    private Integer getCurrentUserId() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        return (Integer) claims.get("id");
    }

    @Override
    public void add(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(getCurrentUserId());
        categoryMapper.add(category);
    }

    @Override
    public List<Category> list() {
        return categoryMapper.list(getCurrentUserId());
    }

    @Override
    public Category detail(Integer id) {
        Category category = categoryMapper.detail(id, getCurrentUserId());
        if (category == null) {
            throw new RuntimeException("分类不存在或无权限访问");
        }
        return category;
    }

    @Override
    public void update(Category category) {
        category.setCreateUser(getCurrentUserId());
        category.setUpdateTime(LocalDateTime.now());
        if (categoryMapper.update(category) == 0) {
            throw new RuntimeException("分类不存在或无权限修改");
        }
    }

    @Override
    public void delete(Integer id) {
        if (categoryMapper.delete(id, getCurrentUserId()) == 0) {
            throw new RuntimeException("分类不存在或无权限删除");
        }
    }
}
