package chasqui.exceptions;

public class PuntoDeRetiroInexistenteException extends Exception{
	
	public PuntoDeRetiroInexistenteException(Exception e){
		super(e);
	}
	
	public PuntoDeRetiroInexistenteException(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public PuntoDeRetiroInexistenteException(String msg){
		super(msg);
	}

	public PuntoDeRetiroInexistenteException() {
		// TODO Auto-generated constructor stub
	}

}
