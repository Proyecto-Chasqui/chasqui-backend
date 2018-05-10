package chasqui.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;

public interface PedidoColectivoDAO {
	
	public void guardar(PedidoColectivo p);

	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid, Integer grupoID,
			Date d, Date h, String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro);

	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(Integer grupoid);

	PedidoColectivo obtenerPedidoColectivoPorID(Integer id);
	
}
