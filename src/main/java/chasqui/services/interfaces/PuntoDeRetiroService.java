package chasqui.services.interfaces;

import org.springframework.transaction.annotation.Transactional;
import chasqui.model.PuntoDeRetiro;

public interface PuntoDeRetiroService {
	
	@Transactional
	public PuntoDeRetiro obtenerPuntoDeRetiroConId(Integer idPuntoDeRetiro);
	@Transactional
	public void guardarPuntoDeRetiro(PuntoDeRetiro pr);
}
