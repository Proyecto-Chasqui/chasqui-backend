package chasqui.model;

import chasqui.services.interfaces.ICaracteristica;

public class CaracteristicaProductor implements ICaracteristica{

	private Integer id;
	private String nombre;
	private String pathImagen;
	private String descripcion;
	private Boolean eliminada;

	//CONSTRUCTORs
	
	public CaracteristicaProductor(){}
	
	public CaracteristicaProductor(String nombre){
		this.nombre = nombre;
	}
	
	//GETs & SETs
	
	
	public CaracteristicaProductor(String nombre,  String path, String desc) {
		this.nombre=nombre;
		this.pathImagen = path;
		this.descripcion = desc;
		this.eliminada=false;
	}

	public Boolean getEliminada() {
		return eliminada;
	}
	
	public void setEliminada(Boolean eliminada) {
		this.eliminada = eliminada;
	}

	
	public Integer getId() {
		return id;
	}	

	
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	
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
	
	
	
	//METHODS
	
	@Override
	public String toString(){
		return this.getNombre();
	}

	@Override
	public boolean equals(Object obj){
		if( obj == null){
			return false;
		}
		if(! (obj instanceof CaracteristicaProductor)){
			return false;
		}
		if(((CaracteristicaProductor) obj).getNombre().equalsIgnoreCase(this.nombre)){
			return true;
		}
		return false;
	}
}
