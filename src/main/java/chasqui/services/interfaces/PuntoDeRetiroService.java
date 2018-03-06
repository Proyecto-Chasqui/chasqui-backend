package chasqui.services.interfaces;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.model.PuntoDeRetiro;

public interface PuntoDeRetiroService {
	
	@Transactional
	public PuntoDeRetiro obtenerProductoresDe(Integer idPuntoDeRetiro);
	
}
