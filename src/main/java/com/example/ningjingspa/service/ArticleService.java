package com.example.ningjingspa.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.ningjingspa.dao.ArticleDao;
import com.example.ningjingspa.entity.Article;
import com.example.ningjingspa.req.ArticleReq;
import com.example.ningjingspa.res.ArticleRes;

import jakarta.transaction.Transactional;
@Service
@Transactional //確保資料一致性。
public class ArticleService {

	@Autowired
	private ArticleDao articledao;
	
	/**
     * 新增文章
     * @param req 前端傳入的請求資料（不含 id、viewCount、publishDate）
     * @return 新增後的文章資料（含自動生成的 id）
     */
    public ArticleRes createArticle(ArticleReq req) {
        // 將 ArticleReq 轉換為 Article 實體
        Article article = new Article();
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        article.setSummary(req.getSummary());
        article.setArticleImg(req.getArticleImg());      // 注意：Req 中需有 imgUrl 欄位
        article.setCategory(req.getCategory());
        article.setAuthor(req.getAuthor());
        article.setPublishDate(LocalDate.now());     // 設定發布日期為當天
        article.setViewCount(0);                      // 初始瀏覽次數為 0

        // 儲存至資料庫
        Article saved = articledao.save(article);

        // 轉換為 ArticleRes 回傳
        return convertToRes(saved);
    }

    /**
     * 更新文章（根據 id）
     * @param id 文章 ID
     * @param req 更新的資料（允許部分欄位為 null 表示不更新）
     * @return 更新後的文章資料
     * @throws RuntimeException 如果文章不存在
     */
    public ArticleRes updateArticle(Integer id, ArticleReq req) {
        // 1. 先查出原有文章
        Article article = articledao.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在，ID：" + id));

        // 2. 更新有值的欄位（req 中的欄位若為 null 則忽略）
        if (req.getTitle() != null) {
            article.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            article.setContent(req.getContent());
        }
        if (req.getSummary() != null) {
            article.setSummary(req.getSummary());
        }
        if (req.getArticleImg() != null) {
            article.setArticleImg(req.getArticleImg());
        }
        if (req.getCategory() != null) {
            article.setCategory(req.getCategory());
        }
        if (req.getAuthor() != null) {
            article.setAuthor(req.getAuthor());
        }
        // 注意：publishDate 和 viewCount 通常不透過更新修改

        // 3. 儲存更新
        Article updated = articledao.save(article);
        return convertToRes(updated);
    }

    /**
     * 根據 ID 查詢文章，並增加瀏覽次數
     * @param id 文章 ID
     * @return 文章資料
     * @throws RuntimeException 如果文章不存在
     */
    @Transactional
    public ArticleRes getArticleById(Integer id) {
        Article article = articledao.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在，ID：" + id));

        // 增加瀏覽次數（使用 DAO 中原生的 incrementViewCount）
        articledao.incrementViewCount(id);

        // 重新查詢以取得更新後的 viewCount（或直接 +1 後 set，但為了精確可重新查詢）
        article = articledao.findById(id).orElse(article);
        return convertToRes(article);
    }

    /**
     * 分頁查詢所有文章（可選按分類篩選或標題搜尋）
     * @param page     頁碼（從 0 開始）
     * @param size     每頁筆數
     * @param category 分類（可為 null）
     * @param keyword  標題關鍵字（可為 null）
     * @return 分頁結果（包含文章列表、總頁數等）
     */
    public Page<ArticleRes> getArticles(int page, int size, String category, String keyword) {
        // 建立分頁請求（按發布日期倒序）
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishDate"));

        Page<Article> articlePage;
        if (category != null && !category.isEmpty()) {
            // 使用 DAO 中的 findByCategory（需修正為支援分頁）
            // 注意：你目前的 findByCategory 是 nativeQuery 且回傳 Page<?> 但可能不支援分頁
            // 建議改用 Spring Data 命名方法：findByCategory(String category, Pageable pageable)
            // 或者修改 DAO 中的方法為 JPQL 以支援分頁
            // 此處先假設你有一個正確的 findByCategory 方法
            articlePage = articledao.findByCategory(category, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            articlePage = articledao.findByTitleContaining(keyword, pageable);
        } else {
            articlePage = articledao.findAll(pageable);
        }

        // 將 Page<Article> 轉換為 Page<ArticleRes>
        return articlePage.map(this::convertToRes);
    }

    /**
     * 刪除文章
     * @param id 文章 ID
     */
    public void deleteArticle(Integer id) {
        if (!articledao.existsById(id)) {
            throw new RuntimeException("文章不存在，無法刪除，ID：" + id);
        }
        articledao.deleteById(id);
    }

    /**
     * 取得最新發布的幾篇文章（用於首頁展示）
     * @param limit 要取得的篇數
     * @return 文章列表（不包含分頁資訊）
     */
    public List<ArticleRes> getLatestArticles(int limit) {
        List<Article> articles = articledao.findLatestArticles(limit);
        return articles.stream()
                .map(this::convertToRes)
                .collect(Collectors.toList());
    }

    // ========== 輔助方法：將 Article 實體轉換為 ArticleRes ==========
    private ArticleRes convertToRes(Article article) {
        ArticleRes res = new ArticleRes();
        res.setArticleId(article.getArticleId());
        res.setTitle(article.getTitle());
        res.setContent(article.getContent());
        res.setSummary(article.getSummary());
        res.setArticleImg(article.getArticleImg());   // 注意實體中為 articleImg
        res.setCategory(article.getCategory());
        res.setAuthor(article.getAuthor());
        res.setPublishDate(article.getPublishDate());
        res.setViewCount(article.getViewCount());
        return res;
    }
    
    public List<String> getAllCategories() {
        return articledao.findDistinctCategories();
    }
    
    
}
