package chasqui.exceptions;

public class TokenInexistenteException extends Exception{
	
	public TokenInexistenteException(Exception e){
		super(e);
	}
	
	public TokenInexistenteException(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public TokenInexistenteException(String msg){
		super(msg);
	}

	public TokenInexistenteException() {
		// TODO Auto-generated constructor stub
	}
}
