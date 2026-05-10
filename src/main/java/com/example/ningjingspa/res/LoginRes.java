package com.example.ningjingspa.res;

public class LoginRes extends BasicRes {
    private boolean admin;
    private String email;
    private String name;
    private Integer userId;
    private String token;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 返回类型改为 Integer，防止空指针
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // 构造方法保持不变
    public LoginRes(int code, String message) {
        super(code, message);
    }

    public LoginRes(int code, String message, boolean admin, String email, String name, Integer userId) {
        super(code, message);
        this.admin = admin;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public LoginRes(int code, String message, boolean admin, String email, String name, Integer userId, String token) {
        super(code, message);
        this.admin = admin;
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.token = token;
    }
}