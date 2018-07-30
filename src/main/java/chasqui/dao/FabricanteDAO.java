package chasqui.dao;

import java.util.List;

import chasqui.model.Fabricante;

public interface FabricanteDAO {

	public List<Fabricante> obtenerProductoresDe(Integer idVendedor);

	public List<Fabricante> obtenerProductoresDeConNombre(Integer idVendedor, String nombre);

	public void guardar(Fabricante fabricante);

	Fabricante inicializarlistasDeProductos(Fabricante fabricante);

	public void delete(Fabricante fabricante);

}
