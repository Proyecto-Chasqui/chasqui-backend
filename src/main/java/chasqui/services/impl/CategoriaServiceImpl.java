package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.dao.CategoriaDAO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Categoria;
import chasqui.services.interfaces.CategoriaService;

@Auditada
public class CategoriaServiceImpl implements CategoriaService {

	@Autowired
	private CategoriaDAO categoriaDAO;

	@Override
	public List<Categoria> obtenerCategoriasDe(Integer idVendedor) throws VendedorInexistenteException {
		List<Categoria> resultado = categoriaDAO.obtenerCategoriasDe(idVendedor);
		if (resultado == null || resultado.size() == 0) {
			throw new VendedorInexistenteException(idVendedor.toString());
		}
		return resultado;
	}
	
	@Override
	public Categoria obtenerCategoriaConNombreDe(String nombre, Integer idVendedor) throws VendedorInexistenteException {
		return categoriaDAO.obtenerCategoriaConNombreDe(nombre, idVendedor);
	}
	
	

}
