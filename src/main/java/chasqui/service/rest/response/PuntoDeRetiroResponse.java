package chasqui.service.rest.response;

import chasqui.model.PuntoDeRetiro;

public class PuntoDeRetiroResponse {

	private String nombre;
	private String descripcion;
	private DireccionResponse direccion;
	
	
	public PuntoDeRetiroResponse(PuntoDeRetiro puntoDeRetiro) {
		this.nombre = puntoDeRetiro.getNombre();
		this.descripcion = puntoDeRetiro.getDescripcion();
		this.direccion = new DireccionResponse(puntoDeRetiro.getDireccion());
	}

	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public DireccionResponse getDireccion() {
		return direccion;
	}

	public void setDireccion(DireccionResponse direccion) {
		this.direccion = direccion;
	}




}
