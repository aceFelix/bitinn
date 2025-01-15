package com.itniuma.bigevent.service.impl;

import com.itniuma.bigevent.mapper.CategoryMapper;
import com.itniuma.bigevent.pojo.Category;
import com.itniuma.bigevent.service.CategoryService;
import com.itniuma.bigevent.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    // 添加分类
    @Override
    public void add(Category category) {
        // 补充属性
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Map<String, Object> claims = ThreadLocalUtil.get();
        category.setCreateUser((Integer)(claims.get("id")));
        categoryMapper.add(category);
    }

    // 查询分类
    @Override
    public List<Category> list() {
        // 查询当前用户所创建的所有分类
        // 参数为当前用户的id
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        return categoryMapper.list(id);
    }

    // 根据id查询分类详情
    @Override
    public Category detail(Integer id) {
        Category category = categoryMapper.detail(id);
        return category;
    }

    // 修改分类
    @Override
    public void update(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    // 删除分类
    @Override
    public void delete(Integer id) {
        categoryMapper.delete(id);
    }
}
