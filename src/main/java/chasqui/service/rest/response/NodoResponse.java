package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;

public class NodoResponse implements Serializable {

	
	/**
	 * 
	 */
	
	private String alias;
	private String estado;
	private String tipo;
	private Direccion domicilioEntrega;
	private String email; 
	
	private static final long serialVersionUID = 3281477518344064501L;
	
	
	/*Constructores*/
	
	public NodoResponse(){}
			
	public NodoResponse(Nodo nodo) {
		
		alias     = nodo.getAlias();
		email 	  = nodo.getAdministrador().getEmail();
		estado    = nodo.getEstado();
		tipo      = nodo.getTipo();
		domicilioEntrega = nodo.getDireccionEntrega();
	}
	
	/*Getters & setters*/
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTipo() {
		
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Direccion getDomicilioEntrega() {
		return domicilioEntrega;
	}

	public void setDomicilioEntrega(Direccion domicilioEntrega) {
		this.domicilioEntrega = domicilioEntrega;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

}
