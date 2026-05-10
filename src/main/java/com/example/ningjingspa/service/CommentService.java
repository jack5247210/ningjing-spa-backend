package com.example.ningjingspa.service;

import com.example.ningjingspa.dao.ArticleDao;
import com.example.ningjingspa.dao.CommentDao;
import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.Article;
import com.example.ningjingspa.entity.Comment;
import com.example.ningjingspa.entity.User;
import com.example.ningjingspa.req.CommentReq;
import com.example.ningjingspa.res.CommentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDao commentdao;

    @Autowired
    private ArticleDao articledao;

    @Autowired
    private UserDao userdao;  // 需要注入 UserDao

    public CommentRes addComment(CommentReq req) {
    	System.out.println("收到留言請求: articleId=" + req.getArticleId() + ", userId=" + req.getUserId() + ", content=" + req.getContent());
        Article article = articledao.findById(req.getArticleId())
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        User user = userdao.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setContent(req.getContent());
        comment.setCommentAt(LocalDateTime.now());

        Comment saved = commentdao.save(comment);
        return convertToRes(saved);
    }

    public List<CommentRes> getCommentsByArticle(Integer articleId) {
        List<Comment> comments = commentdao.findByArticle_ArticleIdOrderByCreatedAtDesc(articleId);
        return comments.stream()
                .map(this::convertToRes)
                .collect(Collectors.toList());
    }

    private CommentRes convertToRes(Comment comment) {
        CommentRes res = new CommentRes();
        res.setCommentId(comment.getCommentId());
        res.setArticleId(comment.getArticle().getArticleId());
        res.setUserId(comment.getUser().getUserId());
        res.setUserName(comment.getUser().getName());  // 從關聯的 User 取得名稱
        res.setContent(comment.getContent());
        res.setCommentAt(comment.getCommentAt());
        return res;
    }
}