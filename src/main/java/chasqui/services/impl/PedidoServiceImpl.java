package chasqui.services.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Point;

import chasqui.aspect.Auditada;
import chasqui.aspect.Dateable;
import chasqui.dao.PedidoColectivoDAO;
import chasqui.dao.PedidoDAO;
import chasqui.dao.ProductoPedidoDAO;
import chasqui.dao.ZonaDAO;
import chasqui.dtos.PaginatedListDTO;
import chasqui.dtos.queries.PedidoQueryDTO;
import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.model_lite.PedidoLite;
import chasqui.model_lite.PedidoStatsLite;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.composer.Constantes;

@Auditada
public class PedidoServiceImpl implements PedidoService {


	@Autowired
	private PedidoDAO pedidoDAO;
	@Autowired
	private ZonaDAO zonaDAO;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private ProductoService productoService;
	@Autowired
  private ProductoPedidoDAO productoPedidoDAO;
	@Autowired
  private PedidoColectivoDAO pedidoColectivoDAO;
	@Autowired
	private ZonaService zonaService;
	@Autowired
	private NotificacionService notificacionService;
	@Autowired
	private Integer cantidadDeMinutosParaExpiracion;

	
	@Override
	public List<Pedido> obtenerPedidosExpirados(Integer idVendedor) {
		return pedidoDAO.obtenerPedidosAbiertosConFechaVencida(idVendedor);
	}
	@Override
	public void eliminarPedidos(List<Pedido> pedidos) {
		for(Pedido p : pedidos) {
			pedidoDAO.eliminar(p);
		}
	}


	@Override
	public void guardar(Pedido p) {
		pedidoDAO.guardar(p);		
	}

	@Override
	public PaginatedListDTO<PedidoLite> obtenerPedidosLite(PedidoQueryDTO query) {
		PaginatedListDTO<PedidoLite> result = new PaginatedListDTO<>();
		List<PedidoLite> list = pedidoDAO.obtenerPedidosLite(query);
		result.setList(list);
		result.setTotal(list.size());

		Integer idColectivo = query.getIdColectivo();
		if(idColectivo != null) {
			List<PedidoStatsLite> stats = pedidoColectivoDAO.calcularPedidosStatsLite(idColectivo);
			result.setList(this.mergePedidoStats(list, stats));
		}
		return result;
	}

	private List<PedidoLite> mergePedidoStats(List<PedidoLite> pedidos, List<PedidoStatsLite> stats) {
		Map<Integer, PedidoStatsLite> map = new HashMap<>();
		for (PedidoStatsLite pedidoStatsLite : stats) {
			map.put(pedidoStatsLite.getId(), pedidoStatsLite);
		}

		for (PedidoLite pedido : pedidos) {
			Integer id = pedido.getId();
			if(map.containsKey(id)) {
				pedido.setStats(map.get(id));
			}
		}
	
		return pedidos;
	}

	@Override
	public List<Pedido> obtenerPedidosDeVendedor(Integer idVendedor) {
		return pedidoDAO.obtenerPedidos(idVendedor);
	}

	@Override
	public List<Pedido> obtenerPedidosIndividuales(Integer idVendedor){
		return pedidoDAO.obtenerPedidosIndividuales(idVendedor);
	}
	
	@Override
	public List<Pedido> obtenerPedidosDeVendedorEnZona(Integer idVendedor, Integer idZona){
		Zona zona = zonaDAO.ObtenerZonaPorID(idVendedor, idZona);
		return pedidoDAO.obtenerPedidosEnDeterminadaZona(idVendedor, zona);
	}

	@Override
	public int totalPedidosParaVendedor(Integer id) {
		return pedidoDAO.obtenerTotalPaginasDePedidosParaVendedor(id);
	}

	@Override
	public List<Pedido> obtenerPedidosDeVendedor(Integer id, Date desde, Date hasta, String estadoSeleccionado) {
		return pedidoDAO.obtenerPedidos(id,desde,hasta,estadoSeleccionado);
		
	}
	
	@Override
	public List<Pedido> obtenerPedidosDeVendedorEnDeterminadaZona(Integer id, Date desde, Date hasta, String estadoSeleccionado,Integer idZona){
		Zona zona = zonaDAO.ObtenerZonaPorID(id, idZona);
		return pedidoDAO.obtenerPedidosEnDeterminadaZona(id,desde,hasta,estadoSeleccionado,zona);
	}

