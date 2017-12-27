package chasqui.service.rest.request;

import java.io.Serializable;

public class CrearPedidoRequest implements Serializable{

	private static final long serialVersionUID = 21308459848888106L;
	private Integer idVendedor;
	
	public Integer getIdVendedor() {
		return idVendedor;
	}
	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

}
