package chasqui.service.rest.response;

import java.io.Serializable;
import chasqui.model.Direccion;
import chasqui.model.Nodo;

public class NodoAbiertoResponse implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6744959569083461984L;
	
	private Integer idNodo;
	private String nombreDelNodo;
	private String emailAdministrador;
	private String descripcion;
	private Direccion direccionDelNodo;
	private String barrio;
	private ZonaResponse zona;
	
	public NodoAbiertoResponse(Nodo nodo) {
		setNombreDelNodo(nodo.getAlias());
		emailAdministrador = nodo.getAdministrador().getEmail();
		this.descripcion = nodo.getDescripcion();
		this.setIdNodo(nodo.getId());
		Direccion direccion = nodo.getDireccionDelNodo();
		direccion.setGeoUbicacion(null);
		direccion.setAltura(null);
		direccion.setLongitud("0.0");
		direccion.setLatitud("0.0");
		this.setDireccionDelNodo(direccion);
		this.setBarrio(nodo.getBarrio());
		this.setZona(null);
		if(nodo.getZona() != null) {
			this.setZona(new ZonaResponse(nodo.getZona()));
		}
	}
	
	public Integer getIdNodo() {
		return idNodo;
	}

	public String getEmailAdministrador() {
		return emailAdministrador;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public Direccion getDireccionDelNodo() {
		return direccionDelNodo;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setIdNodo(Integer idNodo) {
		this.idNodo = idNodo;
	}

	public void setEmailAdministrador(String emailAdministrador) {
		this.emailAdministrador = emailAdministrador;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public void setDireccionDelNodo(Direccion direccionDelNodo) {
		this.direccionDelNodo = direccionDelNodo;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getNombreDelNodo() {
		return nombreDelNodo;
	}

	public void setNombreDelNodo(String nombreDelNodo) {
		this.nombreDelNodo = nombreDelNodo;
	}

	public ZonaResponse getZona() {
		return zona;
	}

	public void setZona(ZonaResponse zona) {
		this.zona = zona;
	}	
	
}
