package chasqui.exceptions;

/**
 * Esta excepción representa la situación de querer hacer acciones que corresponden a un nodo abierto
 * 
 * @author David
 *
 */
public class NodoCerradoException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4119982845337547078L;
	
	public NodoCerradoException() {
		super();
	}
	
	public NodoCerradoException(Exception e) {
		super(e);
	}
	
	public NodoCerradoException(String error) {
		super(error);
	}

}
