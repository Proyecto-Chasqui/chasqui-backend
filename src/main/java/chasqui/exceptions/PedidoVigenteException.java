package chasqui.exceptions;

public class PedidoVigenteException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6493554461558138136L;
	private static String mensaje = "Error en el pedido: ";

	public PedidoVigenteException() {
		super(mensaje);
	}

	public PedidoVigenteException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(mensaje + message, cause, enableSuppression, writableStackTrace);
	}

	public PedidoVigenteException(String message, Throwable cause) {
		super(mensaje +message, cause);
	}

	public PedidoVigenteException(String email) {
		super(mensaje +email);
	}

	public PedidoVigenteException(Throwable cause) {
		super(mensaje +cause);
	}

	
	
	
	
}
