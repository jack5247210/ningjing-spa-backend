package com.example.ningjingspa.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("寧境芳療 - 重設密碼通知");
        
        String content = "<div style='font-family: sans-serif; max-width: 480px; margin: auto;'>"
                + "<h2 style='color: #5D3A2E;'>寧境芳療 · 密碼重置</h2>"
                + "<p>請點擊下方按鈕重設您的密碼（30分鐘內有效）：</p>"
                + "<a href='" + resetLink + "' style='display: inline-block; padding: 12px 24px; background-color: #5D3A2E; color: white; text-decoration: none; border-radius: 30px;'>重設密碼</a>"
                + "<p style='margin-top: 24px; color: #888;'>若您未要求此郵件，請忽略。</p>"
                + "</div>";
        
        helper.setText(content, true);
        mailSender.send(message);
    }
}