package chasqui.service.rest.request;

import java.io.Serializable;

public class NuevoPedidoIndividualRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5297230737069175687L;

	Integer idGrupo;
	Integer idVendedor;


	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public NuevoPedidoIndividualRequest() {
		super();
	}
	
	
}
