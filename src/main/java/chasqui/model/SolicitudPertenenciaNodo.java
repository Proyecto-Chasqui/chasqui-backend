package chasqui.model;

import org.joda.time.DateTime;

import chasqui.view.composer.Constantes;

public class SolicitudPertenenciaNodo {
	private Integer id;
	private Nodo nodo;
	private Usuario usuarioSolicitante;
	private String estado;
	private Integer reintentos;
	private DateTime fechaCreacion;
	
	public SolicitudPertenenciaNodo() {}
	
	public SolicitudPertenenciaNodo(Nodo nodo, Usuario usuario) {
		this.nodo = nodo;
		this.setUsuarioSolicitante(usuario);
		this.setFechaCreacion(DateTime.now());
		this.reintentos = 0;
		this.estado = Constantes.SOLICITUD_PERTENENCIA_NODO_ENVIADO;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Nodo getNodo() {
		return nodo;
	}

	public void setNodo(Nodo nodo) {
		this.nodo = nodo;
	}


	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getReintentos() {
		return reintentos;
	}

	public void setReintentos(Integer reintentos) {
		this.reintentos = reintentos;
	}

	public Usuario getUsuarioSolicitante() {
		return usuarioSolicitante;
	}

	public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
		this.usuarioSolicitante = usuarioSolicitante;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
}
