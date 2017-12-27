package chasqui.model;

import org.joda.time.DateTime;

public class Notificacion {

	private Integer id;
	private DateTime fecha;
	private String usuarioOrigen;
	private String usuarioDestino;
	private String mensaje;
	private String estado;
	
	//GETs & SETs
	
	//TODO no deberia recibir el estado como parametro. DEfinir estado inicial
	public Notificacion(String userOrigen,String userDestino,String msj,String est){
		fecha = new DateTime();
		usuarioOrigen = userOrigen;
		usuarioDestino = userDestino;
		mensaje = msj;
		estado = est;
	}
	
	public Notificacion(){}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public DateTime getFecha() {
		return fecha;
	}

	public void setFecha(DateTime fecha) {
		this.fecha = fecha;
	}
	
	public String getUsuarioOrigen() {
		return usuarioOrigen;
	}

	public void setUsuarioOrigen(String usuarioOrigen) {
		this.usuarioOrigen = usuarioOrigen;
	}

	public String getUsuarioDestino() {
		return usuarioDestino;
	}

	public void setUsuarioDestino(String usuarioDestino) {
		this.usuarioDestino = usuarioDestino;
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
	
	
}
