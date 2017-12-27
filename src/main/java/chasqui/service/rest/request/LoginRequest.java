package chasqui.service.rest.request;

import java.io.Serializable;

public class LoginRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7548566725788722753L;
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
	
	
	
	
	
	
	
	
}
