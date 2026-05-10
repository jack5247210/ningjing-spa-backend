package com.example.ningjingspa.controller;

import com.example.ningjingspa.entity.EssentialOil;
import com.example.ningjingspa.service.OilShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/oils")
public class OilAdminController {

    @Autowired
    private OilShopService oilShopService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.upload.local-dir}")
    private String localUploadDir; // 新增：本地上传目录，例如 "public/assets"

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<EssentialOil>> getAll(@RequestParam(name = "category", required = false) String category) {
        try {
            List<EssentialOil> oils;
            if (category != null && !category.isEmpty()) {
                oils = oilShopService.getOilsByCategory(category);
            } else {
                oils = oilShopService.getAllOilsForAdmin();
            }
            return ResponseEntity.ok(oils);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EssentialOil> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(oilShopService.getOilById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EssentialOil> create(@RequestBody EssentialOil oil) {
        return ResponseEntity.ok(oilShopService.createOil(oil));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EssentialOil> update(@PathVariable Integer id, @RequestBody EssentialOil oil) {
        return ResponseEntity.ok(oilShopService.updateOil(id, oil));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        oilShopService.deleteOil(id);
        return ResponseEntity.noContent().build();
    }

    // ========== 图片上传接口（存储到 public/assets，返回纯文件名） ==========
    @PostMapping("/upload-image")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "只允许上传图片文件"));
            }

            String originalFilename = file.getOriginalFilename();
            // 保留原始扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 使用原始文件名（或加时间戳避免重名），这里简单保留原文件名，确保唯一性可自行调整
            String newFileName = originalFilename; 
            // 如果希望加上简短随机字符防止覆盖：
            // newFileName = UUID.randomUUID().toString().substring(0,8) + "_" + originalFilename;

            // 存储到配置的本地目录（例如 public/assets）
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