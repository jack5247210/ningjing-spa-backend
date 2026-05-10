package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ningjingspa.entity.Comment;

public interface CommentDao extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.article.articleId = :articleId ORDER BY c.commentAt DESC")
    List<Comment> findByArticle_ArticleIdOrderByCreatedAtDesc(@Param("articleId") Integer articleId);
}