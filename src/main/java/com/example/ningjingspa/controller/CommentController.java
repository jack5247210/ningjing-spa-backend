package com.example.ningjingspa.controller;

import com.example.ningjingspa.req.CommentReq;
import com.example.ningjingspa.res.CommentRes;
import com.example.ningjingspa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 新增留言
    @PostMapping
    public ResponseEntity<CommentRes> addComment(@RequestBody CommentReq req) {
        CommentRes res = commentService.addComment(req);
        return ResponseEntity.ok(res);
    }

    // 取得某篇文章的所有留言
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentRes>> getCommentsByArticle(@PathVariable("articleId") Integer articleId) {
        List<CommentRes> res = commentService.getCommentsByArticle(articleId);
        return ResponseEntity.ok(res);
    }
}