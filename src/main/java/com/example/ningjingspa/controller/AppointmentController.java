package com.example.ningjingspa.controller;

import com.example.ningjingspa.entity.Holiday;
import com.example.ningjingspa.req.AppointmentReq;
import com.example.ningjingspa.res.AppointmentRes;
import com.example.ningjingspa.service.AppointmentService;
import com.example.ningjingspa.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HolidayService holidayService;

    // 管理者：取得所有預約
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AppointmentRes>> getAllAppointments() {
        List<AppointmentRes> res = appointmentService.getAllAppointments();
        return ResponseEntity.ok(res);
    }

    // 使用者：新增預約
    @PostMapping
    public ResponseEntity<AppointmentRes> create(@RequestBody AppointmentReq req) {
        AppointmentRes res = appointmentService.create(req);
        return ResponseEntity.ok(res);
    }

 // 使用者：查詢自己的預約
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentRes>> getByUser(@PathVariable("userId") Integer userId) { // <--- 加上 ("userId")
        return ResponseEntity.ok(appointmentService.getByUser(userId));
    }

 // 使用者：取消預約
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> cancel(
            @PathVariable("appointmentId") Integer appointmentId,   // 加上 ("appointmentId")
            @RequestParam("userId") Integer userId                  // 加上 ("userId")
    ) {
        appointmentService.cancel(appointmentId, userId);
        return ResponseEntity.noContent().build();
    }

    // 取得日期區間內每天的剩餘名額（排除公休日）
    @GetMapping("/availability")
    public ResponseEntity<Map<LocalDate, Integer>> getAvailability(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        Map<LocalDate, Integer> counts = appointmentService.getDailyCounts(start, end);
        Map<LocalDate, Integer> available = new HashMap<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (holidayService.isHoliday(date)) {
                available.put(date, 0);          // 公休日不可預約
            } else {
                int booked = counts.getOrDefault(date, 0);
                available.put(date, 2 - booked); // 每日最多 2 人
            }
        }
        return ResponseEntity.ok(available);
    }

    @PutMapping("/admin/{appointmentId}/confirm")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> confirmAppointment(@PathVariable("appointmentId") Integer appointmentId) {
        System.out.println(">>> 确认预约 ID: " + appointmentId);
        appointmentService.confirmAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/{appointmentId}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> rejectAppointment(@PathVariable("appointmentId") Integer appointmentId) {
        System.out.println(">>> 拒绝预约 ID: " + appointmentId);
        appointmentService.rejectAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }

    // 管理者：新增公休日
    @PostMapping("/admin/holidays")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Holiday> addHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(holidayService.addHoliday(holiday));
    }

    // 管理者：刪除公休日
    @DeleteMapping("/admin/holidays/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Integer id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }

    // 取得所有公休日（前端用於顯示）
    @GetMapping("/holidays")
    public ResponseEntity<List<Holiday>> getHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }
    
    
}