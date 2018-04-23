package chasqui.service.rest.request;

import java.io.Serializable;
import java.util.List;

import chasqui.service.rest.impl.OpcionSeleccionadaRequest;

public class ConfirmarPedidoRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7821567228734738867L;
	
	private Integer idPedido;
	private Integer idDireccion;
	private Integer idPuntoDeRetiro;
	private List<OpcionSeleccionadaRequest> opcionesSeleccionadas;
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
	public Integer getIdPuntoDeRetiro() {
		return idPuntoDeRetiro;
	}
	public void setIdPuntoDeRetiro(Integer idPuntoDeRetiro) {
		this.idPuntoDeRetiro = idPuntoDeRetiro;
	}
	public List<OpcionSeleccionadaRequest> getOpcionesSeleccionadas() {
		return opcionesSeleccionadas;
	}
	public void setOpcionesSeleccionadas(List<OpcionSeleccionadaRequest> opcionesSeleccionadas) {
		this.opcionesSeleccionadas = opcionesSeleccionadas;
	}
	
	
	

}
