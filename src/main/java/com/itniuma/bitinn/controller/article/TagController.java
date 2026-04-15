package com.itniuma.bitinn.controller;

import com.itniuma.bitinn.mapper.TagMapper;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagMapper tagMapper;

    public TagController(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagMapper.list());
    }
}
