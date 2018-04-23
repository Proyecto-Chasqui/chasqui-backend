package chasqui.service.rest.impl;

import java.io.Serializable;

public class OpcionSeleccionadaRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6999034658223994397L;
	private String nombre;
	private String opcionSeleccionada;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getOpcionSeleccionada() {
		return opcionSeleccionada;
	}
	public void setOpcionSeleccionada(String opcionSeleccionada) {
		this.opcionSeleccionada = opcionSeleccionada;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
