package com.example.ningjingspa.req;

public class RegisterReq {

	private String email;
	
	private String name;
	
	private String password;
	
	private int age;

	

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public RegisterReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegisterReq(String email, String name, String password, int age) {
		super();
		this.email = email;
		this.name = name;
		this.password = password;
		this.age = age;
	}
	
	
	
}