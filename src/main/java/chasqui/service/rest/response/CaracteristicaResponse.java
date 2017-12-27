package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.services.interfaces.ICaracteristica;

public class CaracteristicaResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8267960233576142542L;
	
	private String nombre;
	private Integer idMedalla;
	private String pathImagen;
	private String descripcion;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPathImagen() {
		return pathImagen;
	}
	public void setPathImagen(String pathImagen) {
		this.pathImagen = pathImagen;
	}	
	public Integer getIdMedalla() {
		return idMedalla;
	}
	public void setIdMedalla(Integer idMedalla) {
		this.idMedalla = idMedalla;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public CaracteristicaResponse(){
	}
	
	public CaracteristicaResponse(ICaracteristica c){
		this.pathImagen = c.getPathImagen();
		this.nombre = c.getNombre();
		this.descripcion = c.getDescripcion();
		this.idMedalla = c.getId();
	}
	
	
	
}
