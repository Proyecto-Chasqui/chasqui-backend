package chasqui.services.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.PedidoColectivoDAO;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.services.interfaces.PedidoColectivoService;


public class PedidoColectivoServiceImpl implements PedidoColectivoService{
	
	@Autowired
	PedidoColectivoDAO pedidoColectivoDao;

	@Override
	public void guardarPedidoColectivo(PedidoColectivo pedidoColectivo) {
		pedidoColectivoDao.guardar(pedidoColectivo);
		
	}
	@Override
	public PedidoColectivo obtenerPedidoColectivoPorID(Integer id){
		return this.pedidoColectivoDao.obtenerPedidoColectivoPorID(id);
	}
	
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid, Integer grupoID, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro) {
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeVendedorDeGrupo( vendedorid, grupoID, d, h,estadoSeleccionado, zonaId,idPuntoRetiro);
	}
	
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedor(Integer vendedorid, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, Integer idPuntoRetiro,String emailadmn) {
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeVendedor( vendedorid, d, h,estadoSeleccionado, zonaId,idPuntoRetiro,emailadmn);
	}
	
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorConPRConNombre(Integer vendedorid, Date d, Date h,
			String estadoSeleccionado, Integer zonaId, String puntoRetiro,String emailadmn) {
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeVendedorConPRPorNombre( vendedorid, d, h,estadoSeleccionado, zonaId,puntoRetiro,emailadmn);
	}

	@Override
	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(Integer grupoid) {
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeGrupo(grupoid);
	}
	
	@Override
	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupoConEstado(Integer idUsuario, Integer idGrupo, List<String> estados){
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeConEstado(idUsuario,idGrupo, estados);
	}
	@Override
	public List<PedidoColectivo>  obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(Integer idVendedor,
			Date d, Date h, String estadoSeleccionado, Integer zonaId, String prSeleccionado, String email) {
		return this.pedidoColectivoDao.obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(idVendedor, d, h, estadoSeleccionado, zonaId, prSeleccionado, email);
	}
	@Override
	public void eliminarPedidosColectivos(Collection<? extends PedidoColectivo> pedidosColectivosDeVendedor) {
		for(PedidoColectivo p : pedidosColectivosDeVendedor) {
			this.pedidoColectivoDao.eliminar(p);
		}
		
	}


}
