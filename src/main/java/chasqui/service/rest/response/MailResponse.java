package chasqui.service.rest.response;

public class MailResponse {
	private String mail;

	public MailResponse(String mail){
		this.setMail(mail);
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}