	@Override
	public List<Pedido> obtenerPedidosVencidos() {
		return pedidoDAO.obtenerPedidosVencidos();
	}


	@Override
	public Pedido obtenerPedidosporId(Integer idPedido) {
		return pedidoDAO.obtenerPedidoPorId(idPedido);
	}

	@Override
	public PedidoLite obtenerPedidoLiteActivo (Integer idColectivo, String emailCliente) {
		PedidoLite pedido = pedidoDAO.obtenerPedidoLiteActivo(idColectivo, emailCliente);

		if(pedido != null) {
			ProductoPedidoQueryDTO query = new ProductoPedidoQueryDTO();
			query.setIdPedido(pedido.getId());
			pedido.setProductosPedidos(productoPedidoDAO.obtenerLite(query));
		}

		return pedido;
	}

	
	@Override
	public List<Pedido> obtenerPedidosProximosAVencerEnDeterminadaZona(Integer cantidadDeDias, Integer idVendedor, DateTime fechaCierrePedido, Integer idZona){
		Zona zona = zonaDAO.ObtenerZonaPorID(idVendedor, idZona);
		return pedidoDAO.obtenerPedidosProximosAVencerEnDeterminadaZona(cantidadDeDias, idVendedor, fechaCierrePedido, zona);
    }

	//Servicios que estaban en UsuarioService

	@Override
	public Pedido obtenerPedidoActualDe(String mail, Integer idVendedor) throws PedidoInexistenteException, UsuarioInexistenteException {
		Cliente cliente =(Cliente) usuarioService.obtenerUsuarioPorEmail(mail);
		usuarioService.inicializarPedidos(cliente);
		return cliente.obtenerPedidoActualDe(idVendedor);
	}


	/*
	 * Este método recupera todos los pedidos vigentes del usuario (multicatalogo
	 * (non-Javadoc)
	 * @see chasqui.services.interfaces.UsuarioService#obtenerPedidosVigentesDe(java.lang.String)
	 */
	@Override
	public List<Pedido> obtenerPedidosVigentesEnTodosLosCatalogosPara(String mail) throws UsuarioInexistenteException {
		return ((Cliente) usuarioService.obtenerUsuarioPorEmail(mail)).obtenerPedidosVigentes();
	}

	@Override
	public void crearPedidoIndividualPara(String mail, Integer idVendedor)
			throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException {
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(mail);
		usuarioService.inicializarDirecciones(cliente);
		usuarioService.inicializarPedidos(cliente);
		
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);
		
		validarVendedorParaCreacionDePedido(cliente, vendedor);
		validarPedidoExistente(cliente, vendedor);
		
		

