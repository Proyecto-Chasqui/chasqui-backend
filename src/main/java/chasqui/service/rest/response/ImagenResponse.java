package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Imagen;

public class ImagenResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4695983036382885996L;
	private String nombre;
	private String path;
	
	
	
	
	public ImagenResponse(){}
	public ImagenResponse(Imagen i) {
		nombre = i.getNombre();
		path = i.getPath();
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	
	
	

}
