package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.RequestIncorrectoException;
import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Pedido;
import chasqui.model.Variante;
import chasqui.service.rest.request.ByCategoriaRequest;
import chasqui.service.rest.request.ByMedallaRequest;
import chasqui.service.rest.request.ByProductorRequest;
import chasqui.service.rest.request.ByQueryRequest;
import chasqui.service.rest.request.SinFiltroRequest;

public interface ProductoService {
	
	@Transactional
	public List<Variante>obtenerVariantesPorCategoria(ByCategoriaRequest request) throws RequestIncorrectoException;
	@Transactional
	public List<Variante> obtenerVariantesPorProductor(ByProductorRequest request) throws RequestIncorrectoException;
	@Transactional
	public List<Variante> obtenerVariantesPorMedalla(ByMedallaRequest request) throws RequestIncorrectoException;
	@Transactional
	public List<Imagen> obtenerImagenesDe(Integer idProducto);
	@Transactional
	public List<Variante> obtenerVariantesPorNombreODescripcion(ByQueryRequest request) throws RequestIncorrectoException ;
	@Transactional
	public Variante obtenerVariantePor(Integer id);
	@Transactional
	public void modificarVariante(Variante v);
	@Transactional
	public void eliminarReservasDe(Pedido p);
	@Transactional
	public Caracteristica obtenerMedalla(Integer idMedalla);
	
	@Transactional
	public Long totalVariantesPorCategoria(ByCategoriaRequest request);
	@Transactional
	public Long totalVariantesPorProductor(ByProductorRequest request);
	@Transactional
	public Long totalVariantesPorMedalla(ByMedallaRequest request);
	@Transactional
	public Long totalVariantesPorNombreODescripcion(ByQueryRequest request);
	@Transactional
	public List<Variante> obtenerVariantesDestacadas(Integer idVendedor);
  //TODO (Mara, 2017.01.17) No debe ser transactional?
	public List<Variante> obtenerVariantesSinFiltro(SinFiltroRequest request) throws RequestIncorrectoException;
	@Transactional
	public Long totalVariantesSinFiltro(SinFiltroRequest request) throws RequestIncorrectoException;
	
	public List<Variante> obtenerProductosConMedallaEnProductor(Integer medallaId);
	
	public Long totalVariantesBajoMultiplesFiltros(Integer idCategoria, Integer idMedalla, Integer idProductor);
	public List<Variante> obtenerVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, Integer idMedalla,
			Integer idProductor,Integer idSelloProductor, String query,Integer pagina, Integer cantItems);
	
	public Long totalVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, Integer idMedalla,
			Integer idProductor, Integer idSelloProductor, String query);
	Variante obtenerVariantePorCodigoProducto(String codigoProducto, Integer idVendedor);
	
	
}