		Pedido p = new Pedido(vendedor, cliente, false, nuevaFechaVencimiento(vendedor.getTiempoVencimientoPedidos()));
		cliente.agregarPedido(p);
		usuarioService.guardarUsuario(cliente);
	}
	
	private DateTime nuevaFechaVencimiento(Integer tiempoVencimiento) {
		DateTime d = new DateTime();
		if(tiempoVencimiento != null) {
		d = d.plusMinutes(tiempoVencimiento);
		}else {
			d =d.plusMinutes(cantidadDeMinutosParaExpiracion);
		}
		return d;
	}
	
	

	@Override
	public void refrescarVencimiento(Integer idPedido, String email) throws UsuarioInexistenteException, PedidoInexistenteException, EstadoPedidoIncorrectoException, VendedorInexistenteException {
		Pedido pedido = pedidoDAO.obtenerPedidoPorId(idPedido);
		Integer tiempoVencimiento= usuarioService.obtenerVendedorPorID(pedido.getIdVendedor()).getTiempoVencimientoPedidos();
		if(pedido == null || !pedido.getCliente().getEmail().equals(email)){
			throw new PedidoInexistenteException("Id incorrecto");
		}
		if(!pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ABIERTO)){
			throw new EstadoPedidoIncorrectoException("El pedido debe estar abierto");
		}
		pedido.setFechaDeVencimiento(this.nuevaFechaVencimiento(tiempoVencimiento));
		pedidoDAO.guardar(pedido);
	}
	
	public void reabrirPedido(Pedido pedido) throws VendedorInexistenteException, EstadoPedidoIncorrectoException{
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(pedido.getIdVendedor());
		pedido.reabrirPedido(nuevaFechaVencimiento(vendedor.getTiempoVencimientoPedidos()));
		pedidoDAO.guardar(pedido);
	}

	@Override
	public Pedido crearPedidoIndividualEnGrupo(GrupoCC grupo, String email, Integer idVendedor)
			throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException {
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
		usuarioService.inicializarPedidos(cliente);
		validarVendedorParaCreacionDePedido(cliente, vendedor);

		Pedido p = new Pedido(vendedor, cliente, true, nuevaFechaVencimiento(vendedor.getTiempoVencimientoPedidos())); 
		p.setPedidoColectivo(grupo.getPedidoActual());
		cliente.agregarPedido(p);
		usuarioService.guardarUsuario(cliente);
		return p;
	}

	@Override
	@Dateable
	public synchronized void agregarProductosAPedido(AgregarQuitarProductoAPedidoRequest request, String email)
			throws UsuarioInexistenteException, ProductoInexistenteException, PedidoVigenteException,
			RequestIncorrectoException, EstadoPedidoIncorrectoException, VendedorInexistenteException {
		
		
		validarRequest(request);
		Pedido p = pedidoDAO.obtenerPedidoPorId(request.getIdPedido());
		Vendedor v = usuarioService.obtenerVendedorPorID(p.getIdVendedor());
		Variante variante = productoService.obtenerVariantePor(request.getIdVariante());
		Integer tiempoVencimiento = usuarioService.obtenerVendedorPorID(p.getIdVendedor()).getTiempoVencimientoPedidos();
		validar(variante, null, request, p);
		
		ProductoPedido pp = new ProductoPedido(variante, request.getCantidad(),variante.getProducto().getFabricante().getNombre());
		if(v.getEstrategiasUtilizadas().isUtilizaIncentivos()) {
			pp.setIncentivo(variante.getIncentivo());
		}
		p.agregarProductoPedido(pp, nuevaFechaVencimiento(tiempoVencimiento));
		p.sumarAlMontoActual(variante.getPrecio(), request.getCantidad());
		variante.reservarCantidad(request.getCantidad());
		
		
		pedidoDAO.guardar(p);
		productoService.modificarVariante(variante);
	}

	
	@Override
	@Dateable
	public synchronized void vencerPedido(Pedido pedido) throws EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException{
		pedido.vencerte();
		productoService.eliminarReservasDe(pedido);
		pedidoDAO.guardar(pedido);
		usuarioService.guardarUsuario(pedido.getCliente());
		
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(pedido.getIdVendedor());
		
		notificacionService.notificarPedidoVencido(pedido.getCliente(), pedido.getFechaCreacion(), pedido, vendedor.getEmail(), vendedor.getNombre());
	}
	
	@Override
	@Dateable
	public synchronized void cancelarPedido(Pedido pedido) throws EstadoPedidoIncorrectoException{
		pedido.cancelar();
		productoService.eliminarReservasDe(pedido);
		usuarioService.guardarUsuario(pedido.getCliente());
	}
	
	@Override
	@Dateable
	public synchronized void cancelarPedidoConfirmado(Pedido pedido) {
		try {
			pedido.cancelar();
			productoService.devolverStockDeProductos(pedido);
			usuarioService.guardarUsuario(pedido.getCliente());
		} catch (EstadoPedidoIncorrectoException e) {
		}

	}
	
	@Override
	@Dateable
	public synchronized void cancelarPedidoPara(String email, Integer idPedido)
			throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, EstadoPedidoIncorrectoException {
		validarRequest(idPedido);

		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
		usuarioService.inicializarPedidos(cliente);

		validarPedidoExistentePara(cliente, idPedido);
		Pedido pedido = cliente.cancelarPedido(idPedido);
		
		productoService.eliminarReservasDe(pedido);
		usuarioService.guardarUsuario(cliente);
	}
	
