package chasqui.service.rest.request;

import java.io.Serializable;

public class SinFiltroRequest extends ProductoRequest implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1992222429532296095L;
	private Integer idVendedor;

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	

}
