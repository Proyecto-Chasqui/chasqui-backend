package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Fabricante;

public interface ProductorService {

	
	@Transactional
	public List<Fabricante>obtenerProductoresDe(Integer idVendedor) throws VendedorInexistenteException;
//	@Transactional
//	public void eliminarProductor(Fabricante f);

	List<Fabricante> obtenerProductores(Integer idVendedor);

	public List<Fabricante> obtenerProductoresPorNombre(Integer id, String busquedaPorNombreProductor) throws VendedorInexistenteException;
	
	public void guardar(Fabricante fabricante);
	
	public void inicializarListasDeProducto(Fabricante fabricante);
	
	public void eliminar(Fabricante fabricante);

	Fabricante obtenerProductorDeConNombreExacto(Integer idVendedor, String busquedaPorNombreProductor)
			throws VendedorInexistenteException;
}