//	@Override
//	public synchronized void vencerPedidoPara(String email, Integer idPedido)
//			throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, EstadoPedidoIncorrectoException {
//		validarRequest(idPedido);
//
//		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
//		usuarioService.inicializarPedidos(cliente);
//
//		validarPedidoExistentePara(cliente, idPedido);
//		Pedido pedido = cliente.vencerPedido(idPedido);
//		
//		productoService.eliminarReservasDe(pedido);
//		usuarioService.guardarUsuario(cliente);
//	}

	@Override
	@Dateable
	public synchronized void eliminarProductoDePedido(AgregarQuitarProductoAPedidoRequest request, String email)
			throws ProductoInexistenteException, RequestIncorrectoException, PedidoVigenteException, UsuarioInexistenteException {

		validarRequest(request);
		Pedido pedido = pedidoDAO.obtenerPedidoPorId(request.getIdPedido());
		
		Variante variante = productoService.obtenerVariantePor(request.getIdVariante());
		validarParaEliminar(variante, pedido, request);
		
		this.eliminarProductoEnPedido(request.getIdVariante(), variante.getPrecio(), pedido,
				request.getCantidad());
		
		variante.eliminarReserva(request.getCantidad());
		
		pedidoDAO.guardar(pedido);
		
		
		productoService.modificarVariante(variante);
	}
	
	public void eliminarProductoEnPedido(Integer idVariante, Double precio, Pedido p, Integer cantidad) {
		ProductoPedido pp = p.encontrarProductoPedido(idVariante);
		if (cantidad < pp.getCantidad()) {
			pp.restar(cantidad);
		} else {
			p.eliminar(pp);
		}
		p.restarAlMontoActual(precio, cantidad);
	}
	


	@Override
	@Dateable
	public synchronized void confirmarPedido(String email, ConfirmarPedidoRequest request)
			throws  RequestIncorrectoException, DomicilioInexistenteException, EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException {

		validarRequest(request);

		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
		usuarioService.inicializarDirecciones(cliente);
		usuarioService.inicializarPedidos(cliente);
		//usuarioService.inicializarHistorial(cliente);
		//suarioService.inicializarColecciones(cliente);
		validarConfirmacionDePedidoPara(cliente, request);
		
		Pedido pedido = cliente.encontrarPedidoConId(request.getIdPedido());
		pedido.setComentario(request.getComentario());
		pedido.setRespuestasAPreguntas(buildMap(request.getOpcionesSeleccionadas()));
		if(request.getIdZona()!= null){
			pedido.setZona(zonaService.obtenerZonaPorId(request.getIdZona()));
		}
		Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(pedido.getIdVendedor());
		usuarioService.inicializarListasDe(vendedor);
		
		vendedor.descontarStockYReserva(pedido);
		cliente.confirmarPedido(request.getIdPedido(),request.getIdDireccion(),request.getIdPuntoDeRetiro());
		if(request.getIdDireccion() !=null) {
			Direccion d = pedido.getDireccionEntrega();
			Point geoubicacion = d.getGeoUbicacion();
			pedido.setZona(zonaService.obtenerZonaDePertenenciaDeDireccion(geoubicacion,pedido.getIdVendedor()));
		}		
		
		notificacionService.enviarAClienteSuPedidoConfirmado(vendedor.getEmail(), email, pedido);
		usuarioService.guardarUsuario(cliente);
		usuarioService.guardarUsuario(vendedor);
	}

	
	//----------------------Utils

	private Map<String, String> buildMap(List<OpcionSeleccionadaRequest> opcionesSeleccionadas) {
		Map<String,String> map = new HashMap<String,String>();
		if(opcionesSeleccionadas != null){
			for(OpcionSeleccionadaRequest o : opcionesSeleccionadas){
				map.put(o.getNombre(), o.getOpcionSeleccionada());
			}
		}
		return map;
	}


	private void validarRequest(AgregarQuitarProductoAPedidoRequest request) throws RequestIncorrectoException {
		if (request.getIdPedido() == null) {
			throw new RequestIncorrectoException("El id De pedido no debe ser null");
		}
		if (request.getIdVariante() == null) {
			throw new RequestIncorrectoException("El id de variante no debe ser null");
		}
		if (request.getCantidad() == null || request.getCantidad() <= 0) {
			throw new RequestIncorrectoException("La cantidad debe ser mayo  debe ser mayor a 0");
		}
	}

	
	private void validarConfirmacionDePedidoPara(Cliente c, ConfirmarPedidoRequest request)
			throws DomicilioInexistenteException, PedidoInexistenteException, EstadoPedidoIncorrectoException {
		if (!c.contienePedido(request.getIdPedido())) {
			throw new PedidoInexistenteException(
					"El usuario: " + c.getUsername() + " no posee un pedido vigente con el ID otorgado");
		}
		
		if(request.getIdDireccion() ==null && request.getIdPuntoDeRetiro() ==null){
			throw new EstadoPedidoIncorrectoException("El pedido le falta id de punto de retiro o id direccion");
		}
		
		if(request.getIdDireccion() !=null && request.getIdPuntoDeRetiro() !=null){
			throw new EstadoPedidoIncorrectoException("El pedido no puede poseer un id de punto de retiro y id direccion");
		}
		
		if(request.getIdDireccion()!=null){
			if (!c.contieneDireccion(request.getIdDireccion())) {
				throw new DomicilioInexistenteException(
						"El usuario: " + c.getUsername() + " no posee una direccion con el ID otorgado");
			}
		}
	}


	private void validarPedidoExistentePara(Cliente c, Integer idPedido) throws PedidoVigenteException {
		if (!c.contienePedido(idPedido)) {
			throw new PedidoVigenteException(
					"El usuario no posee el pedido con ID:" + idPedido + " o el mismo no se encuentra vigente");
		}

	}

	private void validarParaEliminar(Variante v, Pedido p, AgregarQuitarProductoAPedidoRequest request)
			throws ProductoInexistenteException, RequestIncorrectoException, PedidoVigenteException {
		validacionesGenerales(v, request);
		
		Pedido pedido = this.obtenerPedidosporId(request.getIdPedido());
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)) {
			throw new PedidoVigenteException("El pedido se encuentra vencido y no es posible modificarlo");
		}
		
		if (!this.contieneProductoEnPedido(v, p)) {
			throw new ProductoInexistenteException(
					"El usuario no tiene el producto con ID " + request.getIdVariante() + " en el pedido");
		}
		if (!this.contieneCantidadDeProductoEnPedido(v, p, request.getCantidad())) {
			throw new ProductoInexistenteException(
					"No se puede quitar mas cantidad de un producto de la que el usuario posee en su pedido");
		}
		if(!pedido.getIdVendedor().equals(v.getIdVendedor())) {
			throw new ProductoInexistenteException(
					"No se puede quitar un producto que no es del vendedor");
		}
	}
	
	private boolean contieneProductoEnPedido(Variante v, Pedido p) {
		ProductoPedido pp = p.encontrarProductoPedido(v.getId());
		if (pp != null && pp.getIdVariante().equals(v.getId())) {
			return true;
		}
		return false;
	}
	
	private boolean contieneCantidadDeProductoEnPedido(Variante v, Pedido p, Integer cantidad) {
		ProductoPedido pp = p.encontrarProductoPedido(v.getId());
		if (pp.getIdVariante().equals(v.getId()) && cantidad <= pp.getCantidad()) {
			return true;
		}
		return false;
	}

	private void validar(Variante v, Cliente c, AgregarQuitarProductoAPedidoRequest request, Pedido p)
			throws ProductoInexistenteException, PedidoVigenteException, RequestIncorrectoException {
		//validacionesGenerales(v, c, request);
		if (!v.tieneStockParaReservar(request.getCantidad())) {
			throw new ProductoInexistenteException("El producto no posee más Stock");
		}
		if(!v.getIdVendedor().equals(p.getIdVendedor())) {
			throw new ProductoInexistenteException("El producto no pertenece al vendedor solicitado");
		}
	}

	private void validarRequest(ConfirmarPedidoRequest request) throws RequestIncorrectoException {
		validarRequest(request.getIdPedido());
		validarLargoComentario(request.getComentario());

	}

	private void validarRequest(Integer idPedido) throws RequestIncorrectoException {
		if (idPedido == null || idPedido < 0) {
			throw new RequestIncorrectoException("el id del pedido debe ser mayor a 0");
		}
	}
	
	private void validarLargoComentario(String comentario) throws RequestIncorrectoException{
		if (comentario.length() >= 2000) {
			throw new RequestIncorrectoException("la observacion sobre la direccion es muy larga");
		}
	}

	private void validarPedidoExistente(Cliente c, Vendedor v) throws PedidoVigenteException{
		if (c.contienePedidoVigenteParaVendedor(v.getId())) {
			throw new PedidoVigenteException(
					"El usuario: " + c.getUsername() + " ya posee un pedido vigente para el vendedor brindado");
		}

	}

	private void validarVendedorParaCreacionDePedido(Cliente c, Vendedor v)
			throws UsuarioInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException {
		if (c == null) {
			throw new UsuarioInexistenteException("No se ha encontrado el usuario con el mail otorgado");
		}
		if (v == null || v.getIsRoot()) {
			throw new UsuarioInexistenteException("Vendedor Inexistente");
		}

		//TODO 20170502 se debe controlar algo sobre la fecha de cierre?
//		if (v.getFechaCierrePedido() == null) {
//			throw new ConfiguracionDeVendedorException(
//					"El Vendedor al que se le desea crear un pedido, aún no ha definido la fecha de cierre. No es posible crear el pedido");
//		}

		if (v.getMontoMinimoPedido() == null || v.getMontoMinimoPedido() < 0) {
			throw new ConfiguracionDeVendedorException(
					"El Vendedor al que se le desea crear un pedido, aún no ha definido el monto minimo de compra. No es posible crear el pedido");
		}
		
// EL Pedido puede crearse aunque no tenga direccion configurada
//		if (c.obtenerDireccionPredeterminada() == null) {
//			throw new PedidoVigenteException(
//					"El usuario: " + c.getNickName() + " no posee una direccion predeterminada");
//		}
	}
	
	private void validacionesGenerales(Variante v, AgregarQuitarProductoAPedidoRequest request)
			throws ProductoInexistenteException, PedidoVigenteException, RequestIncorrectoException {
		if (v == null) {
			throw new ProductoInexistenteException("No existe el producto con ID: " + request.getIdVariante());
		}
		Pedido p = this.obtenerPedidosporId(request.getIdPedido());// TODO
		// mejorar
		// (Hackaso)
		//!c.contienePedido(request.getIdPedido()) &&
		if (p == null) {
			throw new PedidoVigenteException("El usuario no posee el pedido con ID:" + request.getIdPedido()
			+ " o el mismo no se encuentra vigente ni cancelado");
		}

//		if (!c.tienePedidoDeVendedor(p.getIdVendedor(), request.getIdPedido())) {
//			throw new RequestIncorrectoException(
//					"El producto no corresponde con el vendedor al que se le hizo el pedido con ID: "
//							+ request.getIdPedido());
//		}
	}
	

	@Override
	public List<Pedido> obtenerPedidosConEstados(String email,Integer idVendedor, List<String> estados) throws UsuarioInexistenteException, RequestIncorrectoException {
		Integer idUsuario = usuarioService.obtenerUsuarioPorEmail(email).getId();
		this.validarEstadosPedido(estados);
		return pedidoDAO.obtenerPedidosDeConEstado(idUsuario, idVendedor, estados);
	}


	private void validarEstadosPedido(List<String> estados) throws RequestIncorrectoException {
		if(estados.size() > Constantes.CANTIDAD_ESTADOS){
			//Cantidad maxima de estados, hay repetidos o invalidos
			throw new RequestIncorrectoException("Estados repetidos o incorrectos: " + estados.toString());
		}
		
		if(estados.size() == 0){
			//No se solicito ningun estado.
			throw new RequestIncorrectoException("No se ha solicitado ningun estado: " + estados.toString());
		}

		for(String estado: estados){
		     if (!estado.matches("^[A-Z]*$")){
		    	 //No son mayusculas
		    	 throw new RequestIncorrectoException("Estados incorrectos: " + estados.toString());
		     }
			
		}
	}


	@Override
	public List<Pedido> obtenerPedidosIndividualesDeVendedor(Integer id) {
		return this.pedidoDAO.obtenerPedidosIndividualesDeVendedor(id);
	}


	@Override
	public Collection<? extends Pedido> obtenerPedidosIndividualesDeVendedor(Integer id, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro, String email) {
		return this.pedidoDAO.obtenerPedidosIndividualesDeVendedor( id, d, h,estadoSeleccionado,zonaId,idPuntoRetiro, email);
	}
	
	@Override
	public Collection<? extends Pedido> obtenerPedidosIndividualesDeVendedorConPRPorNombre(Integer id, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, String nombrePuntoRetiro, String email) {
		return this.pedidoDAO.obtenerPedidosIndividualesDeVendedorPRPorNombre( id, d, h,estadoSeleccionado,zonaId,nombrePuntoRetiro, email);
	}

	@Override
	public void eliminarProductosPedidos(List<ProductoPedido> productoPedido) {
		this.pedidoDAO.eliminarProductosPedidos(productoPedido);
		
	}

}
