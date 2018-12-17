package chasqui.exceptions;

public class EncrypterException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncrypterException(Exception e){
		super(e);
	}
	
	public EncrypterException(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public EncrypterException(String msg){
		super(msg);
	}

	public EncrypterException() {
		// TODO Auto-generated constructor stub
	}

}
