package chasqui.exceptions;

/**
 * Esta excepcion representa la situación donde la búsqueda de un objeto a
 * partir del correo electronico o ID de un Usuario (Cliente o Vendedor) arroja
 * un resultado vacío
 * 
 * @author huenu
 *
 */
public class UsuarioInexistenteException extends Exception {

	public UsuarioInexistenteException(Exception e) {
		super(e);
	}

	public UsuarioInexistenteException(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public UsuarioInexistenteException(String msg) {
		super(msg);
	}

	public UsuarioInexistenteException() {
		// TODO Auto-generated constructor stub
	}

}
