package chasqui.service.rest.response;

import chasqui.model.Cliente;

public class ClienteResponse {
	private String alias;
	private String email;
	
	public ClienteResponse(Cliente c) {
		this.alias = c.getUsername();
		this.email = c.getEmail();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

}
