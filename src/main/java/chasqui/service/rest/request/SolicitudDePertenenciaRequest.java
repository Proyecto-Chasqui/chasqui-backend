package chasqui.service.rest.request;

import java.io.Serializable;

public class SolicitudDePertenenciaRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2706101820574957858L;
	private Integer idVendedor;
	private Integer idNodo;
	
	public SolicitudDePertenenciaRequest() {
		
	}
	
	public Integer getIdNodo() {
		return idNodo;
	}

	public void setIdNodo(Integer idNodo) {
		this.idNodo = idNodo;
	}


	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

}
