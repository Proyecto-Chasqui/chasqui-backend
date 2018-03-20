package chasqui.exceptions;

public class PasswordIncorrectoException extends Exception {

	private static final long serialVersionUID = 1L;

	public PasswordIncorrectoException(Exception e){
		super(e);
	}
	
	public PasswordIncorrectoException(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public PasswordIncorrectoException(String msg){
		super(msg);
	}

	public PasswordIncorrectoException() {
		// TODO Auto-generated constructor stub
	}
}
