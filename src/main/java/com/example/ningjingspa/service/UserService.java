package com.example.ningjingspa.service;

import com.example.ningjingspa.constants.ReplyMessage;
import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.User;
import com.example.ningjingspa.req.LoginReq;
import com.example.ningjingspa.req.RegisterReq;
import com.example.ningjingspa.res.BasicRes;
import com.example.ningjingspa.res.LoginRes;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class UserService {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private MailService mailService;

    // 注册（含 email 正规化）
    public BasicRes register(RegisterReq req) {
        // 1. email 正规化
        String rawEmail = req.getEmail();
        if (!StringUtils.hasText(rawEmail)) {
            return new BasicRes(ReplyMessage.PARAM_ERROR.getCode(), "請填寫電子郵件");
        }
        String normalizedEmail = rawEmail.trim().toLowerCase();

        // 2. 基本参数检查
        if (!StringUtils.hasText(req.getName()) || !StringUtils.hasText(req.getPassword())) {
            return new BasicRes(ReplyMessage.PARAM_ERROR.getCode(), "請填寫完整資料");
        }

        // 3. 检查 email 是否已存在
        if (userDao.getEmailCount(normalizedEmail) > 0) {
            return new BasicRes(ReplyMessage.EMAIL_DUPLICATED.getCode(), ReplyMessage.EMAIL_DUPLICATED.getMessage());
        }

        // 4. 新增使用者
        userDao.insert(req.getName(), req.getAge(), normalizedEmail, encoder.encode(req.getPassword()));

        return new BasicRes(ReplyMessage.SUCCESS.getCode(), "註冊成功");
    }

    // 登录（含 email 正规化）
    public LoginRes login(LoginReq req) {
        // 1. email 正规化
        String rawEmail = req.getEmail();
        if (!StringUtils.hasText(rawEmail) || !StringUtils.hasText(req.getPassword())) {
            return new LoginRes(ReplyMessage.LOGIN_FAILED.getCode(), "請輸入帳號與密碼");
        }
        String normalizedEmail = rawEmail.trim().toLowerCase();

        // 2. 查询使用者
        User user = userDao.getByEmail(normalizedEmail);
        if (user == null) {
            return new LoginRes(ReplyMessage.USER_NOT_FOUND.getCode(), "帳號不存在");
        }

        // 3. 验证密码
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return new LoginRes(ReplyMessage.LOGIN_FAILED.getCode(), "密碼錯誤");
        }

        // 4. 回传成功訊息
        return new LoginRes(ReplyMessage.SUCCESS.getCode(),
                ReplyMessage.SUCCESS.getMessage(),
                user.getIsAdmin() != null ? user.getIsAdmin() : false,
                user.getEmail(),
                user.getName(),
                user.getUserId());
    }
    
 // 1. 處理忘記密碼請求
    public BasicRes forgotPassword(String email) {
        User user = userDao.getByEmail(email.trim().toLowerCase());
        if (user == null) {
            return new BasicRes(404, "找不到此信箱，請確認是否已註冊");
        }

        // 產生 token
        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userDao.save(user);

        // 寄信
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        try {
            mailService.sendResetPasswordEmail(user.getEmail(), resetLink);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new BasicRes(500, "郵件發送失敗，請稍後再試");
        }

        return new BasicRes(200, "重設密碼連結已寄到您的信箱");
    }

    // 2. 重設密碼
    public BasicRes resetPassword(String token, String newPassword) {
        User user = userDao.findByResetToken(token);
        if (user == null) {
            return new BasicRes(400, "無效的重設連結");
        }

        // 檢查時效
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return new BasicRes(400, "重設連結已過期，請重新申請");
        }

        // 密碼長度檢查
        if (newPassword == null || newPassword.length() < 6) {
            return new BasicRes(400, "密碼長度至少6位");
        }

        // 更新密碼
        user.setPassword(encoder.encode(newPassword));
        // 清除 token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userDao.save(user);

        return new BasicRes(200, "密碼重設成功，請使用新密碼登入");
    }
}