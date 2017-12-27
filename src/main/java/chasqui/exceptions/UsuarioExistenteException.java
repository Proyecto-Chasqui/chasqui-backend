package chasqui.exceptions;

/**
 * Esta excepcion representa la situación donde se quiere dar de alta un Usuario
 * (Cliente o Vendedor) cuyo correo electrónico ya estaba registrado en el
 * sistema
 * 
 * @author huenu
 *
 */
public class UsuarioExistenteException extends Exception {

	public UsuarioExistenteException(Exception e) {
		super(e);
	}

	public UsuarioExistenteException(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public UsuarioExistenteException(String msg) {
		super(msg);
	}

	public UsuarioExistenteException() {
		// TODO Auto-generated constructor stub
	}

}
