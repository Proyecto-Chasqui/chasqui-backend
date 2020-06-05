package chasqui.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.mail.MessagingException;
import javax.management.Notification;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import chasqui.aspect.Auditada;
import chasqui.dao.GrupoDAO;
import chasqui.dao.NotificacionDAO;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.exceptions.EncrypterException;
import chasqui.model.Cliente;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Nodo;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.model.SolicitudPertenenciaNodo;
import chasqui.model.Usuario;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;
import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;

@Auditada
public class NotificacionServiceImpl implements NotificacionService {

	@Autowired
	NotificacionDAO notificacionDAO;
	@Autowired
	String GCM_API_KEY;
	@Autowired
	private MailService mailService;
	@Autowired
	Integer cantidadDeMinutosParaExpiracion;
	@Autowired
	GrupoDAO grupodao;
	@Autowired
	UsuarioService usuarioService;

	@Override
	public void guardar(Notificacion notificacion, String idDispositivo) {
		notificacionDAO.guardar(notificacion);
		if (idDispositivo != null) {
			try {
				enviarNotificacion(notificacion, idDispositivo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String defineType(Notificacion n) {
		if (n.getMensaje().contains("invitado al")) {
			return "Invitación";
		} else {
			return "Notificación";
		}
	}

	private String defineAction(Notificacion n) {
		if (n.getMensaje().contains("ha expirado por falta de actividad")) {
			return "Vencimiento";
		} else {
			return "Información";
		}
	}

	private void enviarNotificacion(Notificacion n, String idDispositivo) {
		String recipient = idDispositivo;

		if (PushClient.isExponentPushToken(recipient)) {

			HashMap<String, String> data = new HashMap<String, String>();
			data.put("Type", this.defineType(n));
			data.put("Action", this.defineAction(n));
			data.put("id", n.getId().toString());
			PushClient client = new PushClient();
			List<ExpoPushMessage> messages = new ArrayList<>();
			ExpoPushMessage epm = new ExpoPushMessage(recipient);
			epm.title = this.defineType(n);
			if (n.getMensaje() != null)
				epm.body = n.getMensaje();
			epm.data = data;
			messages.add(epm);

			List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(messages);

			List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();
			for (List<ExpoPushMessage> chunk : chunks) {
				messageRepliesFutures.add(client.sendPushNotificationsAsync(chunk));
			}

			// Wait for each completable future to finish
			List<ExpoPushTicket> allTickets = new ArrayList<>();
			for (CompletableFuture<List<ExpoPushTicket>> messageReplyFuture : messageRepliesFutures) {
				try {
					for (ExpoPushTicket ticket : messageReplyFuture.get()) {
						allTickets.add(ticket);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Permite recuperar las notificaciones pendientes de determinado cliente
	 * destinatario
	 * 
	 * @param emailCliente
	 * @return
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesPendientesPara(String emailCliente) {
		return notificacionDAO.obtenerNotificacionesPara(emailCliente, Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
	}

	/**
	 * Permite recuperar las notificaciones ACEPTADAS de determinado cliente
	 * destinatario
	 * 
	 * @param emailCliente
	 * @return
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesAceptadasPara(String emailCliente) {
		return notificacionDAO.obtenerNotificacionesPara(emailCliente, Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA);
	}

	/**
	 * Permite recuperar las notificaciones RECHAZADAS de determinado cliente
	 * destinatario
	 * 
	 * @param emailCliente
	 * @return
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesRechazadasPara(String emailCliente) {
		return notificacionDAO.obtenerNotificacionesPara(emailCliente, Constantes.ESTADO_NOTIFICACION_LEIDA_RECHAZADA);
	}

	@Override
	public InvitacionAGCC obtenerNotificacionPorID(Integer idInvitacion) {
		return notificacionDAO.obtenerNotificacionPorID(idInvitacion);
	}

	@Override
	public List<InvitacionAGCC> obtenerInvitacionPendientePorIDdeGrupo(String emailCliente, Integer idGrupo) {
		return notificacionDAO.obtenerInvitacionPendientePorIDdeGrupo(emailCliente, idGrupo);
	}

	@Override
	public void notificar(String emailOrigen, String emailDestino, String mensaje, String idDispositivo) {
		try {
			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailDestino);
			Notificacion notificacion = new Notificacion(emailOrigen, emailDestino, mensaje,
					Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
			this.guardar(notificacion, c.getIdDispositivo());
		} catch (UsuarioInexistenteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void eliminarNotificacion(Notificacion notificacion) {
		notificacionDAO.eliminar(notificacion);
	}

	@Override
	public void notificarSolicitudDePertenenciaANodo(String emailCliente, String emailAdministrador,
			String nombreCliente, String aliasNodo) {
		String mensaje = Constantes.SOLICITUD_INGRESO_NODO;
		mensaje = mensaje.replaceAll("<usuario>", nombreCliente);
		mensaje = mensaje.replaceAll("<nodo>", aliasNodo);
		this.notificar(emailCliente, emailAdministrador, mensaje, null);
	}

	@Override
	public void notificarGestionDeSolicitudDePertenenciaANodo(String accion, String emailCliente,
			String emailAdministrador, String aliasNodo) {
		String mensaje = Constantes.ACCION_GESTION_SOLICITUD_INGRESO_NODO;
		mensaje = mensaje.replaceAll("<nodo>", aliasNodo);
		mensaje = mensaje.replaceAll("<accion>", accion);
		this.notificar(emailAdministrador, emailCliente, mensaje, null);
	}

	/*
	 * Este método realiza notificaciones: - Email al cliente con la descripcion del
	 * pedido - notificacion interna para registrar la confirmación exitosa
	 * 
	 */
	@Override
	public void enviarAClienteSuPedidoConfirmado(String emailVendedor, String emailCliente, Pedido pedidoConfirmado)
			throws UsuarioInexistenteException {
		mailService.enviarEmailConfirmacionPedido(emailVendedor, emailCliente, pedidoConfirmado);

		String mensaje = Constantes.CONFIRMACION_COMPRA_NOTIFICACION;
		this.notificar(emailVendedor, emailCliente, mensaje, null);
	}

	@Override
	public void notificarConfirmacionCompraOtroMiembro(String emailVendedor, String emailCliente, String nombreCliente,
			String alias) {
		String mensaje = Constantes.CONFIRMACION_COMPRA_NOTIFICACION_OTROMIEMBRO;
		mensaje = mensaje.replaceAll("<usuario>", nombreCliente);
		mensaje = mensaje.replaceAll("<grupo>", alias);
		this.notificar(emailVendedor, emailCliente, mensaje, null);
	}

	@Override
	public void notificarConfirmacionNodoCompraOtroMiembro(String emailVendedor, String emailCliente,
			String nombreCliente, String alias) {
		String mensaje = Constantes.CONFIRMACION_COMPRA_NOTIFICACION_OTROMIEMBRO_NODO;
		mensaje = mensaje.replaceAll("<usuario>", nombreCliente);
		mensaje = mensaje.replaceAll("<alias>", alias);
		this.notificar(emailVendedor, emailCliente, mensaje, null);
	}

	@Override
	public void notificarConfirmacionPedidoColectivo(Integer idGrupo, String emailAdministrador, String alias,
			String emailClienteDestino, String nombreUsuario, String nombreVendedor) {
		GrupoCC grupo = grupodao.obtenerGrupoAbsolutoPorId(idGrupo);
		String mensaje = Constantes.CONFIRMACION_PEDIDO_COLECTIVO;
		mensaje = mensaje.replaceAll("<grupo>", alias);
		mensaje = mensaje.replaceAll("<vendedor>", nombreVendedor);
		mensaje = mensaje.replaceAll("<colectivo>", (grupo.isEsNodo()) ? "nodo" : "grupo");

		// --------------Notificación interna
		this.notificar(emailAdministrador, emailClienteDestino, mensaje, null);
		// --------------Mail de respaldo (Deprecado, queda comentado para en un futuro
		// habilitarlo segun opcion del usuario)

	}

	/**
	 * Este método notifica a un usuario que otro usuario ha iniciado el pedido
	 * colectivo TODO: No hace nada si el destinatario y el originante son el mismo
	 */
	@Override
	public void notificarNuevoPedidoEnGCC(Integer idGrupo, String alias, String emailOriginante,
			String emailDestinatario, String nicknameDestinatario, String nombreVendedor) {
		String mensaje = Constantes.NUEVO_PEDIDO_NOTIFICACION_OTROMIEMBRO;
		GrupoCC grupo = grupodao.obtenerGrupoAbsolutoPorId(idGrupo);
		mensaje = mensaje.replaceAll("<grupo>", alias);
		mensaje = mensaje.replaceAll("<usuario>", emailOriginante);
		mensaje = mensaje.replaceAll("<vendedor>", nombreVendedor);
		mensaje = mensaje.replaceAll("<colectivo>", (grupo.isEsNodo()) ? "nodo" : "grupo");
		// --------------Notificación interna
		this.notificar(emailOriginante, emailDestinatario, mensaje, null);
		// --------------Mail de respaldo (Desestimado, queda comentado para futura
		// opcion de mandar mail si lo desea el usuario)
		// mailService.enviarEmailNotificacionChasqui(emailOriginante,nicknameDestinatario,emailDestinatario,
		// mensaje , Constantes.NUEVO_PEDIDO_EN_GCC_SUBJECT);
	}

	@Override
	public void notificarInvitacionAGCCClienteRegistrado(Cliente adminGCC, String emailInvitado, GrupoCC grupo,
			String idDispositivo)
			throws IOException, MessagingException, TemplateException, UsuarioInexistenteException {
		String mensaje = Constantes.TXT_INVITACION_GCC;
		mensaje = mensaje.replaceAll("<usuario>", adminGCC.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		mensaje = mensaje.replaceAll("<colectivo>", (grupo.isEsNodo()) ? "nodo" : "grupo");

		this.invitar(adminGCC.getEmail(), emailInvitado, mensaje, idDispositivo, grupo.getId());
		mailService.enviarEmailInvitadoRegistrado(adminGCC, emailInvitado, grupo.getAlias(),
				grupo.getVendedor().getUrl(), grupo.getVendedor().getNombreCorto(), grupo.getVendedor().getNombre(),
				grupo.isEsNodo());
	}

	/**
	 * Crea un objeto InvitacionAGCC con estado NO_LEIDO para persistirlo. Envía un
	 * mail de invitación a GCC
	 */
	private void invitar(String emailOrigen, String emailDestino, String msg, String idDispositivo, Integer idGrupo) {
		InvitacionAGCC invitacion = new InvitacionAGCC(emailOrigen, emailDestino, msg, idGrupo);
		this.guardar(invitacion, idDispositivo);
	}

	@Override
	public void notificarInvitacionAGCCClienteNoRegistrado(Cliente adminGCC, String emailInvitado, GrupoCC grupo,
			String iddisp) throws EncrypterException {
		String mensaje = Constantes.TXT_INVITACION_GCC;
		mensaje = mensaje.replaceAll("<usuario>", adminGCC.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		mensaje = mensaje.replaceAll("<colectivo>", (grupo.isEsNodo()) ? "nodo" : "grupo");

		this.invitar(adminGCC.getEmail(), emailInvitado, mensaje, iddisp, grupo.getId());
		try {
			mailService.enviarmailInvitadoSinRegistrar(adminGCC, emailInvitado, grupo.getVendedor().getUrl(),
					grupo.getVendedor().getNombreCorto(), grupo.getVendedor().getNombre(), grupo.getId(),
					grupo.isEsNodo());
		} catch (Exception e) {
			throw new EncrypterException(e);
		}
	}

	@Override
	public void notificarInvitacionAGCCAceptada(GrupoCC grupo, Cliente invitado) {
		String mensaje = Constantes.TXT_INVITACION_GCC_ACEPTADA;
		mensaje = mensaje.replaceAll("<usuario>", invitado.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		mensaje = mensaje.replaceAll("<colectivo>", (grupo.isEsNodo()) ? "nodo" : "grupo");

		this.notificar(invitado.getEmail(), grupo.getAdministrador().getEmail(), mensaje, null);
		mailService.enviarEmailDeInvitacionAGCCAceptada(grupo, invitado);
	}

	@Override
	public void notificarNuevoAdministrador(Cliente administradorAnterior, Cliente nuevoAdministrador, GrupoCC grupo) {

		// TODO:
		/**
		 * Los notificar de este metodo no envian notificaciones. Por eso se decidion
		 * enviar un email. Si es posible arreglarlos.
		 */
		String mensajeNuevoAdministrador = Constantes.TXT_NUEVO_ADMINISTRADOR;
		mensajeNuevoAdministrador = mensajeNuevoAdministrador.replaceAll("<administradorAnterior>",
				administradorAnterior.getUsername());
		mensajeNuevoAdministrador = mensajeNuevoAdministrador.replaceAll("<alias>", grupo.getAlias());

		this.notificar(administradorAnterior.getEmail(), nuevoAdministrador.getEmail(), mensajeNuevoAdministrador,
				null);

		String mensajeAnteriorAdministrador = Constantes.TXT_ANTERIOR_ADMINISTRADOR;
		mensajeAnteriorAdministrador = mensajeAnteriorAdministrador.replaceAll("<alias>", grupo.getAlias());
		mensajeAnteriorAdministrador = mensajeAnteriorAdministrador.replaceAll("<nuevoAdministrador>",
				nuevoAdministrador.getUsername());

		this.notificar(nuevoAdministrador.getEmail(), administradorAnterior.getEmail(), mensajeAnteriorAdministrador,
				null);
		mailService.enviarEmailNuevoAdministrador(administradorAnterior, nuevoAdministrador, grupo);
	}

	@Override
	public void notificarPedidoVencido(Cliente cliente, DateTime fechaCreacion, Pedido pedido, String emailVendedor,
			String nombreVendedor) {
		String mensaje = Constantes.PEDIDO_VENCIDO_NOTIFICACION;

		mensaje = mensaje.replaceAll("<timestamp>", this.dateTimeToString(fechaCreacion));
		mensaje = mensaje.replaceAll("<vendedor>", nombreVendedor);

		this.notificar(emailVendedor, cliente.getEmail(), mensaje, null);
		// TODO desestimado hasta que se defina si es correcto mandarlo, o va a ser
		// parte de una configuracion opcional
		// mailService.enviarEmailVencimientoPedido(nombreVendedor, cliente,
		// this.dateTimeToString(fechaCreacion),
		// cantidadDeMinutosParaExpiracion.toString());
	}

	private String dateTimeToString(DateTime fecha) {
		// "29/10/2012 a las 14:44 Hs "
		Integer horas = fecha.getHourOfDay();
		String horasResultantes;
		Integer minutos = fecha.getMinuteOfHour();
		String minutosResultantes;

		if (horas < 10) {
			horasResultantes = "0" + horas.toString() + ":";
		} else {
			horasResultantes = horas.toString() + ":";
		}
		if (minutos < 10) {
			minutosResultantes = "0" + minutos.toString() + " Hs ";
		} else {
			minutosResultantes = minutos.toString() + " Hs ";
		}

		String fechaResultante = Integer.toString(fecha.getDayOfMonth()) + "/"
				+ Integer.toString(fecha.getMonthOfYear()) + "/" + Integer.toString(fecha.getYear()) + " a las "
				+ horasResultantes + minutosResultantes;

		return fechaResultante;
	}

	@Override
	public void notificarSolicitudCreacionNodo(Nodo nodo, String estadoSolicitudNodo) {
		mailService.enviarEmailDeGestionDeSolicitudCreacionNodoFinalizada(nodo, nodo.getVendedor(),
				nodo.getEmailAdministradorNodo(), estadoSolicitudNodo);
		String mensaje = Constantes.ACCION_GESTION_SOLICITUD_CREACION_NODO;
		String accion = (estadoSolicitudNodo.equals(Constantes.SOLICITUD_NODO_APROBADO)) ? "aprobado" : "rechazado";
		mensaje = mensaje.replaceAll("<accion>", accion);
		mensaje = mensaje.replaceAll("<vendedor>", nodo.getVendedor().getNombre());
		this.notificar(nodo.getVendedor().getEmail(), nodo.getEmailAdministradorNodo(), mensaje, null);
	}

	@Override
	public void notificarCancelacionDeSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia) {
		String mensaje = Constantes.ACCION_CANCELACION_POR_USUARIO_SOLICITUD_INGRESO_NODO;
		mensaje = mensaje.replaceAll("<usuario>", solicitudpertenencia.getUsuarioSolicitante().getUsername());
		mensaje = mensaje.replaceAll("<nodo>", solicitudpertenencia.getNodo().getAlias());
		this.notificar(solicitudpertenencia.getUsuarioSolicitante().getEmail(),
				solicitudpertenencia.getNodo().getAdministrador().getEmail(), mensaje, null);
	}

	@Override
	public void notificarSolicitudCreacionNodoAVendedor(Integer idVendedor, String nombrenodo, Cliente usuario)
			throws VendedorInexistenteException {
		mailService.enviarEmailDeSolicitudCreacionNodoAVendedor(idVendedor, nombrenodo, usuario);

	}

	@Override
	public void notificarCancelacionDeSolicitudCreacionNodoAVendedor(Integer idVendedor, String nombreNodo,
			Usuario usuarioSolicitante) throws VendedorInexistenteException {
		mailService.enviarEmailDeCancelacionDeSolicitudCreacionNodoAVendedor(idVendedor, nombreNodo,
				(Cliente) usuarioSolicitante);

	}

	@Override
	public void enviarEmailDeSolicitudDePertenenciaANodo(Nodo nodo, Cliente usuario) {
		mailService.enviarEmailDeAvisoDeSolicitudDePertenenciaANodo(nodo, usuario);

	}

	@Override
	public void notificarCancelacionDeSolicitudDePertenenciaANodo(SolicitudPertenenciaNodo solicitudpertenencia) {
		mailService.enviarEmailDeAvisoDeCancelacionDePertenenciaANodo(solicitudpertenencia);

	}

	@Override
	public void notificarGestionDeSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia) {
		mailService.enviarEmailDeAvisoDeGestionDePertenenciaANodo(solicitudpertenencia);

	}

	@Override
	public void enviarEmailDeAvisoDeCambioDeTipoDeNodoAVendedor(Nodo nodo) {
		mailService.enviarEmailDeAvisoDeCambioDeTipoDeNodoAVendedor(nodo);

	}

}
