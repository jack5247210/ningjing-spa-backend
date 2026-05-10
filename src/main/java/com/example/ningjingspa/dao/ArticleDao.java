package com.example.ningjingspa.dao;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ningjingspa.entity.Article;

import jakarta.transaction.Transactional;

@Repository
public interface ArticleDao  extends JpaRepository<Article, Integer> {

	// 1. 分頁查詢文章 (給前端 list-article 使用)
    // JpaRepository 內建支援 Pageable，不用寫 SQL 就能實現分頁
    Page<Article> findAll(Pageable pageable);

    // 2. 根據標題模糊搜尋 (搜尋功能)
    @Query(value = "SELECT * FROM article WHERE title LIKE %?1%",
    	       countQuery = "SELECT COUNT(*) FROM article WHERE title LIKE %?1%",
    	       nativeQuery = true)
    	Page<Article> findByTitleContaining(String title, Pageable pageable);

    // 3. 根據分類搜尋
    @Query("SELECT a FROM Article a WHERE a.category = :category")
    Page<Article> findByCategory(@Param("category") String category, Pageable pageable);

    // 4. 增加瀏覽次數 (每點擊一次閱讀更多就 +1)
    @Modifying
    @Transactional
    @Query(value = "UPDATE article SET view_count = view_count + 1 WHERE article_id = ?1", nativeQuery = true)
    void incrementViewCount(int articleId);

    // 5. 取得最新發布的文章 (例如首頁要顯示最新的 3 篇)
    @Query(value = "SELECT * FROM article ORDER BY publish_date DESC LIMIT ?1", nativeQuery = true)
    java.util.List<Article> findLatestArticles(int limit);
    
    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.category IS NOT NULL AND a.category != '' ORDER BY a.category")
    List<String> findDistinctCategories();
}
