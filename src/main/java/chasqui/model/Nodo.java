package chasqui.model;

import chasqui.view.composer.Constantes;

public class Nodo extends GrupoCC{
	
	private Integer id;
	private String tipo;
	private String emailAdministradorNodo;
	private Direccion direccionDelNodo;
	private String barrio;
	
	//Constructor
	public Nodo () {}
	
	public Nodo (Cliente administrador, String alias, String descripcion) {
		super(administrador, alias, descripcion);
		this.setTipo(Constantes.NODO_ABIERTO);
	}

	//Gets & Sets
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
//
//	public void setEstado(String estado) {
//		this.estado = estado;
//	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	

	public void abrirNodo() {
		this.tipo = Constantes.NODO_ABIERTO;
	}

	public void cerrarNodo() {
		this.tipo = Constantes.NODO_CERRADO;
	}

	public String getEmailAdministradorNodo() {
		return emailAdministradorNodo;
	}

	public void setEmailAdministradorNodo(String emailAdministradorNodo) {
		this.emailAdministradorNodo = emailAdministradorNodo;
	}

	public Direccion getDireccionDelNodo() {
		return direccionDelNodo;
	}

	public void setDireccionDelNodo(Direccion direccionDelNodo) {
		this.direccionDelNodo = direccionDelNodo;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	
	
}
