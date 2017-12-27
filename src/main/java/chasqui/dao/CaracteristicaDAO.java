package chasqui.dao;

import java.util.List;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.services.interfaces.ICaracteristica;

public interface CaracteristicaDAO {

	
	public void guardaCaracteristicasProducto(List<Caracteristica>list);	
	public void guardarCaracteristicaProductor(List<CaracteristicaProductor>list);	
	public List<Caracteristica> buscarCaracteristicasProducto();
	public List<CaracteristicaProductor> buscarCaracteristicasProductor();
	public void eliminarCaracteristica(Caracteristica c);
	public void eliminarCaracteristicaProductor(CaracteristicaProductor c);
	public void actualizarCaracteristicaProductor(ICaracteristica c);
	public void actualizar(ICaracteristica c);
	
}
