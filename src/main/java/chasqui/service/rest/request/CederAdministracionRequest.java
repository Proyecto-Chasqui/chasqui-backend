package chasqui.service.rest.request;

public class CederAdministracionRequest {
	private Integer idGrupo;
	private String emailCliente;
	
	public CederAdministracionRequest(){
		
	}
	
	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getEmailCliente() {
		return emailCliente;
	}
	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}
}
