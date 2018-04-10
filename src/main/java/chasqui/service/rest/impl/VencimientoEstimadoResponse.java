package chasqui.service.rest.impl;

import org.joda.time.DateTime;

import chasqui.model.Pedido;

public class VencimientoEstimadoResponse {

	String vencimientoEstimado;
	
	public VencimientoEstimadoResponse(Pedido pedido){
	
		if(pedido.getPerteneceAPedidoGrupal()){
			Integer max = Integer.MAX_VALUE;
			vencimientoEstimado = max.toString();
		}else{
			Long vencimientoEnMillis = pedido.getFechaDeVencimiento().getMillis();
			Long modificacionEnMillis = DateTime.now().getMillis();
			//Long modificacionEnMillis = pedido.getFechaModificacion().getMillis();	
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
