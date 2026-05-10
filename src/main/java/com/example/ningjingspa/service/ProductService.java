package com.example.ningjingspa.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ningjingspa.dao.ProductDao;
import com.example.ningjingspa.entity.Product;
import com.example.ningjingspa.req.ProductReq;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Product createProduct(ProductReq req) {
        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setIntroduce(req.getIntroduce());   // 新增
        product.setDuration(req.getDuration());
        product.setPrice(req.getPrice());
        product.setProductImg(req.getProductImg());
        product.setIsVisible(true);
        return productDao.save(product);
    }

    public Product updateProduct(Integer id, ProductReq req) {
        Product existing = productDao.findById(id)
            .orElseThrow(() -> new RuntimeException("產品不存在"));
        if (req.getTitle() != null) existing.setTitle(req.getTitle());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getIntroduce() != null) existing.setIntroduce(req.getIntroduce()); // 新增
        if (req.getDuration() > 0) existing.setDuration(req.getDuration());
        if (req.getPrice() > 0) existing.setPrice(req.getPrice());
        if (req.getProductImg() != null) existing.setProductImg(req.getProductImg());
        return productDao.save(existing);
    }

    public Product getProductById(Integer id) {
        return productDao.findById(id)
            .orElseThrow(() -> new RuntimeException("產品不存在"));
    }

    public List<Product> getAllProductsForAdmin() {
        return productDao.findAll();
    }

    public List<Product> getVisibleProducts() {
        return productDao.findByIsVisibleTrue();
    }

    public void deleteProduct(Integer id) {
        productDao.deleteById(id);
    }

    public void toggleVisibility(Integer id) {
        Product product = productDao.findById(id)
            .orElseThrow(() -> new RuntimeException("產品不存在"));
        product.setIsVisible(!product.getIsVisible());
        productDao.save(product);
    }
}