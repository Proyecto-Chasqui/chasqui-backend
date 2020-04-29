package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Point;

import chasqui.aspect.Auditada;
import chasqui.dao.ZonaDAO;
import chasqui.model.Zona;
import chasqui.services.interfaces.ZonaService;

@Auditada
public class ZonaServiceImpl implements ZonaService{

	
	@Autowired
	private ZonaDAO zonaDAO;
	
	public void guardar(Zona z) {
		zonaDAO.guardar(z);
		
	}

	public void borrar(Zona z) {
		zonaDAO.eliminar(z);		
	}

	
	
	public List<Zona> buscarZonasBy(Integer idUsuario) {
		return zonaDAO.buscarZonasBy(idUsuario);
	}
	
	
	
	
	
	public ZonaDAO getZonaDAO() {
		return zonaDAO;
	}

	public void setZonaDAO(ZonaDAO zonaDAO) {
		this.zonaDAO = zonaDAO;
	}

	@Override
	public List<Zona> obtenerZonas(Integer idVendedor) {
		return zonaDAO.obtenerZonas(idVendedor);
	}

	@Override
	public Zona buscarZonaProxima(Integer idVendedor) {
		return zonaDAO.buscarZonaProxima(idVendedor);
	}

	@Override
	public Zona obtenerZonaPorId(Integer zonaID) {
		return zonaDAO.obtenerZonaPorId(zonaID);
	}

	@Override
	public Zona obtenerZonaDePertenenciaDeDireccion(Point punto, Integer idVendedor) {
		
		return zonaDAO.obtenerZonaDePertenenciaDeDireccion(punto, idVendedor);
	}

	@Override
	public void eliminarZonas(List<Zona> zonas) {
		for(Zona zona:zonas) {
			zonaDAO.eliminar(zona);
		}
		
	}

	
	
	
	

	
	
}
