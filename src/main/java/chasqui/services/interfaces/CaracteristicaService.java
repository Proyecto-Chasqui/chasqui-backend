package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;

public interface CaracteristicaService {

	@Transactional
	public void guardaCaracteristicasProducto(List<Caracteristica>list);
	@Transactional
	public void guardarCaracteristicaProductor(List<CaracteristicaProductor>list);
	@Transactional
	public void eliminarCaracteristica(Caracteristica c);
	@Transactional
	public void eliminarCaracteristicaProductor(CaracteristicaProductor c);
	@Transactional
	public List<Caracteristica> buscarCaracteristicasProducto();
	@Transactional
	public List<CaracteristicaProductor> buscarCaracteristicasProductor();
	@Transactional
	public void actualizarCaracteristica(ICaracteristica c);
	@Transactional
	void actualizarCaracteristicaProductor(ICaracteristica c);
	
}
