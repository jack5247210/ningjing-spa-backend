package com.example.ningjingspa.service;

import com.example.ningjingspa.dao.AppointmentDao;
import com.example.ningjingspa.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderService {

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private LineMessagingService lineMessagingService;

    /**
     * 每天早上 10:00 執行
     * 查詢「明天、後天、大後天」的已確認預約，並發送 LINE 提醒
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendAppointmentReminders() {
        // ✅ 計算時間範圍
        LocalDate tomorrow = LocalDate.now().plusDays(1);               // 明天
        LocalDateTime start = tomorrow.atStartOfDay();                  // 明天 00:00
        LocalDateTime end = tomorrow.plusDays(3).atStartOfDay();       // 大後天 00:00

        System.out.println("⏰ 定時任務觸發，查詢 " + tomorrow + " 至 " + tomorrow.plusDays(2) + " 的已確認預約");

        List<Appointment> confirmedAppointments = appointmentDao.findByStatusAndAppointmentTimeBetween(
                "confirmed", start, end);

        System.out.println("📋 找到 " + confirmedAppointments.size() + " 條預約");

        for (Appointment apt : confirmedAppointments) {
            String lineUserId = apt.getUser().getLineUserId();
            System.out.println("👤 用戶: " + apt.getUser().getName() + ", LINE ID: " + lineUserId);

            if (lineUserId != null && !lineUserId.isEmpty()) {
                String message = buildReminderMessage(apt);
                System.out.println("📨 準備推送: " + message);
                lineMessagingService.pushMessage(lineUserId, message);
                System.out.println("✅ 推送完成");
            } else {
                System.out.println("⚠️ 用戶 " + apt.getUser().getName() + " 未綁定 LINE");
            }
        }
    }

    /**
     * 建立提醒訊息內容
     */
    private String buildReminderMessage(Appointment apt) {
        return String.format(
            "【寧境 · 預約提醒】\n" +
            "%s 您好，\n" +
            "您預約的「%s」療程將於 %s 進行，\n" +
            "請準時抵達工作室，期待為您服務。\n" +
            "若有任何問題，請來電或 LINE 洽詢。",
            apt.getUser().getName(),
            apt.getProduct().getTitle(),
            apt.getAppointmentTime().toLocalDate().toString() + " " + apt.getAppointmentTime().toLocalTime().toString()
        );
    }
}