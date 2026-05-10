package com.example.ningjingspa.req;

public class LoginReq {

	private String email;
	
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginReq() {
		super();
		// TODO Auto-generated constructor stub
	}


	public LoginReq(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	
}
