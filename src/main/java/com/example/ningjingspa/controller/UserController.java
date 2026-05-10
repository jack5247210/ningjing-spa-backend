package com.example.ningjingspa.controller;

import com.example.ningjingspa.constants.ReplyMessage;
import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.User;
import com.example.ningjingspa.req.LoginReq;
import com.example.ningjingspa.req.RegisterReq;
import com.example.ningjingspa.res.BasicRes;
import com.example.ningjingspa.res.LoginRes;
import com.example.ningjingspa.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userservice;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;   // 新增注入

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final String SESSION_USER_EMAIL = "user_email";

    @PostMapping("/register")
    public BasicRes register(@RequestBody RegisterReq req) {
        System.out.println(">>> 收到註冊請求: " + req.getEmail());
        try {
            return userservice.register(req);
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicRes(500, "系統發生例外: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq req, HttpSession session) {
        LoginRes res = userservice.login(req);
        if (res.getCode() == ReplyMessage.SUCCESS.getCode()) {
            String jwtToken = generateJwtToken(req.getEmail());
            res.setToken(jwtToken);
            session.setAttribute(SESSION_USER_EMAIL, req.getEmail());
        }
        return res;
    }

    private String generateJwtToken(String email) {
        User user = userDao.getByEmail(email);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", user.getUserId())
                .claim("isAdmin", user.getIsAdmin())
                .claim("userName", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    @GetMapping("/logout")
    public BasicRes logout(HttpSession session) {
        session.invalidate();
        return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
    }
    
 // 检查 LINE 绑定状态
    @GetMapping("/line-status")
    public ResponseEntity<Map<String, Object>> getLineStatus(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.ok(Map.of("lineUserId", null));
        }
        User user = (User) authentication.getPrincipal();
        boolean bound = user.getLineUserId() != null && !user.getLineUserId().isEmpty();
        return ResponseEntity.ok(Map.of("lineUserId", bound ? user.getLineUserId() : null));
    }

    // 解绑 LINE
    @PostMapping("/unbind-line")
    public ResponseEntity<Map<String, String>> unbindLine(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        }
        User user = (User) authentication.getPrincipal();
        user.setLineUserId(null);
        userDao.save(user);
        return ResponseEntity.ok(Map.of("message", "已取消绑定"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<BasicRes> changePassword(@RequestBody Map<String, String> req, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).body(new BasicRes(401, "請先登入"));
        }
        User user = (User) authentication.getPrincipal();
        String oldPassword = req.get("oldPassword");
        String newPassword = req.get("newPassword");
        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(new BasicRes(400, "密碼格式不正確"));
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(new BasicRes(400, "舊密碼錯誤"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
        return ResponseEntity.ok(new BasicRes(200, "密碼修改成功"));
    }
    
 // 忘記密碼 - 發送重設連結
    @PostMapping("/forgot-password")
    public ResponseEntity<BasicRes> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new BasicRes(400, "請輸入信箱"));
        }
        BasicRes res = userservice.forgotPassword(email);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    // 重設密碼
    @PostMapping("/reset-password")
    public ResponseEntity<BasicRes> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(new BasicRes(400, "參數錯誤"));
        }
        BasicRes res = userservice.resetPassword(token, newPassword);
        return ResponseEntity.status(res.getCode()).body(res);
    }
}