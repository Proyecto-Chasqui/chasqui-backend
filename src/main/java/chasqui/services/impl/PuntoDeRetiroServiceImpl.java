package chasqui.services.impl;


import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.PuntoDeRetiroDAO;
import chasqui.model.PuntoDeRetiro;
import chasqui.services.interfaces.PuntoDeRetiroService;

public class PuntoDeRetiroServiceImpl implements PuntoDeRetiroService {
	
	@Autowired
	PuntoDeRetiroDAO puntoDeRetiroDAO;
	
	@Override
	public PuntoDeRetiro obtenerPuntoDeRetiroConId(Integer idPuntoDeRetiro){
		PuntoDeRetiro pr = puntoDeRetiroDAO.obtenerPuntoDeRetiro(idPuntoDeRetiro);
		return pr;

	}
	@Override
	public void guardarPuntoDeRetiro(PuntoDeRetiro pr){
		puntoDeRetiroDAO.guardar(pr);
	}
}
