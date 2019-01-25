package chasqui.service.rest.response;

import chasqui.model.DataContacto;

public class DataContactoResponse {

	private DireccionResponse direccion;
	private String telefono;
	private String celular;
	private String email;
	private String url;
	
	public DataContactoResponse(DataContacto dc) {
		if(dc != null){
			this.setDireccion(new DireccionResponse().direccionResponseNoID(dc.getDireccion()));
			this.telefono = dc.getTelefono();
			this.celular = dc.getCelular();
			this.email = dc.getEmail();
			this.url = dc.getUrl();
		}
	}
	

	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public DireccionResponse getDireccion() {
		return direccion;
	}


	public void setDireccion(DireccionResponse direccion) {
		this.direccion = direccion;
	}
	
}
