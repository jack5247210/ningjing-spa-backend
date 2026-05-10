package com.example.ningjingspa.res;

public class BasicRes {

	private int code;
	
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BasicRes(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public BasicRes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * 一般res回復，都會有建構方法，因為要返回常常是return new Res
	 */
	
}
