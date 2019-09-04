package chasqui.service.rest.response;

public class MailResponse {
	private String mail;
	private boolean existeUsuario;


	public MailResponse(String mail, boolean existeUsuario){
		this.setMail(mail);
		this.setExisteUsuario(existeUsuario);
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public boolean isExisteUsuario() {
		return existeUsuario;
	}

	public void setExisteUsuario(boolean existeUsuario) {
		this.existeUsuario = existeUsuario;
	}

}