package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import chasqui.model.PuntoDeRetiro;

public interface PuntoDeRetiroService {
	
	@Transactional
	public PuntoDeRetiro obtenerPuntoDeRetiroConId(Integer idPuntoDeRetiro);
	@Transactional
	public void guardarPuntoDeRetiro(PuntoDeRetiro pr);
	@Transactional
	public void eliminarPuntosDeRetiro(List<PuntoDeRetiro> puntosDeRetiro);
}
