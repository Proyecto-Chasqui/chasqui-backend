package chasqui.service.rest.request;

import java.io.Serializable;

public class DireccionZonaRequest  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	Integer idDireccion;
	Integer idVendedor;
	
	public DireccionZonaRequest () {
		
	}


	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}


	public Integer getIdDireccion() {
		return idDireccion;
	}


	public void setIdDireccion(Integer idDireccion) {
		this.idDireccion = idDireccion;
	}

}
