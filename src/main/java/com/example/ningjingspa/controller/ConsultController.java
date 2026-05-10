package com.example.ningjingspa.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ningjingspa.dao.ConsultDao;
import com.example.ningjingspa.entity.Consult;
import com.example.ningjingspa.req.ConsultReq;
import com.example.ningjingspa.req.RegisterReq;
import com.example.ningjingspa.res.BasicRes;
import com.example.ningjingspa.res.ConsultRes;
import com.example.ningjingspa.res.ProductRes;
import com.example.ningjingspa.service.ConsultService;
import com.example.ningjingspa.service.UserService;

@RestController
@RequestMapping("/api/consult")
public class ConsultController {

	@Autowired
	private ConsultDao consultdao;
	
	@Autowired
	private ConsultService consultservice;
	
	@PostMapping("/recommend")
	public ResponseEntity<ProductRes> recommend(@RequestBody ConsultReq req) {
	    ProductRes result = consultservice.recommendProduct(req);
	    return ResponseEntity.ok(result);
	}
	
	/**
     * 儲存 AI 諮詢紀錄
     */
    public ConsultRes saveConsult(ConsultReq req) {
        Consult consult = new Consult();
        consult.setUserId(req.getUserId());
        consult.setTags(req.getTags());
        consult.setProductId(req.getProductId());
        consult.setAiReason(req.getAiReason());
        consult.setCreatedDate(LocalDateTime.now());

        Consult saved = consultdao.save(consult);
        return convertToRes(saved);
    }

    /**
     * 根據 ID 查詢單筆諮詢紀錄
     * @param id 諮詢紀錄 ID
     * @return 諮詢紀錄資料
     * @throws RuntimeException 如果紀錄不存在
     */
    public ConsultRes getConsultById(Integer id) {
        Consult consult = consultdao.findById(id)
                .orElseThrow(() -> new RuntimeException("諮詢紀錄不存在，ID：" + id));
        return convertToRes(consult);
    }

    /**
     * 根據使用者 ID 查詢所有諮詢紀錄
     * @param userId 使用者 ID
     * @return 該使用者的所有諮詢紀錄列表
     */
    public List<ConsultRes> getConsultsByUserId(Integer userId) {
        return consultdao.findByUserId(userId)
                .stream()
                .map(this::convertToRes)
                .collect(Collectors.toList());
    }

    /**
     * 刪除諮詢紀錄
     * @param id 諮詢紀錄 ID
     * @throws RuntimeException 如果紀錄不存在
     */
    public void deleteConsult(Integer id) {
        if (!consultdao.existsById(id)) {
            throw new RuntimeException("諮詢紀錄不存在，無法刪除，ID：" + id);
        }
        consultdao.deleteById(id);
    }

    /**
     * 轉換邏輯：Entity -> ConsultRes
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
    
    
}
