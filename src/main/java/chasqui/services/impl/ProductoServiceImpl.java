package chasqui.services.impl;

import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.dao.ProductoDAO;
import chasqui.exceptions.CaracteristicaInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.service.rest.request.ByCategoriaRequest;
import chasqui.service.rest.request.ByMedallaRequest;
import chasqui.service.rest.request.ByProductorRequest;
import chasqui.service.rest.request.ByQueryRequest;
import chasqui.service.rest.request.ProductoRequest;
import chasqui.service.rest.request.SinFiltroRequest;
import chasqui.services.interfaces.ProductoService;


@Auditada
public class ProductoServiceImpl implements ProductoService {

	@Autowired
	private ProductoDAO productoDAO;
	

	@Override
	public List<Variante> obtenerVariantesDestacadas(Integer idVendedor) {
		return productoDAO.obtenerDestacadosPorVendedor(idVendedor);
	}
	@Deprecated
	@Override
	public List<Variante> obtenerVariantesPorCategoria(ByCategoriaRequest request) throws RequestIncorrectoException {
		validarCategoriaRequest(request);
		return productoDAO.obtenerVariantesPorCategoria(request.getIdCategoria(),request.getPagina(), request.getCantItems());
	}
	
	@Deprecated
	@Override
	public List<Variante> obtenerVariantesPorProductor(ByProductorRequest request) throws RequestIncorrectoException {
		validarProductorRequest(request);
		return productoDAO.obtenerVariantesPorProductor(request.getIdProductor(), request.getPagina(), request.getCantItems());
	}
	@Deprecated
	@Override
	public List<Variante> obtenerVariantesPorMedalla(ByMedallaRequest request) throws RequestIncorrectoException {
		validarMedallaRequest(request);
		return productoDAO.obtenerVariantesPorMedalla(request.getIdMedalla(), request.getPagina(), request.getCantItems(),request.getIdVendedor());
	}
	
	@Deprecated
	@Override
	public List<Variante> obtenerVariantesSinFiltro(SinFiltroRequest request) throws RequestIncorrectoException {
		validarRequest(request);
		return productoDAO.obtenerVariantesSinFiltro(request.getPagina(),request.getCantItems(),request.getIdVendedor());
	}

	@Override
	public List<Imagen> obtenerImagenesDe(Integer idProducto) {
		return productoDAO.obtenerImagenesDe(idProducto);
	}

	@Override
	public List<Variante> obtenerVariantesPorNombreODescripcion(ByQueryRequest request) throws RequestIncorrectoException {
		validarQueryRequest(request);
		return productoDAO.obtenerVariantesPorNombreODescripcion(request.getQuery(),request.getPagina(),request.getCantItems(),request.getIdVendedor());
	}
	
	@Override
	public Variante obtenerVariantePor(Integer id){
		return productoDAO.obtenervariantePor(id);
	}
	
	@Override
	public Variante obtenerVariantePorCodigoProducto(String codigoProducto, Integer idVendedor){
		return productoDAO.obtenervariantePorCodigoProducto(codigoProducto,idVendedor);
	}

	@Override
	public void modificarVariante(Variante v) {
		productoDAO.modificarVariante(v);		
	}

	@Override
	public void eliminarReservasDe(Pedido p) {
		for(ProductoPedido pp : p.getProductosEnPedido()){
			Variante v = productoDAO.obtenervariantePor(pp.getIdVariante());
			v.eliminarReserva(pp.getCantidad());
			productoDAO.modificarVariante(v);
		}
		
	}

	@Override
	public Caracteristica obtenerMedalla(Integer idMedalla) {
		Caracteristica c = productoDAO.obtenerCaracteristicaPor(idMedalla);
		if( c != null){
			return c;
		}
		throw new CaracteristicaInexistenteException("No existe la medalla con ID: " + idMedalla);
	}
	
	
	
