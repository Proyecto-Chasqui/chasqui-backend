package chasqui.exceptions;

/**
 * Esta excepción representa la situación de alta de un nodo por cuyo alias ya
 * está registrado
 * 
 * @author huenu
 *
 */
public class NodoYaExistenteException extends Exception {

	public NodoYaExistenteException(Exception e) {
		super(e);
	}

	public NodoYaExistenteException(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public NodoYaExistenteException(String alias) {
		super("Ya existe el nodo con alias: " + alias);
	}

	public NodoYaExistenteException() {
		// TODO Auto-generated constructor stub
	}

}
