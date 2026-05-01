package com.itniuma.bitinn.controller.interaction;

import com.itniuma.bitinn.pojo.Comment;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.interaction.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/like/{articleId}")
    public Result toggleLike(@PathVariable Integer articleId) {
        return interactionService.toggleLike(articleId);
    }

    @GetMapping("/like/status/{articleId}")
    public Result isLiked(@PathVariable Integer articleId) {
        return interactionService.isLiked(articleId);
    }

    @PostMapping("/favorite/{articleId}")
    public Result toggleFavorite(@PathVariable Integer articleId) {
        return interactionService.toggleFavorite(articleId);
    }

    @GetMapping("/favorite/status/{articleId}")
    public Result isFavorited(@PathVariable Integer articleId) {
        return interactionService.isFavorited(articleId);
    }

    @PostMapping("/follow/{userId}")
    public Result toggleFollow(@PathVariable Integer userId) {
        return interactionService.toggleFollow(userId);
    }

    @GetMapping("/follow/status/{userId}")
    public Result isFollowed(@PathVariable Integer userId) {
        return interactionService.isFollowed(userId);
    }

    @PostMapping("/comment")
    public Result addComment(@RequestBody Comment comment) {
        return interactionService.addComment(comment);
    }

    @DeleteMapping("/comment/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        return interactionService.deleteComment(id);
    }

    @GetMapping("/comment/{articleId}")
    public Result getComments(@PathVariable Integer articleId) {
        return interactionService.getComments(articleId);
    }

    @GetMapping("/liked-articles")
    public Result getLikedArticles() {
        return interactionService.getLikedArticles();
    }

    @GetMapping("/favorited-articles")
    public Result getFavoritedArticles() {
        return interactionService.getFavoritedArticles();
    }

    @GetMapping("/following-users")
    public Result getFollowingUsers() {
        return interactionService.getFollowingUsers();
    }

    @GetMapping("/article-status/{articleId}")
    public Result getArticleInteractionStatus(@PathVariable Integer articleId) {
        return interactionService.getArticleInteractionStatus(articleId);
    }

    @PostMapping("/batch-status")
    public Result getBatchArticleInteractionStatus(@RequestBody List<Integer> articleIds) {
        return interactionService.getBatchArticleInteractionStatus(articleIds);
    }

    @PostMapping("/share/{articleId}")
    public Result shareArticle(@PathVariable Integer articleId) {
        return interactionService.shareArticle(articleId);
    }
}
