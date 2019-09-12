package chasqui.model;

import chasqui.view.composer.Constantes;

public class SolicitudCreacionNodo {
	
	private Integer id;
	private Integer idVendedor;
	private Usuario usuarioSolicitante;
	private String nombreNodo;
	private Direccion domicilio;
	private String tipoNodo;
	private String barrio;
	private String descripcion;
	private String estado;
	
	public SolicitudCreacionNodo() {}
	
	public SolicitudCreacionNodo(Integer idVendedor, Usuario usuario, String nombre, Direccion direccion, String tipo, String barrio, String descripcion) {
		this.idVendedor = idVendedor;
		this.usuarioSolicitante = usuario;
		this.nombreNodo = nombre;
		this.domicilio = direccion;
		this.tipoNodo = tipo;
		this.barrio = barrio;
		this.descripcion = descripcion;
		this.estado = Constantes.SOLICITUD_NODO_EN_GESTION;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Usuario getUsuarioSolicitante() {
		return usuarioSolicitante;
	}

	public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
		this.usuarioSolicitante = usuarioSolicitante;
	}

	public String getNombreNodo() {
		return nombreNodo;
	}

	public void setNombreNodo(String nombreNodo) {
		this.nombreNodo = nombreNodo;
	}

	public Direccion getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Direccion domicilio) {
		this.domicilio = domicilio;
	}

	public String getTipoNodo() {
		return tipoNodo;
	}

	public void setTipoNodo(String tipoNodo) {
		this.tipoNodo = tipoNodo;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
}
