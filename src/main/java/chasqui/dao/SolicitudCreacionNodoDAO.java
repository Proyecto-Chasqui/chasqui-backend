package chasqui.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.SolicitudCreacionNodo;

public interface SolicitudCreacionNodoDAO {
	/**
	 * Guarda la solicitud
	 * @param solicitud
	 */
	@Transactional
	public void guardar(SolicitudCreacionNodo solicitud);
	
	/**
	 * Obtiene todas las @SolicitudCreacionNodo del venededor y del cliente especificado.
	 * @param idCliente
	 * @param idVendedor
	 * @return
	 */
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(Integer idCliente, final Integer idVendedor);

	public SolicitudCreacionNodo obtenerSolitudCreacionNodoEnGestion(Integer idCliente, final Integer idVendedor);

	public SolicitudCreacionNodo obtenerSolitudCreacionNodo(Integer idSolicitud, Integer idCliente, Integer idVendedor);

	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionEnGestionDe(Integer idVendedor);
	
}
