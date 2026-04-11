package com.itniuma.bitinn.mapper;

import com.itniuma.bitinn.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> list();

    Category findById(Integer id);
}
