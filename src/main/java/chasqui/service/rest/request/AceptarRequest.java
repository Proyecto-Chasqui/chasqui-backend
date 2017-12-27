package chasqui.service.rest.request;

import java.io.Serializable;

public class AceptarRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7905522839168390161L;
	
	private Integer idInvitacion;

	public AceptarRequest() {
	}
	

	public AceptarRequest(Integer idInvitacion){
		this.idInvitacion = idInvitacion;
	}

	public Integer getIdInvitacion() {
		return idInvitacion;
	}

	public void setIdInvitacion(Integer idInvitacion) {
		this.idInvitacion = idInvitacion;
	}

}
