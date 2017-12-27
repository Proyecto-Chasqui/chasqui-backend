package chasqui.service.rest.response;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import chasqui.model.Notificacion;

public class NotificacionResponse implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3705330718520859584L;
	private String usuarioOrigen;
	private String fechaCreacion;
	private String mensaje;
	private String estado;
	private Integer id;
	
	
	public NotificacionResponse(Notificacion n) {
		usuarioOrigen = n.getUsuarioOrigen();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		fechaCreacion = format.format(n.getFecha().toDate());
		mensaje = n.getMensaje();
		estado = n.getEstado();
		id = n.getId();
	}
	
	public NotificacionResponse(){}
	
	public String getUsuarioOrigen() {
		return usuarioOrigen;
	}
	public void setUsuarioOrigen(String usuarioOrigen) {
		this.usuarioOrigen = usuarioOrigen;
	}
	public String getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
	
	

}
