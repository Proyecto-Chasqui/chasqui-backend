package chasqui.service.rest.request;

import java.io.Serializable;
import java.util.List;

import chasqui.service.rest.impl.OpcionSeleccionadaRequest;

public class ConfirmarPedidoColectivoRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5077516709705680L;
	
	private Integer idGrupo;
	private Integer idDireccion;
	private Integer idPuntoDeRetiro;
	private List<OpcionSeleccionadaRequest> opcionesSeleccionadas;
	private String comentario;
	private Integer idZona;
	
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Integer getIdDireccion() {
		return idDireccion;
	}

	public void setIdDireccion(Integer idDireccion) {
		this.idDireccion = idDireccion;
	}

	public ConfirmarPedidoColectivoRequest() {
		super();
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
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

	public Integer getIdZona() {
		return idZona;
	}

	public void setIdZona(Integer idZona) {
		this.idZona = idZona;
	}
	


}
