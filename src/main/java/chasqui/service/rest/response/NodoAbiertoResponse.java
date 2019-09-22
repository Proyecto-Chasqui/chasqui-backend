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
	private String alias;
	private String emailAdministrador;
	private String descripcion;
	private Direccion direccionDelNodo;
	private String barrio;
	
	public NodoAbiertoResponse(Nodo nodo) {
		alias=nodo.getAlias();
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
	}
	
	public Integer getIdNodo() {
		return idNodo;
	}
	public String getAlias() {
		return alias;
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
	public void setAlias(String alias) {
		this.alias = alias;
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
	
}
