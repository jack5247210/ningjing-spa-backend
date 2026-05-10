package com.example.ningjingspa.controller;

import com.example.ningjingspa.entity.Holiday;
import com.example.ningjingspa.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    // 取得所有公休日（公開，前端日曆用）
    @GetMapping
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    // 管理者：新增公休日
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Holiday> addHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(holidayService.addHoliday(holiday));
    }

    // 管理者：刪除公休日
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteHoliday(@PathVariable("id") Integer id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}