package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Categoria;

public interface CategoriaService {

	
	@Transactional
	public List<Categoria> obtenerCategoriasDe(Integer idVendedor) throws VendedorInexistenteException;

	Categoria obtenerCategoriaConNombreDe(String nombre, Integer idVendedor) throws VendedorInexistenteException;



}
