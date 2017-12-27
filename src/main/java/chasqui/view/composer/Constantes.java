package chasqui.view.composer;

public class Constantes {

	
	public final static String SESSION_USERNAME = "USERNAME";
	//public final static String EMAIL_REGEX_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$;";
	public final static Integer VENTANA_MODO_EDICION = 1;
	public final static Integer VENTANA_MODO_LECTURA = 2;
	public final static String ESTADO_PEDIDO_INEXISTENTE="INEXISTENTE";
	public final static String ESTADO_PEDIDO_ABIERTO="ABIERTO";
	public final static String ESTADO_PEDIDO_VENCIDO="VENCIDO";
	public final static String ESTADO_PEDIDO_CANCELADO="CANCELADO";
	public final static String ESTADO_PEDIDO_CONFIRMADO="CONFIRMADO";
	public final static String ESTADO_PEDIDO_ENTREGADO="ENTREGADO";
	
	public final static String VENTANA_PRODUCTOR = "VENTANA_PRODUCTOR";
	public final static String VENTANA_PRODUCTO = "VENTANA_PRODUCTO";
	
	public final static String ESTADO_NODO_SOLICITADO_SIN_ADMIN = "SOLICITADO_SIN_ADMIN";
	public final static String ESTADO_NODO_SOLICITADO = "SOLICITADO";
	public final static String ESTADO_NODO_APROBADO = "APROBADO";
	public final static String ESTADO_NODO_DESHABILITADO = "DESHABILITADO";
	public final static String NODO_ABIERTO = "NODO_ABIERTO";
	public final static String NODO_CERRADO = "NODO_CERRADO";
	
	public static final String DOMICILIO_NO_ESPECIFICADO = "No especificado";
	
	//USUARIO
	
	public static final String MAIL_CONFIRMADO = "MAIL_CONFIRMADO";
	public static final String MAIL_SIN_CONFIRMAR = "MAIL_SIN_CONFIRMAR";
	
	//NOTIFICACIONES
	public final static String ESTADO_NOTIFICACION_NO_LEIDA = "NOTIFICACION_NO_LEIDA";
//	public final static String ESTADO_NOTIFICACION_LEIDA = "Leído";
	public final static String ESTADO_NOTIFICACION_LEIDA_ACEPTADA = "NOTIFICACION_ACEPTADA";
	public final static String ESTADO_NOTIFICACION_LEIDA_RECHAZADA = "NOTIFICACION_RECHAZADA";
	public static final String ZONA_NO_DEFINIDA = "Zona no definida";
	
	// ENVIO MAILS
	public static final String VENCIMIENTO_DE_PEDIDO_SUBJECT = "Vencimiento automatico";
	public static final String NUEVO_PEDIDO_EN_GCC_SUBJECT = "Un miembro de tu grupo de compras ha iniciado su pedido";
	public static final String AGRADECIMIENTO = "Muchas gracias por utilizar el sistema Chasqui";
	public static final String TEMPLATE_ACEPTAR_INVITACION_GCC ="emailInvitacionAGCCAceptada.ftl";
	public static final String TEMPLATE_BIENVENIDA_VENDEDOR = "emailBienvenida.ftl";
	public static final String TEMPLATE_BIENVENIDA_CLIENTE = "emailBienvenidaCliente.ftl";
	public static final String TEMPLATE_NOTIFICACION_VENCIMIENTO_PROXIMO = "emailNotificacionPedido.ftl"; //TODO cambiar template, ahora en desuso
	public static final String TEMPLATE_NOTIFICACION_PEDIDO = "emailNotificacionPedido.ftl";
	public static final String TEMPLATE_NOTIFICACION = "emailNotificacionPedido.ftl";
	public static final String TEMPLATE_INVITAR_GCC_NO_REGISTRADO = "emailInvitadoSinRegistrar.ftl";
	public static final String TEMPLATE_INVITAR_GCC_REGISTRADO = "emailInvitadoRegistrado.ftl";//TODO Definir, porque no existe, AUN.
	public static final String TEMPLATE_INVITACION_CHASQUI = "emailInvitacion.ftl";
	public static final String TEMPLATE_RECUPERO = "emailRecupero.ftl";
	public static final String SUBJECT_BIENVENIDO = "Bienvenido a Chasqui";
	public static final String SUBJECT_ALERT_VENCIMIENTO = "Tu Pedido esta a punto de vencer";
	public static final String SUBJECT_INVITACION_NO_REGISTRADO = "Te han invitado a un grupo de compras colectivas, registrate para seguir!";
	public static final String SUBJECT_INVITACION_REGISTRADO = "Te han invitado a un grupo de compras colectivas";
	public static final String SUBJECT_CONOCES_CHASQUI = "¿Conocés Chasqui?";
	public static final String SUBJECT_INVITACION_GCC_ACEPTADA = "<usuario> acepto tu invitacion";
	public static final String AVISO_DE_RECUPERO_DE_CONTRASEÑA = "Aviso de Recupero de contraseña";
	public static final String CONFIRMACION_COMPRA_TEMPLATE_URL = "emailConfirmacionPedido.ftl";
	public static final String CONFIRMACIÓN_DE_COMPRA_SUBJECT = "Confirmación de Compra";
	public static final int CANT_MAX_IMAGENES_VARIEDAD = 3;
	public static final int MAX_SIZE_DESC_LARGA_PRODUCTOR = 8200;
	public static final String CONFIRMACION_COMPRA_NOTIFICACION ="Tu pedido se ha confirmado con éxito, recibirás un correo con la fecha probable de entrega";
	public static final String CONFIRMACION_COMPRA_NOTIFICACION_OTROMIEMBRO = "El usuario <usuario> ha confirmado su pedido en tu grupo de compras <grupo> ¡Apurate a hacer el tuyo!";
	public static final String NUEVO_PEDIDO_NOTIFICACION_OTROMIEMBRO =  "El usuario <usuario> ha iniciado su pedido en el grupo <grupo> del catálogo de <vendedor> ¡No te pierdas esta compra!";
	public static final String CONFIRMACION_PEDIDO_COLECTIVO = "El administrador del grupo <grupo> en el catálogo del vendedor <vendedor> ha confirmado el pedido colectivo";
	public static final String VENCIMIENTO_PEDIDO_TEMPLATE = "emailVencimientoAutomatico.ftl";

	public static final String TXT_INVITACION_GCC = "El usuario <usuario> te ha invitado al grupo de compras colectivas <alias> para el catálogo de <vendedor>";
	public static final String TXT_INVITACION_GCC_ACEPTADA = "El usuario <usuario> ha aceptado tu invitacion al grupo de compras colectivas <alias> para el catálogo de <vendedor>.";
	public static final String PEDIDO_VENCIDO_NOTIFICACION = "Tu pedido abierto el dia <timestamp> del catálogo del vendedor <vendedor> ha expirado por falta de actividad.";
	
	//Mensajes de error
	public static final String ERROR_USUARIO_MAIL_SIN_CONFIRMAR = "Para acceder debe validar la cuenta via e-mail";
	public static final String ERROR_CREDENCIALES_INVALIDAS = "Usuario o Password incorrectos!";
	public static final String PASSWORD_CORTO = "El password debe tener entre 10 y 26 caracteres";
	
}
