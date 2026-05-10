package com.example.ningjingspa.res;

public class RegisterRes extends BasicRes{

	private boolean success;       // 是否註冊成功
    private String message;        // 提示訊息（例如「註冊成功」或「手機號碼已存在」）
    private Integer userId;        // 新使用者的 ID（可選）
	
    public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public RegisterRes(int code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}
	public RegisterRes(int code, String message, boolean success, String message2, Integer userId) {
		super(code, message);
		this.success = success;
		this.userId = userId;
	}
    
}