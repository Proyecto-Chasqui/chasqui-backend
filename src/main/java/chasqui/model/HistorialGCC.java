package chasqui.model;

import java.util.ArrayList;
import java.util.List;

import chasqui.view.composer.Constantes;

public class HistorialGCC {

	private Integer id;
	private Integer idGCC;
	private List<PedidoColectivo> pedidosGrupales;
	
	//GETs & SETs 

	public HistorialGCC(Integer id) {
		idGCC = id;
		pedidosGrupales = new ArrayList<PedidoColectivo>();
	}
	
	public HistorialGCC(){
		
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getIdGCC() {
		return idGCC;
	}

	public void setIdGCC(Integer idGCC) {
		this.idGCC = idGCC;
	}

	public List<PedidoColectivo> getPedidosGrupales() {
		return pedidosGrupales;
	}

	public void setPedidosGrupales(List<PedidoColectivo> pedidosGrupales) {
		this.pedidosGrupales = pedidosGrupales;
	}

	public void agregarAHistorial(PedidoColectivo p) {
		pedidosGrupales.add(p);
		
	}
	
	public boolean tienePedidosSinEntregar(){
		boolean result = false;
		for(Integer i=0 ; i < this.pedidosGrupales.size();i++){
			if(!result){
				result = this.pedidosGrupales.get(i).getEstado() == Constantes.ESTADO_PEDIDO_CONFIRMADO;
			}else{
				i = this.pedidosGrupales.size();
			}
		}
		return result;
	}
}
