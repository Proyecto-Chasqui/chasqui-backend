package chasqui.service.rest.response;

import chasqui.model.Cliente;

public class PerfilResponse {

	
	  private String email;
	  private String nickName;
	  private String nombre;
	  private String apellido;
	  private String telefonoFijo;
	  private String telefonoMovil;
	  private DireccionResponse direccion;
	  //TODO agregar Imagen (la clase para guardar el avatar)
	  
	public PerfilResponse(){}
	public PerfilResponse(Cliente c){
		this.email = c.getEmail();
		this.nickName = c.getUsername();
		this.nombre = c.getNombre();
		this.apellido = c.getApellido();
		this.telefonoFijo = c.getTelefonoFijo();
		this.telefonoMovil = c.getTelefonoMovil();
		if (c.obtenerDireccionPredeterminada() == null) {
			this.direccion = null;
		}
		else {
			this.direccion = new DireccionResponse(c.obtenerDireccionPredeterminada());
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public DireccionResponse getDireccion() {
		return direccion;
	}

	public void setDireccion(DireccionResponse direccion) {
		this.direccion = direccion;
	}
	
	
	
	
	
	
	
	
}
