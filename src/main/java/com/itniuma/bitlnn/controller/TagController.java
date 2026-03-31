package com.itniuma.bitlnn.controller;

import com.itniuma.bitlnn.pojo.Result;
import com.itniuma.bitlnn.pojo.Tag;
import com.itniuma.bitlnn.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aceFelix
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping
    public Result add(@RequestBody @Validated(Tag.Add.class) Tag tag) {
        tagService.add(tag);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list() {
        List<Tag> list = tagService.list();
        return Result.success(list);
    }

    @GetMapping("/detail")
    public Result detail(Integer id) {
        Tag tag = tagService.detail(id);
        return Result.success(tag);
    }

    @PutMapping
    public Result update(@RequestBody @Validated(Tag.Update.class) Tag tag) {
        tagService.update(tag);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Integer id) {
        tagService.delete(id);
        return Result.success();
    }
}
