# 寧境芳療系統 – 後端 API

Spring Boot 3 後端服務，提供會員、預約、電商、文章管理、後台管理等 RESTful API。

---

## 技術棧

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security（JWT）
- MySQL
- Maven

---

## 主要功能

- JWT 身分驗證 API
- Google OAuth2 第三方登入 API
- 會員 CRUD API
- 線上預約 API（自動排除公休日、即時查詢名額）
- 購物車與訂單 API
- 文章管理 API
- 後台管理 API

---

## 環境變數設定

建立 `application.properties` 檔案，並填入以下內容：

```properties
# 資料庫設定
spring.datasource.url=jdbc:mysql://localhost:3306/ningjing_spa?useSSL=false&serverTimezone=Asia/Taipei
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT 設定
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Google OAuth2 設定
spring.security.oauth2.client.registration.google.client-id=your_google_client_id
spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
