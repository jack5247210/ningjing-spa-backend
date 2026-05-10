package com.example.ningjingspa.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ningjingspa.req.ArticleReq;
import com.example.ningjingspa.res.ArticleRes;
import com.example.ningjingspa.service.ArticleService;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ArticleRes> create(@RequestBody @Validated ArticleReq req) {
        ArticleRes res = articleService.createArticle(req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleRes> getById(@PathVariable(value = "id") Integer id) {
        ArticleRes res = articleService.getArticleById(id);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<Page<ArticleRes>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Page<ArticleRes> res = articleService.getArticles(page, size, category, keyword);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ArticleRes> update(@PathVariable(value = "id") Integer id,
                                             @RequestBody ArticleReq req) {
        ArticleRes res = articleService.updateArticle(id, req);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Integer id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ArticleRes>> getLatest(
            @RequestParam(value = "limit", defaultValue = "3") int limit) {
        List<ArticleRes> res = articleService.getLatestArticles(limit);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = articleService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Value("${app.upload.local-dir}")
    private String localUploadDir;

    @PostMapping("/upload-images")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "只允许上传图片文件"));
                }

                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = originalFilename;  // 直接使用原始文件名

                Path uploadPath = Paths.get(localUploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                fileNames.add(newFileName);
            }

            // 返回逗号分隔的文件名列表
            return ResponseEntity.ok(Map.of("urls", fileNames));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "文件上传失败"));
        }
    }
}