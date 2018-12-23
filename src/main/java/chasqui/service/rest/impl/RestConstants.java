package chasqui.service.rest.impl;

import javax.ws.rs.core.Response.Status;

public class RestConstants {
	public final static int CLIENTE_INEXISTENTE =404;
	public final static int CLIENTE_NO_ES_ADMINISTRADOR =505;
	public final static int CLIENTE_NO_ESTA_EN_GRUPO =404;
	public final static int VENDEDOR_INEXISTENTE =404;
	public final static int REQ_INCORRECTO =504;
	public final static int PEDIDO_EXISTENTE = 503;
	public final static int PEDIDO_INEXISTENTE = 502;
	public final static int GRUPOCC_INEXISTENTE = 404;
	public static final int MONTO_INSUFICIENTE = 501;
	public static final int DIRECCION_INEXISTENTE = 500;
	public static final int IO_EXCEPTION = 418;
	public static final int ERROR_INTERNO = 419;

}
