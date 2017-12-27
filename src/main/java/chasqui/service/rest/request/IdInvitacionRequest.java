package chasqui.service.rest.request;

import java.io.Serializable;

public class IdInvitacionRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 633570686066621281L;
	private String idInvitacion;
	
	public String getIdInvitacion() {
		return idInvitacion;
	}
	public void setIdInvitacion(String idInvitacion) {
		this.idInvitacion = idInvitacion;
	}

}
