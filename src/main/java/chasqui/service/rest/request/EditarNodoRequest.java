package chasqui.service.rest.request;

import java.io.Serializable;

public class EditarNodoRequest implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6380639718383818713L;
	private String alias;
	private String descripcion;
	private String tipoNodo;
	private Integer idDireccion;
	private String barrio;

	public EditarNodoRequest() {
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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

}
