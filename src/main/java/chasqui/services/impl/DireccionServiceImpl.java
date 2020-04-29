package chasqui.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.DireccionDAO;
import chasqui.model.Direccion;
import chasqui.services.interfaces.DireccionService;

public class DireccionServiceImpl implements DireccionService {
	
	@Autowired
	private DireccionDAO direccionDAO;
	
	@Override
	public Direccion obtenerDireccionPorId(Integer id) {
		return direccionDAO.obtenerDireccionPorId(id);
	}

}
