package chasqui.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.SolicitudPertenenciaNodo;

public interface SolicitudPertenenciaNodoDAO {
	
	public void guardar(SolicitudPertenenciaNodo solicitud);

	public SolicitudPertenenciaNodo obtenerSolicitudPertenenciaById(Integer idSolicitud);

	public SolicitudPertenenciaNodo obtenerSolicitudDe(Integer idNodo, Integer idCliente);

	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeNodo(Integer idNodo);

	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(Integer idUsuario,
			Integer idVendedor);

	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePretenenciaDeVendedor(Integer id);
	@Transactional
	public void eliminarSolicitudesDePertenencia(List<SolicitudPertenenciaNodo> solicitudesDePertenenciaDeVendedor);
}	
