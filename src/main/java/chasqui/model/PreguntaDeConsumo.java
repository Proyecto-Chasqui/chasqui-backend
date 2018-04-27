package chasqui.model;

import java.util.List;

public class PreguntaDeConsumo {

	private Integer id;
	private String nombre;
	private Boolean habilitada;
	private List<String> opciones;
	
	public PreguntaDeConsumo(){
		
	}
	
	public PreguntaDeConsumo(String nombre, Boolean habilitada, List<String> opciones){
		this.setNombre(nombre);
		this.setHabilitada(habilitada);
		this.setOpciones(opciones);
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

	public void setNombre(String pnombre) {
		nombre = pnombre;
	}
	
	public boolean noHabilitada(){
		boolean ret = false;
		if(! habilitada){
			ret = true;
		}
		return ret;
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
