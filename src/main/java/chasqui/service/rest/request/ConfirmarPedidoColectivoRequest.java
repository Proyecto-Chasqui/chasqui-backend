package chasqui.service.rest.request;

import java.io.Serializable;

public class ConfirmarPedidoColectivoRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5077516709705680L;
	
	Integer idGrupo;
	Integer idDireccion;
	Integer idPuntoDeRetiro;
	String comentario;
	
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
	


}
