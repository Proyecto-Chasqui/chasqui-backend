package chasqui.exceptions;

public class ClienteNoPerteneceAGCCException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3163407439925521624L;

	public ClienteNoPerteneceAGCCException(String  mail) {
		super(mail); //TODO mejorar mensaje
	}
}
