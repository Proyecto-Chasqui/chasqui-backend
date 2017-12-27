package chasqui.service.rest.request;

import java.io.Serializable;

public class EditarPerfilRequest implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6246311656076856907L;
	private DireccionRequest direccion;
	private String password;
	private String nickName;
	private String nombre;
	private String apellido;
	private String telefonoFijo;
	private String telefonoMovil;
	private String avatar;
	private String extension;
	
	public DireccionRequest getDireccion() {
		return direccion;
	}
	public void setDireccion(DireccionRequest direccion) {
		this.direccion = direccion;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getTelefonoFijo() {
		return telefonoFijo;
	}
	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}
	public String getTelefonoMovil() {
		return telefonoMovil;
	}
	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	
	
	
	
	@Override
	public String toString(){
		return "EditarPerfilRequest: [nickName:"+nickName+" nombre:"+nombre+" apellido:"+apellido+
		 " telefonoFijo:"+telefonoFijo+" telefonoMovil:"+telefonoMovil+" ]";
	}
	
	
	
	
	
}
