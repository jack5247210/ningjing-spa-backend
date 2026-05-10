package com.example.ningjingspa.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.example.ningjingspa.entity.Product;
import com.example.ningjingspa.req.ProductReq;
import com.example.ningjingspa.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody ProductReq req) {
        Product saved = productService.createProduct(req);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable("id") Integer id, @RequestBody ProductReq req) {
        Product updated = productService.updateProduct(id, req);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable("id") Integer id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(@RequestParam(value = "admin", defaultValue = "false") boolean admin) {
        List<Product> products;
        if (admin) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
                throw new AccessDeniedException("需要管理员权限");
            }
            products = productService.getAllProductsForAdmin();
        } else {
            products = productService.getVisibleProducts();
        }
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle-visibility")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> toggleVisibility(@PathVariable("id") Integer id) {
        productService.toggleVisibility(id);
        return ResponseEntity.ok().build();
    }

    @Value("${app.upload.local-dir}")
    private String localUploadDir;

    @PostMapping("/upload-image")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "只允许上传图片文件"));
            }

            // 生成唯一文件名：使用原始文件名（可加上时间戳防重名）
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = originalFilename;  // 直接使用原始文件名

            // 存储到 public/assets 目录
            Path uploadPath = Paths.get(localUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回纯文件名
            return ResponseEntity.ok(Map.of("url", newFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "文件上传失败"));
        }
    }
}