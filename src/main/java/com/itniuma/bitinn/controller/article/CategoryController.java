package com.itniuma.bitinn.controller;

import com.itniuma.bitinn.pojo.Category;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return categoryService.list();
    }
}
