package chasqui.dao;

import java.util.List;

import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Variante;

public interface ProductoDAO {
	@Deprecated
	public List<Variante> obtenerVariantesPorCategoria( Integer idCategoria, Integer pagina, Integer cantidadDeItems);
	@Deprecated
	public List<Variante> obtenerVariantesPorProductor(Integer idProductor, Integer pagina, Integer cantItems);
	@Deprecated
	public List<Variante> obtenerVariantesPorMedalla(Integer idMedalla, Integer pagina, Integer cantItems,Integer idVendedor);
	
	public List<Imagen> obtenerImagenesDe(Integer idVariante);
	
	public List<Variante> obtenerVariantesPorNombreODescripcion(String param,Integer pagina,Integer cantItems,Integer idVendedor);
	
	public List<Variante> obtenerDestacadosPorVendedor(Integer idVendedor);

	public Variante obtenervariantePor(Integer id);

	public void modificarVariante(Variante v);

	public Caracteristica obtenerCaracteristicaPor(Integer idMedalla);
	
	public Long totalVariantesPorCategoria(Integer idCategoria);
	
	public Long totalVariantesPorProductor(Integer idProductor);
	
	public Long totalVariantesPorMedalla(Integer idMedalla);
	
	public Long totalVariantesPorNombreODescripcion(String query,Integer idVendedor);

	public Long totalVariantesSinFiltro(Integer idVendedor);
	@Deprecated
	public List<Variante> obtenerVariantesSinFiltro(Integer pagina, Integer cantItems, Integer idVendedor);
	
	public List<Variante> obtenerProductosConMedallaEnProductor(Integer medallaId);

	Long totalVariantesBajoMultiplesFiltros(Integer idCategoria, Integer idMedalla, Integer idProductor);
	
	public List<Variante> obtenerVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, Integer idMedalla,
			Integer idProductor, Integer idSelloProductor, String query, Integer pagina, Integer cantidadDeItems);


}
