package chasqui.exceptions;

public class ErrorDeParseoDeCoordenadasException extends RuntimeException{
	
	public ErrorDeParseoDeCoordenadasException(Exception e){
		super(e);
	}
	
	public ErrorDeParseoDeCoordenadasException(String msg){
		super(msg);
	}
	
}
