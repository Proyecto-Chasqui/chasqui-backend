package chasqui.services.impl;


import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.PuntoDeRetiroDAO;
import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.model.PuntoDeRetiro;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.view.composer.Constantes;

public class PuntoDeRetiroServiceImpl implements PuntoDeRetiroService {
	
	@Autowired
	PuntoDeRetiroDAO puntoDeRetiroDAO;
	
	@Override
	public PuntoDeRetiro obtenerPuntoDeRetiroConId(Integer idPuntoDeRetiro){
		PuntoDeRetiro pr = puntoDeRetiroDAO.obtenerPuntoDeRetiro(idPuntoDeRetiro);
		return pr;

	}
}
