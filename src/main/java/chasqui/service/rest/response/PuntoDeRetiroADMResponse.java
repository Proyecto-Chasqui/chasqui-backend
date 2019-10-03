package chasqui.service.rest.response;

import chasqui.model.PuntoDeRetiro;

public class PuntoDeRetiroADMResponse {
	
	private Integer id;
	private String nombre;
	private String descripcion;
	private DireccionResponse direccion;
	private boolean habilitado;
	private String status;
	
	public PuntoDeRetiroADMResponse(PuntoDeRetiro puntoDeRetiro, String status) {
		this.setId(puntoDeRetiro.getId());
		this.nombre = puntoDeRetiro.getNombre();
		this.descripcion = puntoDeRetiro.getDescripcion();
		this.direccion = new DireccionResponse(puntoDeRetiro.getDireccion());
		this.setHabilitado(puntoDeRetiro.getDisponible());
		this.setStatus(status);
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

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}




}

