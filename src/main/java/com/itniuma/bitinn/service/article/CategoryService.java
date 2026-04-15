package com.itniuma.bitinn.service.article;

import com.itniuma.bitinn.pojo.Category;
import com.itniuma.bitinn.pojo.Result;

import java.util.List;

public interface CategoryService {

    Result<List<Category>> list();
}
