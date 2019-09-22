package chasqui.service.rest.response;

import chasqui.model.Cliente;

public class DataClienteSolicitudResponse {
	private String email;
	private String nombre;
	private String apellido;
	private String telefonoFijo;
	private String telefonoMovil;

	
	public DataClienteSolicitudResponse(Cliente cliente) {
		this.email = cliente.getEmail();
		this.nombre = cliente.getNombre();
		this.apellido = cliente.getApellido();
		this.telefonoFijo = cliente.getTelefonoFijo();
		this.telefonoMovil = cliente.getTelefonoMovil();

	}
	
	public String getNombre() {
		return nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public String getTelefonoFijo() {
		return telefonoFijo;
	}
	public String getTelefonoMovil() {
		return telefonoMovil;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}
	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}	
