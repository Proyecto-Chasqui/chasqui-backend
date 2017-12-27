package chasqui.model;

import chasqui.view.composer.Constantes;

public class Nodo extends GrupoCC{
	
	private Integer id;
	private String estado;
	private String tipo;
	private String emailAdministradorNodo;
	private Direccion direccionEntrega;
	
	//Constructor
	public Nodo () {}
	
	public Nodo (Cliente administrador, String alias, String descripcion) {
		super(administrador, alias, descripcion);
		this.estado = Constantes.ESTADO_NODO_SOLICITADO;
		this.setTipo(Constantes.NODO_ABIERTO);
	}

	//Gets & Sets
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
//
//	public void setEstado(String estado) {
//		this.estado = estado;
//	}
	
	//Methods
	public void aprobarNodo() {
		if(estado.equals(Constantes.ESTADO_NODO_SOLICITADO)){
			estado = Constantes.ESTADO_NODO_APROBADO;	
		}
		else{
			//TODO levantar excepcion
		}
	}

	public void cancelarAprobacion(){
		if(estado.equals(Constantes.ESTADO_NODO_APROBADO)){
			estado = Constantes.ESTADO_NODO_SOLICITADO;	
		}
		else{
			//TODO levantar excepcion
		}
	}

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

	public Direccion getDireccionEntrega() {
		return direccionEntrega;
	}

	public void setDireccionEntrega(Direccion direccionEntrega) {
		this.direccionEntrega = direccionEntrega;
	}
	
	
}
