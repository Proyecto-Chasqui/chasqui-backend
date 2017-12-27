package chasqui.model;

public class Usuario {
	
	private Integer id;
	protected String username;
	protected String password;
	private String imagenPerfil;
	protected String email;
	private boolean isRoot;
	private boolean enabled=true;
	
	
	//GETs & SETs
	
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getImagenPerfil() {
		return imagenPerfil;
	}

	public void setImagenPerfil(String imagenPerfil) {
		this.imagenPerfil = imagenPerfil;
	}

	public boolean getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
		
}
