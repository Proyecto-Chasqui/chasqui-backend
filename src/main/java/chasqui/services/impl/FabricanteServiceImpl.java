package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.dao.FabricanteDAO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Fabricante;
import chasqui.services.interfaces.ProductorService;

@Auditada
public class FabricanteServiceImpl implements ProductorService{

	@Autowired
	FabricanteDAO fabricanteDAO;

	@Override
	public List<Fabricante> obtenerProductoresDe(Integer idVendedor) throws VendedorInexistenteException {
		List<Fabricante> productores = fabricanteDAO.obtenerProductoresDe(idVendedor);
		if(productores == null || productores.isEmpty() ){
			throw new VendedorInexistenteException();
		}
		return productores;
	}
	
	
	@Override
	public List<Fabricante> obtenerProductores(Integer idVendedor) {
		return fabricanteDAO.obtenerProductoresDe(idVendedor);
	}


	@Override
	public List<Fabricante> obtenerProductoresPorNombre(Integer idVendedor, String busquedaPorNombreProductor) throws VendedorInexistenteException {
		List<Fabricante> productores = fabricanteDAO.obtenerProductoresDeConNombre(idVendedor,busquedaPorNombreProductor);
		return productores;
	}
	
	@Override
	public Fabricante obtenerProductorDeConNombreExacto(Integer idVendedor, String busquedaPorNombreProductor) throws VendedorInexistenteException {
		return fabricanteDAO.obtenerProductorDeConNombreExacto(idVendedor,busquedaPorNombreProductor);
	}
	
	@Override
	public void guardar(Fabricante fabricante) {
		fabricanteDAO.guardar(fabricante);
	}


	@Override
	public void inicializarListasDeProducto(Fabricante fabricante) {
		fabricanteDAO.inicializarlistasDeProductos(fabricante);
		
	}


	@Override
	public void eliminar(Fabricante fabricante) {
		fabricanteDAO.delete(fabricante);
		
	}


	@Override
	public void guardarProductores(List<Fabricante> productores) {
		fabricanteDAO.guardarProductores(productores);
		
	}


	@Override
	public void eliminarProductores(List<Fabricante> productores) {
		for(Fabricante p : productores) {
			fabricanteDAO.delete(p);
		}
		
	}

}
