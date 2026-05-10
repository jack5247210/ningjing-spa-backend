package com.example.ningjingspa.constants;

public enum ReplyMessage {

    // 成功
    SUCCESS(200, "操作成功"),

    // 使用者相關
    USER_NOT_FOUND(404, "找不到此使用者"),
    EMAIL_DUPLICATED(400, "該電子郵件已註冊"),
    LOGIN_FAILED(400, "電子郵件或密碼錯誤"),
    USER_NAME_ERROR(400, "姓名格式不正確"),
    PASSWORD_WEAK(400, "密碼強度不足"),
    AGE_INVALID(400, "年齡格式不合法"),

    // 認證授權
    UNAUTHORIZED(401, "請先登入"),
    ACCESS_DENIED(403, "權限不足"),
    TOKEN_EXPIRED(401, "登入狀態已過期，請重新登入"),
    TOKEN_INVALID(401, "無效的登入憑證"),

    // 文章相關
    TITLE_EMPTY(400, "請輸入標題"),
    CONTENT_EMPTY(400, "請輸入內容"),
    ARTICLE_NOT_FOUND(404, "文章不存在或已被刪除"),
    CATEGORY_NOT_FOUND(404, "文章分類不存在"),
    AUTHOR_NOT_FOUND(404, "作者不存在"),
    ARTICLE_CREATE_FAIL(500, "文章新增失敗，請稍後再試"),
    ARTICLE_UPDATE_FAIL(500, "文章更新失敗"),
    ARTICLE_DELETE_FAIL(500, "文章刪除失敗"),

    // 產品相關
    PRODUCT_NOT_FOUND(404, "產品不存在"),
    PRODUCT_CREATE_FAIL(500, "產品新增失敗"),

    // 圖片上傳
    IMAGE_UPLOAD_FAIL(500, "圖片上傳失敗"),
    FILE_TYPE_NOT_ALLOWED(400, "不支援的檔案格式，請上傳圖片"),
    FILE_SIZE_EXCEEDED(400, "檔案過大，請壓縮後再試"),
    FILE_EMPTY(400, "請選擇要上傳的檔案"),

    // 預約相關
    APPOINTMENT_FULL(400, "此日期預約已滿"),
    APPOINTMENT_TIME_INVALID(400, "只能預約今天起未來三週內的日期"),
    APPOINTMENT_NOT_FOUND(404, "預約不存在"),
    APPOINTMENT_CANCEL_FAIL(400, "您沒有權限取消此預約"),

    // 通用錯誤
    PARAM_ERROR(400, "請求參數有誤"),
    ID_NOT_FOUND(404, "查無此資料"),
    RESOURCE_ALREADY_EXISTS(409, "資源已存在"),
    OPERATION_NOT_ALLOWED(403, "此操作不被允許"),

    // 系統錯誤
    DATABASE_ERROR(500, "資料庫連線異常，請聯繫管理員"),
    SYSTEM_BUSY(503, "系統忙碌中，請稍後再試");

    private final int code;
    private final String message;

    ReplyMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}