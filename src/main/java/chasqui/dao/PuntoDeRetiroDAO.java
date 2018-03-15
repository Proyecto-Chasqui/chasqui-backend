package chasqui.dao;

import chasqui.model.PuntoDeRetiro;

public interface PuntoDeRetiroDAO {
	
	public PuntoDeRetiro obtenerPuntoDeRetiro(final Integer Id);
	public void guardar(PuntoDeRetiro pr);
	public void eliminar(PuntoDeRetiro pr);
	
}
