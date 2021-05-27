package chasqui.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

import chasqui.dtos.queries.PedidoQueryDTO;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.Zona;
import chasqui.model_lite.PedidoLite;

public interface PedidoDAO {

	public List<Pedido> obtenerPedidosProximosAVencer(Integer cantidadDeDias, Integer idVendedor,
			DateTime fechaCierrePedido);

	public void guardar(Pedido p);

	public List<Pedido> obtenerPedidos(Integer idVendedor);

	public List<PedidoLite> obtenerPedidosLite(PedidoQueryDTO query);

	public PedidoLite obtenerPedidoLiteActivo (Integer idColectivo, String emailCliente);

	public Integer obtenerTotalPaginasDePedidosParaVendedor(Integer id);

	public List<Pedido> obtenerPedidos(Integer id, Date desde, Date hasta, String estadoSeleccionado);

	public List<Pedido> obtenerPedidosAbiertosConFechaVencida(Integer idVendedor);

	public List<Pedido> obtenerPedidosProximosAVencerEnDeterminadaZona(Integer cantidadDeDias, Integer idVendedor,
			DateTime fechaCierrePedido, Zona Zona);

	List<Pedido> obtenerPedidosEnDeterminadaZona(Integer idVendedor, Zona zona);

	List<Pedido> obtenerPedidosEnDeterminadaZona(Integer idVendedor, Date desde, Date hasta, String estadoSeleccionado,
			Zona zona);

	Pedido obtenerPedidoPorId(Integer idPedido);

	List<Pedido> obtenerPedidosVencidos();

	public List<Pedido> obtenerPedidosDeConEstado(Integer idUsuario, Integer idVendedor, List<String> estados);

	List<Pedido> obtenerPedidosIndividuales(Integer idVendedor);

	public List<Pedido> obtenerPedidosIndividualesDeVendedor(Integer id);

	public Collection<? extends Pedido> obtenerPedidosIndividualesDeVendedor(Integer id, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoDeRetiro, String email);

	List<Pedido> obtenerPedidosIndividualesDeVendedorPRPorNombre(Integer idVendedor, Date desde, Date hasta,
			String estadoSeleccionado, Integer zonaId, String idPuntoRetiro, String email);

	public void eliminar(Pedido p);

	@Transactional
	public void eliminarProductosPedidos(List<ProductoPedido> productoPedido);

}
