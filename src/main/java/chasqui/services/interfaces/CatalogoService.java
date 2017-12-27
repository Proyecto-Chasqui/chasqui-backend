package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Vendedor;

public interface CatalogoService {

	
	@Transactional
	public List<Vendedor> obtenerVendedor(String url) throws VendedorInexistenteException;
}
