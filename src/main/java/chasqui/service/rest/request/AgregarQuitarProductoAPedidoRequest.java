package chasqui.service.rest.request;

import java.io.Serializable;

public class AgregarQuitarProductoAPedidoRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3912776557819016139L;
	private Integer idPedido;
	private Integer idVariante;
	private Integer cantidad;
	
	public Integer getIdPedido() {
		return idPedido;
	}
	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}
	public Integer getIdVariante() {
		return idVariante;
	}
	public void setIdVariante(Integer idVariante) {
		this.idVariante = idVariante;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	
	@Override
	public String toString(){
		return "AgregarOQuitarPedidoRequest: [ idPedido:"+idPedido+" idVariante:"+idVariante+" cantidad:"+cantidad+" ]";
	}
	
	
	
}
