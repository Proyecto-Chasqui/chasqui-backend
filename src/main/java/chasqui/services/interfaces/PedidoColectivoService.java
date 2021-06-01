package chasqui.services.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.PedidoColectivo;
import chasqui.model_lite.PedidoColectivoStatsByEstado;
import chasqui.model_lite.PedidoStatsLite;
import chasqui.model_lite.ProductoPedidoLiteAgrupados;

public interface PedidoColectivoService {
	
	@Transactional
	public void guardarPedidoColectivo(PedidoColectivo pedidoColectivo);
	
	@Transactional
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid, Integer grupoID,
			Date d, Date h, String estadoSeleccionado, Integer zonaId,Integer idPuntoRetiro);

	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(Integer id);

	PedidoColectivo obtenerPedidoColectivoPorID(Integer id);

	PedidoColectivoStatsByEstado calcularStatsPedidoColectivoActivo(Integer grupoId);

	List<PedidoStatsLite> calcularPedidosStatsLite(Integer grupoId);

	List<ProductoPedidoLiteAgrupados> productosPedidoColectivoActivo(Integer grupoId);

	Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedor(Integer vendedorid, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro, String emailAdmin);

	List<PedidoColectivo> obtenerPedidosColectivosDeGrupoConEstado(Integer idUsuario, Integer idVendedor,
			List<String> estados);

	Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorConPRConNombre(Integer vendedorid, Date d,
			Date h, String estadoSeleccionado, Integer zonaId, String puntoRetiro, String emailadmn);

	public List<PedidoColectivo>  obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(Integer id,
			Date d, Date h, String estadoSeleccionado, Integer zonaId, String prSeleccionado, String queryNodo);
	@Transactional
	public void eliminarPedidosColectivos(Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedor);

	
}
