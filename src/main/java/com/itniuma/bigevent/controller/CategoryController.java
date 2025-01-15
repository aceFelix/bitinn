package com.itniuma.bigevent.controller;

import com.itniuma.bigevent.pojo.Category;
import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
     private CategoryService categoryService;

    /**
     *  添加分类
     * @param category
     * @return result
     */
    @PostMapping
    public Result add(@RequestBody @Validated(Category.Add.class) Category category){
        categoryService.add(category);
        return Result.success();
    }

    /**
     * 查询所有分类
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>>list(){
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    /**
     * 根据id查询分类详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public Result<Category> detail(Integer id){
        Category category = categoryService.detail(id);
        return Result.success(category);
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public Result update(@RequestBody @Validated(Category.Update.class) Category category){
        categoryService.update(category);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result delete(Integer id){
        categoryService.delete(id);
        return Result.success();
    }
}
