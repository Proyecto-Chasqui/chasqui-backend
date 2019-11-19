package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;

public interface VendedorService {

	
	@Transactional
	public List<Vendedor> obtenerVendedores();
	
	@Transactional
	public Vendedor obtenerVendedor(String nombreVendedor) throws VendedorInexistenteException;
	
	@Transactional
	public List<PuntoDeRetiro> obtenerPuntosDeRetiroDeVendedor(Integer idVendedor);

	@Transactional
	Vendedor obtenerVendedorPorNombreCorto(String nombreCorto) throws VendedorInexistenteException;
	@Transactional
	Vendedor obtenerVendedorPorId(Integer idVendedor) throws VendedorInexistenteException;
	@Transactional
	List<PreguntaDeConsumo> obtenerPreguntasColectivas(Integer idVendedor);
	@Transactional
	List<PreguntaDeConsumo> obtenerPreguntasIndividuales(Integer idVendedor);

	public List<Vendedor> obtenerVendedoresConTags(String nombre, List<Integer> idsTagsTipoOrganizacion,
			List<Integer> idsTagsTipoProducto, List<Integer> idsTagsZonaDeCobertura, boolean entregaADomicilio, boolean usaPuntoDeRetiro, boolean usaEstrategiaGrupos, boolean usaEstrategiaIndividual, boolean usaEstrategiaNodos);

}
