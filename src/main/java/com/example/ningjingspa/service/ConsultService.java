package com.example.ningjingspa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ningjingspa.dao.ConsultDao;
import com.example.ningjingspa.dao.ProductDao;
import com.example.ningjingspa.entity.Consult;
import com.example.ningjingspa.entity.Product;
import com.example.ningjingspa.req.ConsultReq;
import com.example.ningjingspa.res.ConsultRes;
import com.example.ningjingspa.res.ProductRes;



@Service
public class ConsultService {

    @Autowired
    private ConsultDao consultdao; // 注入改名後的 Repository
    
    @Autowired
    private ProductDao productdao;

    /**
     * 儲存 AI 諮詢紀錄
     */
    public ConsultRes saveConsult(ConsultReq req) {
        // 1. 實例化改名後的實體類別，現在不會報錯了！
        Consult consult = new Consult(); 

        // 2. 設定資料 (確保 Req 的 Getter 回傳的是 Integer)
        consult.setUserId(req.getUserId());     
        consult.setTags(req.getTags());
        consult.setProductId(req.getProductId()); 
        consult.setAiReason(req.getAiReason());
        
        // 確保對齊 SQL 的 created_at
        consult.setCreatedDate(LocalDateTime.now()); 

        // 3. 執行存檔
        Consult savedConsult = consultdao.save(consult);

        // 4. 轉換成 Res 回傳
        return convertToRes(savedConsult);
    }

    /**
     * 根據使用者 ID 取得歷史紀錄
     */
    public List<ConsultRes> getHistory(Integer userId) {
        return consultdao.findByUserId(userId)
                .stream()
                .map(this::convertToRes)
                .collect(Collectors.toList());
    }

    /**
     * 轉換邏輯：Entity -> RecordRes
     */
    private ConsultRes convertToRes(Consult consult) {
    	ConsultRes res = new ConsultRes();
        res.setConsultId(consult.getConsultId());
        res.setUserId(consult.getUserId());
        res.setTags(consult.getTags());
        res.setProductId(consult.getProductId());
        res.setAiReason(consult.getAiReason());
        res.setCreatedDate(consult.getCreatedDate());
        return res;
    }
    
    public ProductRes recommendProduct(ConsultReq req) {
        // 1. 根據 tags 搜尋產品（關鍵字比對）
        List<Product> candidates = productdao.findByTitleContainingOrDescriptionContaining(req.getTags());
        if (candidates.isEmpty()) {
            throw new RuntimeException("沒有找到符合的產品");
        }
        
        // 2. 選第一個（可依需求改為隨機或最相關）
        Product product = candidates.get(0);
        
        // 3. 產生理由
        String reason = "根據您選擇的「" + req.getTags() + "」，我們推薦「" + product.getTitle() + "」療程。";
        
        // 4. 儲存諮詢紀錄
        Consult consult = new Consult();
        consult.setUserId(req.getUserId());
        consult.setTags(req.getTags());
        consult.setProductId(product.getProductId());
        consult.setAiReason(reason);
        consult.setCreatedDate(LocalDateTime.now());
        consultdao.save(consult);
        
        // 5. 回傳產品資訊
        return convertToProductRes(product);
    }

    private ProductRes convertToProductRes(Product product) {
        ProductRes res = new ProductRes();
        res.setProductId(product.getProductId());
        res.setTitle(product.getTitle());
        res.setDescription(product.getDescription());
        res.setDuration(product.getDuration());
        res.setPrice(product.getPrice());
        res.setProductImg(product.getProductImg());
        return res;
    }
}
