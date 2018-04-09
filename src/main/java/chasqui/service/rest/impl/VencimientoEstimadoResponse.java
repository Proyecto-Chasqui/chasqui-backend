package chasqui.service.rest.impl;

import chasqui.model.Pedido;

public class VencimientoEstimadoResponse {

	String vencimientoEstimado;
	
	public VencimientoEstimadoResponse(Pedido pedido){
	
		if(pedido.getPerteneceAPedidoGrupal() || pedido.getFechaModificacion() ==  null){
			Integer max = Integer.MAX_VALUE;
			vencimientoEstimado = max.toString();
		}else{
			Long vencimientoEnMillis = pedido.getFechaDeVencimiento().getMillis();
			Long modificacionEnMillis = pedido.getFechaModificacion().getMillis();	
			Long millis = vencimientoEnMillis - modificacionEnMillis;
			Long minutes = ((millis / (1000*60)) % 60);
			vencimientoEstimado = minutes.toString();
		}
	}

	public String getVencimientoEstimado() {
		return vencimientoEstimado;
	}

	public void setVencimientoEstimado(String vencimientoEstimado) {
		this.vencimientoEstimado = vencimientoEstimado;
	}
	
}
