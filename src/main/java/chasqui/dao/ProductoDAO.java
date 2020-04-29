package chasqui.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Producto;
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
	
	/**
	 * @Deprecated usar obtenerTotalVariantesPorMultiplesFiltros
	 */
	@Deprecated
	Long totalVariantesBajoMultiplesFiltros(Integer idCategoria, Integer idMedalla, Integer idProductor);
	
	/**
	 * @return Obtiene el total de productos (objeto Variante) que resultarian de ejecutar una busqueda con los mismos filtros.
	 * 
	 * @param idVendedor es el id del vendedor al que pertenecen los productos.
	 * @param idCategoria es el id de la categoria a la que pertenecen los productos. Si es null no filtra por categoria.
	 * @param idMedalla es el id del sello de producto al que pertenecen los productos. Si es null no filtra por sello de producto.
	 * @param idProductor es el id del productor al que pertenecen los productos. Si es null no filtra por productor.
	 * @param idSelloProductor es el id del sello de productor al que pertenecen los productos. Si es null no filtra por sello de productor.
	 * @param query es la query es el filtro sobre el nombre de producto. Si es null no filtra por nombre de producto
	 * 
	 * @inheritDoc
	 */
	public Long obtenerTotalVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, List<Integer> idsSellosProducto,
			Integer idProductor, List<Integer> idsSellosProductor, String query);

	Variante obtenervariantePorCodigoProducto(String codigoProducto, Integer idVendedor);

	public List<Variante> obtenerVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, List<Integer> idsSellosProducto,
			Integer idProductor,  List<Integer> idsSellosProductor, String query, Integer pagina, Integer cantidadDeItems,
			Integer numeroDeOrden);
	public void eliminarVariante(Variante variante);
	List<Variante> obtenerTodasLasVariantes(Integer idVendedor);
	@Transactional
	public void eliminarProducto(Producto producto);
}
