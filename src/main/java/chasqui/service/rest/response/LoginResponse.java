package chasqui.service.rest.response;

import chasqui.model.Cliente;

public class LoginResponse {

	
	private String email;
	private String token;
	private Integer id;
	private String nickname;
	private String avatar;
	
	public LoginResponse(){
		
	}
	
	public LoginResponse(Cliente c){
		email = c.getEmail();
		token = c.getToken();
		id = c.getId();
		nickname = c.getUsername();
		avatar = c.getImagenPerfil();
	}

	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar(){
		return this.avatar;
	}

	public void setAvatar(String newAvatar){
		this.avatar = newAvatar;
	}
	
	
	
	
	
}
