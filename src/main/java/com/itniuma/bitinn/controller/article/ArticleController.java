package com.itniuma.bitinn.controller.article;

import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.pojo.PageBean;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.article.ArticleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public Result add(@RequestBody Article article) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            return Result.error("文章标题不能为空");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            return Result.error("文章内容不能为空");
        }
        return articleService.add(article);
    }

    @PutMapping
    public Result update(@RequestBody Article article) {
        if (article.getId() == null) {
            return Result.error("文章ID不能为空");
        }
        return articleService.update(article);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return articleService.delete(id);
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Integer id) {
        return articleService.detail(id);
    }

    @GetMapping
    public Result<PageBean<Article>> list(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "已发布") String state,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return articleService.list(categoryId, state, pageNum, pageSize);
    }

    @GetMapping("/my")
    public Result<PageBean<Article>> myArticles(
            @RequestParam(required = false) String state,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return articleService.myArticles(state, pageNum, pageSize);
    }

    @GetMapping("/feed")
    public Result<PageBean<Article>> feed(
            @RequestParam(required = false, defaultValue = "recommend") String sortType,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return articleService.feed(sortType, pageNum, pageSize);
    }
}
