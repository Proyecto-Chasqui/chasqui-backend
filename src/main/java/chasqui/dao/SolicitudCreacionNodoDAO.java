package chasqui.dao;

import java.util.List;

import chasqui.model.SolicitudCreacionNodo;

public interface SolicitudCreacionNodoDAO {
	/**
	 * Guarda la solicitud
	 * @param solicitud
	 */
	public void guardar(SolicitudCreacionNodo solicitud);
	
	/**
	 * Obtiene todas las @SolicitudCreacionNodo del venededor y del cliente especificado.
	 * @param idCliente
	 * @param idVendedor
	 * @return
	 */
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(Integer idCliente, final Integer idVendedor);
	
}
