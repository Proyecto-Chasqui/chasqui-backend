package chasqui.dao;

import chasqui.model.SolicitudPertenenciaNodo;

public interface SolicitudPertenenciaNodoDAO {
	
	public void guardar(SolicitudPertenenciaNodo solicitud);

	public SolicitudPertenenciaNodo obtenerSolicitudPertenenciaById(Integer idSolicitud);

	public SolicitudPertenenciaNodo obtenerSolicitudDe(Integer idNodo, Integer idCliente);
}	
