package com.itniuma.bitinn.service.impl;

import com.itniuma.bitinn.mapper.CategoryMapper;
import com.itniuma.bitinn.pojo.Category;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Result<List<Category>> list() {
        List<Category> categories = categoryMapper.list();
        return Result.success(categories);
    }
}
