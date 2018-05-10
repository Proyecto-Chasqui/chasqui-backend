package chasqui.services.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;

public interface PedidoColectivoService {
	
	@Transactional
	public void guardarPedidoColectivo(PedidoColectivo pedidoColectivo);
	
	@Transactional
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid, Integer grupoID,
			Date d, Date h, String estadoSeleccionado, Integer zonaId,Integer idPuntoRetiro);

	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(Integer id);

	PedidoColectivo obtenerPedidoColectivoPorID(Integer id);
	
}
