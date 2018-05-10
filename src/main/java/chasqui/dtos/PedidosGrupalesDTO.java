package chasqui.dtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chasqui.model.GrupoCC;
import chasqui.model.PedidoColectivo;

public class PedidosGrupalesDTO {
	private List<PedidoColectivo> pedidos = new ArrayList<PedidoColectivo>();
	//<IdPedidoColectivo><Grupo>
	private Map<Integer,GrupoCC> pedidoGrupo= new HashMap<Integer,GrupoCC>();
	
	public PedidosGrupalesDTO(List<PedidoColectivo> pPedidos,Map<Integer,GrupoCC> pPedidoGrupo ){
		this.pedidos = pPedidos;
		this.pedidoGrupo = pPedidoGrupo;
	}

	public List<PedidoColectivo> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<PedidoColectivo> pedidos) {
		this.pedidos = pedidos;
	}

	public Map<Integer, GrupoCC> getPedidoGrupo() {
		return pedidoGrupo;
	}

	public void setPedidoGrupo(Map<Integer, GrupoCC> pedidoGrupo) {
		this.pedidoGrupo = pedidoGrupo;
	}
	
}
