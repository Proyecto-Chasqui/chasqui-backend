package chasqui.dao;

import java.util.List;

import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;

public interface VendedorDAO {
	
	public List<Vendedor> obtenerVendedores();

	public Vendedor obtenerVendedor(String nombreVendedor);

	public Vendedor obtenerVendedorPorURL(String url);

	public List<PuntoDeRetiro> obtenerPuntosDeRetiroDeVendedor(Integer idVendedor);

	Vendedor obtenerVendedorPorNombreCorto(String nombreCorto);

}
