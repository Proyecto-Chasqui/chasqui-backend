package chasqui.dao;

import java.util.List;

import chasqui.model.Fabricante;

public interface FabricanteDAO {

	public List<Fabricante> obtenerProductoresDe(Integer idVendedor);

}
