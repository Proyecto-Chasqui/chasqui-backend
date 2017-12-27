package chasqui.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.VendedorDAO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.CatalogoService;

public class CatalogoServiceImpl implements CatalogoService {
	@Autowired
	VendedorDAO vendedorDAO;
	
	@Override
	public List<Vendedor> obtenerVendedor(String url) throws VendedorInexistenteException {
		Vendedor v = vendedorDAO.obtenerVendedorPorURL(url);
		if(v == null){
			throw new VendedorInexistenteException("No existe el vendedor: "+url );
		}
		List<Vendedor> lista = new ArrayList<Vendedor>();
		lista.add(v);
		
		return lista;
	}

}
