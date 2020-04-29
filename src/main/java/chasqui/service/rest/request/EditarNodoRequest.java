package chasqui.service.rest.request;

import java.io.Serializable;

public class EditarNodoRequest implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6380639718383818713L;
	private Integer idNodo;
	private Integer idVendedor;
	private String nombreNodo;
	private String descripcion;
	private String tipoNodo;
	private Integer idDireccion;
	private String barrio;

	public EditarNodoRequest() {
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTipoNodo() {
		return tipoNodo;
	}

	public Integer getIdDireccion() {
		return idDireccion;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setTipoNodo(String tipoNodo) {
		this.tipoNodo = tipoNodo;
	}

	public void setIdDireccion(Integer idDireccion) {
		this.idDireccion = idDireccion;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public Integer getIdNodo() {
		return idNodo;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdNodo(Integer idNodo) {
		this.idNodo = idNodo;
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

}
