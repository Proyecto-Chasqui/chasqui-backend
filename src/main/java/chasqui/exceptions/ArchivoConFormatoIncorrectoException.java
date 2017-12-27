package chasqui.exceptions;

public class ArchivoConFormatoIncorrectoException extends RuntimeException{
	
	public ArchivoConFormatoIncorrectoException(Exception e){
		super(e);
	}
	
	public ArchivoConFormatoIncorrectoException(String msg){
		super(msg);
	}
	
}
