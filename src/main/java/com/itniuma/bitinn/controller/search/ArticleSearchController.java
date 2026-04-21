package com.itniuma.bitinn.controller.search;

import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.search.ArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class ArticleSearchController {

    @Autowired(required = false)
    private ArticleSearchService searchService;

    @GetMapping
    public Result search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "relevant") String sortType
    ) {
        if (searchService == null) {
            return Result.error("搜索服务暂未启用，请先安装并配置 Elasticsearch");
        }
        return searchService.search(keyword, pageNum, pageSize, sortType);
    }

    @GetMapping("/suggest")
    public Result suggest(@RequestParam String prefix) {
        if (searchService == null) {
            return Result.error("搜索服务暂未启用，请先安装并配置 Elasticsearch");
        }
        return searchService.suggest(prefix);
    }

    @GetMapping("/hot-keywords")
    public Result hotKeywords(@RequestParam(required = false, defaultValue = "10") Integer topN) {
        if (searchService == null) {
            return Result.error("搜索服务暂未启用，请先安装并配置 Elasticsearch");
        }
        return searchService.getHotKeywords(topN);
    }
}
