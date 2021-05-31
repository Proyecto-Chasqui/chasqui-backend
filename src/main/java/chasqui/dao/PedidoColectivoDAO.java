package chasqui.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import chasqui.model.PedidoColectivo;
import chasqui.model_lite.PedidoColectivoStatsByEstado;
import chasqui.model_lite.ProductoPedidoLiteAgrupados;

public interface PedidoColectivoDAO {

	public void guardar(PedidoColectivo p);

	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid,
			Integer grupoID, Date d, Date h, String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro);

	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(Integer grupoid);

	PedidoColectivo obtenerPedidoColectivoPorID(Integer id);

	PedidoColectivoStatsByEstado calcularStatsPedidoColectivoActivo(Integer grupoId);

	List<ProductoPedidoLiteAgrupados> productosPedidoColectivoActivo(Integer grupoId);

	Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedor(Integer vendedorid, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro, String emailAdmin);

	List<PedidoColectivo> obtenerPedidosColectivosDeConEstado(Integer idUsuario, Integer idVendedor,
			List<String> estados);

	Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorConPRPorNombre(Integer vendedorid, Date d,
			Date h, String estadoSeleccionado, Integer zonaId, String puntoRetiro, String emailAdmin);

	public List<PedidoColectivo> obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(Integer idVendedor, Date d,
			Date h, String estadoSeleccionado, Integer zonaId, String prSeleccionado, String queryNodo);

	public void eliminar(PedidoColectivo p);

}
