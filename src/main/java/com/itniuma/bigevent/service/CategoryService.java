package com.itniuma.bigevent.service;

import com.itniuma.bigevent.pojo.Category;

import java.util.List;

public interface CategoryService {
    // 添加分类
    void add(Category category);
    // 查询所有分类
    List<Category> list();
    // 根据id查询分类详情
    Category detail(Integer id);
    // 修改分类
    void update(Category category);
    // 删除分类
    void delete(Integer id);
}
