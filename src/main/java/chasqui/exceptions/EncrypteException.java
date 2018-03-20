package chasqui.exceptions;

public class EncrypteException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncrypteException(Exception e){
		super(e);
	}
	
	public EncrypteException(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public EncrypteException(String msg){
		super(msg);
	}

	public EncrypteException() {
		// TODO Auto-generated constructor stub
	}

}
