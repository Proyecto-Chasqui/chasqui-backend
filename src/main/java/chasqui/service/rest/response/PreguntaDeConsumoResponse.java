package chasqui.service.rest.response;

import java.util.List;

public class PreguntaDeConsumoResponse {
	

	private String nombre;
	private Boolean habilitada;
	private List<String> opciones;
	
	
	public PreguntaDeConsumoResponse(String nombre, Boolean habilitada, List<String> opciones){
		this.setNombre(nombre);
		this.setHabilitada(habilitada);
		this.setOpciones(opciones);
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String pnombre) {
		nombre = pnombre;
	}
	public Boolean getHabilitada() {
		return habilitada;
	}
	public void setHabilitada(Boolean habilitada) {
		this.habilitada = habilitada;
	}
	public List<String> getOpciones() {
		return opciones;
	}
	public void setOpciones(List<String> opciones) {
		this.opciones = opciones;
	}
	
}
