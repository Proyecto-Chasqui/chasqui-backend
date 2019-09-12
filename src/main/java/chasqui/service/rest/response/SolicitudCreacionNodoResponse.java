package chasqui.service.rest.response;

import chasqui.model.Direccion;
import chasqui.model.SolicitudCreacionNodo;

public class SolicitudCreacionNodoResponse {
	
	private Integer id;
	private String nombreNodo;
	private Direccion domicilio;
	private String tipoNodo;
	private String barrio;
	private String descripcion;
	private String estado;
	
	public SolicitudCreacionNodoResponse(SolicitudCreacionNodo solicitud) {
		this.id = solicitud.getId();
		this.nombreNodo = solicitud.getNombreNodo();
		this.domicilio = solicitud.getDomicilio();
		this.tipoNodo= solicitud.getTipoNodo();
		this.barrio= solicitud.getBarrio();
		this.descripcion = solicitud.getDescripcion();
		this.estado = solicitud.getEstado();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombreNodo() {
		return nombreNodo;
	}

	public void setNombreNodo(String nombreNodo) {
		this.nombreNodo = nombreNodo;
	}

	public Direccion getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Direccion domicilio) {
		this.domicilio = domicilio;
	}

	public String getTipoNodo() {
		return tipoNodo;
	}

	public void setTipoNodo(String tipoNodo) {
		this.tipoNodo = tipoNodo;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
