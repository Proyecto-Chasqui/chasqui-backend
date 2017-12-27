package chasqui.dao;

import java.util.List;

import chasqui.model.Zona;

public interface ZonaDAO {
	
	public void guardar(Zona z);
	public void eliminar(Zona z);
	public List<Zona> buscarZonasBy(Integer idUsuario);
	public Zona ObtenerZonaPorID(final Integer idUsuario, final Integer idZona);
}
