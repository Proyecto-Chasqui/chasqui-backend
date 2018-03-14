package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.dtos.PuntoDeRetiroDTO;
import chasqui.model.PuntoDeRetiro;

public class PuntosDeRetiroResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9076738316761311132L;
	
	List<PuntoDeRetiroDTO> puntosDeRetiro;
	
	public PuntosDeRetiroResponse(List<PuntoDeRetiro>prs){
		puntosDeRetiro = new ArrayList<PuntoDeRetiroDTO>();
		for(PuntoDeRetiro pr : prs){
			puntosDeRetiro.add(new PuntoDeRetiroDTO(pr));
		}
	}

	public List<PuntoDeRetiroDTO> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setPuntosDeRetiro(List<PuntoDeRetiroDTO> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
