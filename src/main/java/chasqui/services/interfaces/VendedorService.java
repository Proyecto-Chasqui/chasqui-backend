package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Vendedor;

public interface VendedorService {

	
	@Transactional
	public List<Vendedor> obtenerVendedores();
	
	@Transactional
	public Vendedor obtenerVendedor(String nombreVendedor) throws VendedorInexistenteException;

	Vendedor obtenerVendedorPorNombreCorto(String nombreCorto) throws VendedorInexistenteException;
}
