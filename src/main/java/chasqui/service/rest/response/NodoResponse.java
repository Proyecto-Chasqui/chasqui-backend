package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Direccion;
import chasqui.model.Nodo;

public class NodoResponse implements Serializable {

	
	/**
	 * 
	 */
	
	private String alias;
	private String tipo;
	private Direccion direccionDelNodo;
	private String email; 
	
	private static final long serialVersionUID = 3281477518344064501L;
	
	
	/*Constructores*/
	
	public NodoResponse(){}
			
	public NodoResponse(Nodo nodo) {
		
		alias     = nodo.getAlias();
		email 	  = nodo.getAdministrador().getEmail();
		tipo      = nodo.getTipo();
		setDireccionDelNodo(nodo.getDireccionDelNodo());
	}
	
	/*Getters & setters*/
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTipo() {
		
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Direccion getDireccionDelNodo() {
		return direccionDelNodo;
	}

	public void setDireccionDelNodo(Direccion direccionDelNodo) {
		this.direccionDelNodo = direccionDelNodo;
	}

	

}
