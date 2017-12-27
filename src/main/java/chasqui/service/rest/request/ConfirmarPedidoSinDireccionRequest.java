package chasqui.service.rest.request;

import java.io.Serializable;

public class ConfirmarPedidoSinDireccionRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4706025448249068963L;

	private Integer idPedido;

	public Integer getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}