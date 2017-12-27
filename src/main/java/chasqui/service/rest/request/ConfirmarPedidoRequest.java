package chasqui.service.rest.request;

import java.io.Serializable;

public class ConfirmarPedidoRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7821567228734738867L;
	
	private Integer idPedido;
	private Integer idDireccion;
	private String comentario;
	
	public Integer getIdPedido() {
		return idPedido;
	}
	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}
	public Integer getIdDireccion() {
		return idDireccion;
	}
	public void setIdDireccion(Integer idDireccion) {
		this.idDireccion = idDireccion;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	
	

}
