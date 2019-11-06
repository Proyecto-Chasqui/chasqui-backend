package chasqui.dao;

import java.util.List;

import chasqui.model.PreguntaDeConsumo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;

public interface VendedorDAO {
	
	public List<Vendedor> obtenerVendedores();

	public Vendedor obtenerVendedor(String nombreVendedor);

	public Vendedor obtenerVendedorPorURL(String url);

	public List<PuntoDeRetiro> obtenerPuntosDeRetiroDeVendedor(Integer idVendedor);

	Vendedor obtenerVendedorPorNombreCorto(String nombreCorto);

	public Vendedor obtenerVendedorPorId(Integer idVendedor);
	
	public List<PreguntaDeConsumo> obtenerPreguntasDeConsumoIndividuales(Integer idVendedor);
	
	public List<PreguntaDeConsumo> obtenerPreguntasDeConsumoColectivas(Integer idVendedor);
	
	public Vendedor obtenerVendedorRoot(String username);

	public List<Vendedor> obtenerVendedoresConTags(String nombre, List<Integer> idsTagsTipoOrganizacion,
			List<Integer> idsTagsTipoProducto, List<Integer> idsTagsZonaDeCobertura);

}
