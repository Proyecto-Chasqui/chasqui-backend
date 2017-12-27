package chasqui.model;

import chasqui.services.interfaces.ICaracteristica;

public class Caracteristica implements ICaracteristica{
	
	private Integer id;
	private String nombre;
	private String pathImagen;
	private String descripcion;
	private Boolean eliminada;
	//CONSTRUCTORs
	
	public Caracteristica(){}
	
	public Caracteristica(String nombre){
		this.nombre = nombre;
	}
	
	//GETs & SETs
		
	public Caracteristica(String nombre, String path, String desc) {
		this.nombre=nombre;
		this.pathImagen = path;
		this.descripcion = desc;
		this.eliminada=false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
	
	
	
	
	public Boolean getEliminada() {
		return eliminada;
	}
	
	public void setEliminada(Boolean eliminada) {
		this.eliminada = eliminada;
	}
	
	
	
	//METHODS

	@Override
	public boolean equals(Object obj){
		if( obj == null){
			return false;
		}
		if(! (obj instanceof Caracteristica)){
			return false;
		}
		if(((Caracteristica) obj).getNombre().equalsIgnoreCase(this.nombre)){
			return true;
		}
		return false;
	}
	
	public String toString(){
		return nombre;
	}
	

}
