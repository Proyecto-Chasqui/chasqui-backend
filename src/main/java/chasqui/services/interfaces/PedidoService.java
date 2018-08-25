package chasqui.services.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;

public interface PedidoService {

	//@Transactional
	// public List<Pedido>obtenerPedidosProximosAVencer(Integer
	// cantidadDeDias,Integer idVendedor,DateTime fechaCierrePedido);

	public List<Pedido> obtenerPedidosDeVendedor(Integer idVendedor);

	@Transactional
	public void guardar(Pedido p);

	public int totalPedidosParaVendedor(Integer id);

	public List<Pedido> obtenerPedidosDeVendedor(Integer id, Date desde, Date hasta, String estadoSeleccionado);

	/*
	 * Pedidos en estado VENCIDO (ya han sido procesados por quartz)
	 */
	public List<Pedido> obtenerPedidosVencidos();

	/*
	 * Pedidos en estado ABIERTO que han expirado (Es para quartz)
	 */
	List<Pedido> obtenerPedidosExpirados();

	List<Pedido> obtenerPedidosProximosAVencerEnDeterminadaZona(Integer cantidadDeDias, Integer idVendedor,
			DateTime fechaCierrePedido, Integer idZona);

	List<Pedido> obtenerPedidosDeVendedorEnDeterminadaZona(Integer id, Date desde, Date hasta,
			String estadoSeleccionado, Integer idZona);

	List<Pedido> obtenerPedidosDeVendedorEnZona(Integer idVendedor, Integer idZona);

	public Pedido obtenerPedidosporId(Integer idPedido);

	// Servicios que estaban en usuario
	@Transactional
	public Pedido obtenerPedidoActualDe(String mail, Integer idVendedor)
			throws PedidoInexistenteException, UsuarioInexistenteException;

	@Transactional
	public void crearPedidoIndividualPara(String mail, Integer idVendedor) throws ConfiguracionDeVendedorException,
			PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException;

	@Transactional
	public void agregarProductosAPedido(AgregarQuitarProductoAPedidoRequest request, String email)
			throws UsuarioInexistenteException, ProductoInexistenteException, PedidoVigenteException,
			RequestIncorrectoException, EstadoPedidoIncorrectoException;

	@Transactional
	public void eliminarProductoDePedido(AgregarQuitarProductoAPedidoRequest request, String email)
			throws ProductoInexistenteException, RequestIncorrectoException, PedidoVigenteException,
			UsuarioInexistenteException;

	@Transactional
	public List<Pedido> obtenerPedidosVigentesEnTodosLosCatalogosPara(String mail) throws UsuarioInexistenteException;

	@Transactional
	Pedido crearPedidoIndividualEnGrupo(GrupoCC grupo, String email, Integer idVendedor)
			throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException,
			VendedorInexistenteException;

	@Transactional
	void confirmarPedido(String email, ConfirmarPedidoRequest request)
			throws RequestIncorrectoException, DomicilioInexistenteException, EstadoPedidoIncorrectoException,
			UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException;

	/**
	 * Este método es ejecutado automáticamente con Quartz. Cambia de estado el pedido a VENCIDO
	 * @param pedido es un pedido abierto cuya fecha de vencimiento ya se alcanzó.
	 * @throws EstadoPedidoIncorrectoException
	 * @throws UsuarioInexistenteException
	 * @throws VendedorInexistenteException 
	 */
	@Transactional
	void vencerPedido(Pedido pedido) throws EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException;

	@Transactional
	public List<Pedido> obtenerPedidosConEstados(String mail, Integer idVendedor, List<String> estados) throws UsuarioInexistenteException, RequestIncorrectoException;

	void cancelarPedido(Pedido pedido) throws EstadoPedidoIncorrectoException;

	void cancelarPedidoPara(String email, Integer idPedido) throws PedidoVigenteException, RequestIncorrectoException,
			UsuarioInexistenteException, EstadoPedidoIncorrectoException;

	List<Pedido> obtenerPedidosIndividuales(Integer idVendedor);

	public List<Pedido> obtenerPedidosIndividualesDeVendedor(Integer id);

	public Collection<? extends Pedido> obtenerPedidosIndividualesDeVendedor(Integer id, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro, String email);

	public void refrescarVencimiento(Integer idPedido, String email) throws UsuarioInexistenteException, PedidoInexistenteException, EstadoPedidoIncorrectoException;
}
