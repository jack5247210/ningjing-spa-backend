package com.example.ningjingspa.controller;

import com.example.ningjingspa.dao.AppointmentDao;
import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.Appointment;
import com.example.ningjingspa.entity.User;
import com.example.ningjingspa.service.LineMessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.webhook.model.CallbackRequest;
import com.linecorp.bot.webhook.model.Event;
import com.linecorp.bot.webhook.model.FollowEvent;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/webhook")
public class LineWebhookController {

    @Value("${line.bot.channel-secret}")
    private String channelSecret;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LineMessagingService lineMessagingService;

    @Autowired   // ✅ 新增：用來查詢使用者的已確認預約
    private AppointmentDao appointmentDao;

    private final ObjectMapper objectMapper;

    public LineWebhookController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> handleCallback(
            @RequestBody String rawBody,
            HttpServletRequest request) {

        String signature = request.getHeader("x-line-signature");
        if (signature == null || !isValidSignature(rawBody, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            CallbackRequest callbackRequest = objectMapper.readValue(rawBody, CallbackRequest.class);

            for (Event event : callbackRequest.events()) {
                if (event instanceof MessageEvent messageEvent) {
                    if (messageEvent.message() instanceof TextMessageContent textContent) {
                        String userId = messageEvent.source().userId();
                        String text = textContent.text().trim();

                        // 嘗試使用電子郵件綁定
                        if (text.contains("@") && text.contains(".")) {
                            String email = text;
                            if (email.startsWith("綁定 ")) {
                                email = email.substring(3).trim();
                            }
                            User user = userDao.getByEmail(email);
                            if (user != null) {
                                user.setLineUserId(userId);
                                userDao.save(user);
                                lineMessagingService.pushMessage(userId, "✅ 綁定成功！您將會在預約前一天收到 LINE 提醒。");

                                // ✅ 綁定成功後，立即檢查該使用者「明天～後天」的已確認預約
                                sendFutureAppointmentReminders(user, userId);
                            } else {
                                lineMessagingService.pushMessage(userId, "❌ 找不到該 Email 的註冊帳號，請檢查後重新輸入。");
                            }
                        }
                    }
                } else if (event instanceof FollowEvent followEvent) {
                    String userId = followEvent.source().userId();
                    lineMessagingService.pushMessage(userId, 
                        "🌸 歡迎關注寧境芳療！\n" +
                        "請在對話框直接發送您的 Email，\n" +
                        "系統將自動為您開啟預約提醒。\n" +
                        "（例如：joyce@gmail.com）");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Parse error");
        }

        return ResponseEntity.ok("OK");
    }

    /**
     * 發送使用者未來三天（明、後天）的已確認預約提醒
     */
    private void sendFutureAppointmentReminders(User user, String lineUserId) {
        // 計算時間範圍：明天 00:00 ～ 大後天 00:00（覆蓋明天、後天）
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.plusDays(3).atStartOfDay();

        // 查詢該使用者所有已確認的預約，並過濾出未來範圍內的
        List<Appointment> confirmedAppointments = appointmentDao
                .findByUserUserIdAndStatusWithProduct(user.getUserId(), "confirmed");

        for (Appointment apt : confirmedAppointments) {
            if (apt.getAppointmentTime().isAfter(start) && apt.getAppointmentTime().isBefore(end)) {
                String reminder = String.format(
                    "【寧境 · 預約提醒】\n" +
                    "%s 您好，\n" +
                    "您已預約的「%s」療程將於 %s 進行，\n" +
                    "請準時抵達工作室，期待為您服務。",
                    user.getName(),
                    apt.getProduct().getTitle(),
                    apt.getAppointmentTime().toLocalDate().toString() + " " + apt.getAppointmentTime().toLocalTime().toString()
                );
                lineMessagingService.pushMessage(lineUserId, reminder);
            }
        }
    }

    private boolean isValidSignature(String body, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(channelSecret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(body.getBytes());
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}