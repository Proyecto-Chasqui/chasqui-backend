package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Categoria;

public class CategoriaResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2009984395172880603L;

	private Integer idCategoria;
	private String nombre;
	
	public CategoriaResponse(){}
	
	public CategoriaResponse(Categoria c){
		idCategoria = c.getId();
		nombre = c.getNombre();
	}
	
	
	
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	
	
	
}
