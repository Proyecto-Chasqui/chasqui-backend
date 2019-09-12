package chasqui.service.rest.request;

import java.io.Serializable;

import chasqui.model.Direccion;

public class NodoSolicitudCreacionRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8462160780802294936L;
	
	private Integer idVendedor;
	private String nombreNodo;
	private Integer idDomicilio;
	private String tipoNodo;
	private String barrio;
	private String descripcion;
	private String estado;
	
	public NodoSolicitudCreacionRequest() {
		
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	public String getNombreNodo() {
		return nombreNodo;
	}

	public void setNombreNodo(String nombreNodo) {
		this.nombreNodo = nombreNodo;
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

	public Integer getIdDomicilio() {
		return idDomicilio;
	}

	public void setIdDomicilio(Integer idDomicilio) {
		this.idDomicilio = idDomicilio;
	}
}