	private void validarCategoriaRequest(ByCategoriaRequest request) throws RequestIncorrectoException{
		validarRequest(request);
		if(request.getIdCategoria() == null){
			throw new RequestIncorrectoException("El idCategoria es obligatorio!");
		}
	}
	
	private void validarRequest(ProductoRequest request) throws RequestIncorrectoException {
		if(!("Down".equals(request.getPrecio()) || "Up".equals(request.getPrecio()))){
			throw new RequestIncorrectoException("El orden debe ser 'Up' o 'Down'");
		}
		if(request.getPagina() == null){
			throw new RequestIncorrectoException("Debe especificar la pagina que se desea obtener!");
		}
		if(request.getCantItems() == null){
			throw new RequestIncorrectoException("Debe especificar la cantidad de resultados");
		}		
	}
	
	private void validarMedallaRequest(ByMedallaRequest request) throws RequestIncorrectoException{
		validarRequest(request);
		if(request.getIdMedalla() == null || request.getIdMedalla() < 0){
			throw new RequestIncorrectoException("El idMedalla no es valido!");
		}
		if(request.getIdVendedor() == null || request.getIdVendedor() < 0 ){
			throw new RequestIncorrectoException("El idVendedor no es valido!");
		}
	}
	
	private void validarProductorRequest(ByProductorRequest request) throws RequestIncorrectoException{
		validarRequest(request);
		if(request.getIdProductor() == null){
			throw new RequestIncorrectoException("El idProductor es obligatorio!");
		}
	}
	
	private void validarQueryRequest(ByQueryRequest request) throws RequestIncorrectoException{
		validarRequest(request);
		if(StringUtils.isEmpty(request.getQuery())){
			throw new RequestIncorrectoException("El valor query es obligatorio!");
		}
		if(request.getIdVendedor() == null ){
			throw new RequestIncorrectoException("El vendedor es obligatorio!");
		}
	}

	@Override
	public Long totalVariantesPorCategoria(ByCategoriaRequest request) {
		return productoDAO.totalVariantesPorCategoria(request.getIdCategoria());
	}

	@Override
	public Long totalVariantesPorProductor(ByProductorRequest request) {
		return productoDAO.totalVariantesPorProductor(request.getIdProductor());
	}

	@Override
	public Long totalVariantesPorMedalla(ByMedallaRequest request) {
		return productoDAO.totalVariantesPorMedalla(request.getIdMedalla());
	}

	@Override
	public Long totalVariantesPorNombreODescripcion(ByQueryRequest request) {
		return productoDAO.totalVariantesPorNombreODescripcion(request.getQuery(),request.getIdVendedor());
	}

	@Override
	public Long totalVariantesSinFiltro(SinFiltroRequest request) {
		return productoDAO.totalVariantesSinFiltro(request.getIdVendedor());
	}

	@Override
	public List<Variante> obtenerProductosConMedallaEnProductor(Integer medallaId) {
		return productoDAO.obtenerProductosConMedallaEnProductor(medallaId);
	}

	@Override
	public List<Variante> obtenerVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, Integer idMedalla,
			Integer idProductor,Integer idSelloProductor, String query, Integer pagina, Integer cantItems) {
		return productoDAO.obtenerVariantesPorMultiplesFiltros(idVendedor, idCategoria, idMedalla, idProductor, idSelloProductor, query, pagina, cantItems);
	}

	@Override
	public Long totalVariantesBajoMultiplesFiltros(Integer idCategoria, Integer idMedalla, Integer idProductor) {
		return productoDAO.totalVariantesBajoMultiplesFiltros(idCategoria, idMedalla, idProductor);
	}
	
	@Override
	public Long totalVariantesPorMultiplesFiltros(Integer idVendedor, Integer idCategoria, Integer idMedalla, Integer idProductor, Integer idSelloProductor, String query){
		return productoDAO.obtenerTotalVariantesPorMultiplesFiltros(idVendedor, idCategoria, idMedalla, idProductor, idSelloProductor, query);
	}
}
