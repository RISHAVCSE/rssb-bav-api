package com.RSSBAMB.API.DTO;


public class UserRegisterDTO {
private String userName;
	
	public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

	private String password;
	
	private String role;

}
