package com.itniuma.bigevent.controller;

import com.itniuma.bigevent.pojo.Article;
import com.itniuma.bigevent.pojo.Category;
import com.itniuma.bigevent.pojo.PageBean;
import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 添加文章
     * @param article
     * @return
     */
    @PostMapping
    public Result add(@RequestBody @Validated Article article){
        articleService.add(article);
        return Result.success();
    }

    /**
     * 文章列表（条件分页）
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param state
     * @return 分页数据
     */
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    ){
       PageBean<Article> pageBean = articleService.list(pageNum,pageSize,categoryId,state);
        return Result.success(pageBean);
    }

    /**
     * 文章详情
     * @param id
     * @return 文章详情
     */
    @GetMapping("/detail")
    public Result<Article> detail(Integer id){
        Article article = articleService.detail(id);
        return Result.success(article);
    }

    /**
     * 修改文章
     * @param article
     * @return
     */
    @PutMapping
    public Result update(@RequestBody @Validated Article article){
        articleService.update(article);
        return Result.success();
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @DeleteMapping
    public Result delete(Integer id){
        articleService.delete(id);
        return Result.success();
    }





    // @GetMapping("/list")
    public Result list2(/*@RequestHeader(name = "Authorization") String token, HttpServletResponse response*/){
        // 验证token
        /*try {
            Map<String, Object> calims = JwtUtil.parseToken(token);
            return Result.success("所有的文章数据...");
        } catch (Exception e) {
            // 非法令牌
            // 设置http相应状态码为401
            response.setStatus(401);
            return Result.error("未登录");
        }*/
        return Result.success("所有的文章数据...");
    }
}
