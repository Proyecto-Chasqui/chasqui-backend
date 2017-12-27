package chasqui.exceptions;

/**
 * Esta excepción representa la situación de búsqueda de un nodo por id o alias
 * que no se existe
 * 
 * @author huenu
 *
 */
public class NodoInexistenteException extends RuntimeException {

	public NodoInexistenteException(Exception e) {
		super(e);
	}

	public NodoInexistenteException(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public NodoInexistenteException(String alias) {
		super("No existe el nodo con alias: "+alias);
	}
	public NodoInexistenteException(int id) {
		super("No existe el nodo con id: "+ String.valueOf(id));
	}

	public NodoInexistenteException() {
		// TODO Auto-generated constructor stub
	}

}
