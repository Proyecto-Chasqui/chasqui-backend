package chasqui.exceptions;

public class ErrorZona extends RuntimeException {
	
	public ErrorZona(Exception e) {
		super(e);
	}
	
	public ErrorZona(String msj) {
		super(msj);
	}
}
