package chasqui.service.rest.request;

import java.io.Serializable;

public class InvitacionRequest implements Serializable{

	
	private static final long serialVersionUID = 8728125085601220464L;
	
	private Integer idGrupo;
	private String emailInvitado;
	
	public InvitacionRequest(){
		
	}
	
	public InvitacionRequest(Integer idGrupo, String emailInvitado){
		
		this.idGrupo = idGrupo;
		this.emailInvitado = emailInvitado;
		
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getEmailInvitado() {
		return emailInvitado;
	}

	public void setEmailInvitado(String emailInvitado) {
		this.emailInvitado = emailInvitado;
	}
	
}
