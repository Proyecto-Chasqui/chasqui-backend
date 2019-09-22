package chasqui.service.rest.response;

import java.io.Serializable;

import org.joda.time.DateTime;

import chasqui.model.Cliente;
import chasqui.model.SolicitudPertenenciaNodo;

public class SolicitudDePertenenciaResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2022595631933252473L;
	private Integer id;
	private NodoAbiertoResponse nodo;
	private DataClienteSolicitudResponse cliente;
	private String estado;
	private DateTime fechaCreacion;
	
	public SolicitudDePertenenciaResponse(SolicitudPertenenciaNodo solicitud) {
		this.nodo = new NodoAbiertoResponse(solicitud.getNodo());
		this.id = solicitud.getId();
		this.cliente = new DataClienteSolicitudResponse((Cliente) solicitud.getUsuarioSolicitante());
		this.estado = solicitud.getEstado();
		this.fechaCreacion = solicitud.getFechaCreacion();
	}

	public Integer getId() {
		return id;
	}

	public NodoAbiertoResponse getNodo() {
		return nodo;
	}

	public DataClienteSolicitudResponse getCliente() {
		return cliente;
	}

	public String getEstado() {
		return estado;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNodo(NodoAbiertoResponse nodo) {
		this.nodo = nodo;
	}

	public void setCliente(DataClienteSolicitudResponse cliente) {
		this.cliente = cliente;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	
}
