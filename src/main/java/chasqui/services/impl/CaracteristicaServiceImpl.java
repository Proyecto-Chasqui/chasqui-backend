package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.dao.CaracteristicaDAO;
import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.ICaracteristica;

@Auditada
public class CaracteristicaServiceImpl implements CaracteristicaService{

	@Autowired
	private CaracteristicaDAO caracteristicaDAO;
	
	
	
	
	public void guardaCaracteristicasProducto(List<Caracteristica> list) {
		caracteristicaDAO.guardaCaracteristicasProducto(list);
		
	}

	public void guardarCaracteristicaProductor(List<CaracteristicaProductor> list) {
		caracteristicaDAO.guardarCaracteristicaProductor(list);
		
	}

	public List<Caracteristica> buscarCaracteristicasProducto() {
		return caracteristicaDAO.buscarCaracteristicasProducto();
	}

	public List<CaracteristicaProductor> buscarCaracteristicasProductor() {
		return caracteristicaDAO.buscarCaracteristicasProductor();
	}

	public void eliminarCaracteristica(Caracteristica c){
		caracteristicaDAO.eliminarCaracteristica(c);
	}
	
	public void eliminarCaracteristicaProductor(CaracteristicaProductor c){
		caracteristicaDAO.eliminarCaracteristicaProductor(c);
	}
	
	
	public CaracteristicaDAO getCaracteristicaDAO() {
		return caracteristicaDAO;
	}

	public void setCaracteristicaDAO(CaracteristicaDAO caracteristicaDAO) {
		this.caracteristicaDAO = caracteristicaDAO;
	}

	@Override
	public void actualizarCaracteristica(ICaracteristica c) {
		caracteristicaDAO.actualizar(c);
		
	}
	
	
	@Override
	public void actualizarCaracteristicaProductor(ICaracteristica c) {
		caracteristicaDAO.actualizarCaracteristicaProductor(c);
		
	}

	@Override
	public boolean existeCaracteristicaProductorConNombre(String nombre) {
		return caracteristicaDAO.existeCaracteristicaProductorConNombre(nombre);
		
	}

	@Override
	public boolean existeCaracteristicaProductoConNombre(String nombre) {
		return caracteristicaDAO.existeCaracteristicaProductoConNombre(nombre);
	}

	
	
	
	
	
	
	
}
