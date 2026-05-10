package com.example.ningjingspa.service;

import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 讓 Spring Security 先去 Google 拿使用者資料
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. 從 Google 回傳的資料中取出 email 和 name
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 3. 根據 email 查詢或建立使用者
        User user = userDao.getByEmail(email);
        if (user == null) {
            // 第一次登入 → 建立新使用者（密碼可留空，因為之後都走 Google 登入）
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setIsAdmin(false);  // 預設非管理員
            // 注意：User 實體中 password 可以為 null，但資料庫欄位若設定 NOT NULL 需給予預設值
            user.setPassword(""); // 或 null，但若資料庫不允許 null，則給空字串
            user.setAge(0); // 給預設年齡
            userDao.save(user);
        } else {
            // 已存在的使用者，可在此更新名稱（如果 Google 的名稱有變）
            userDao.save(user);
        }

        // 4. 回傳 OAuth2User（後續 Authentication Success Handler 會用到）
        return oAuth2User;
    }
}