package chasqui.services.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import chasqui.aspect.Auditada;
import chasqui.dao.NotificacionDAO;
import chasqui.model.Cliente;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.services.interfaces.NotificacionService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;


@Auditada
public class NotificacionServiceImpl implements NotificacionService{

	@Autowired
	NotificacionDAO notificacionDAO;
	@Autowired
	String GCM_API_KEY;
	@Autowired
	private MailService mailService;
	@Autowired
	Integer cantidadDeMinutosParaExpiracion;
	
	@Override
	public void guardar(Notificacion notificacion,String idDispositivo) {
		if(idDispositivo != null){
			enviarNotificacion(notificacion,idDispositivo);
		}
		notificacionDAO.guardar(notificacion);
	}
	
	private void enviarNotificacion(Notificacion n,String idDispositivo){
		Sender sender = new Sender(GCM_API_KEY);
		Message m = new Message.Builder().addData("message",n.getMensaje()).build();
		try{
			Result r = sender.send(m, idDispositivo, 2);
		}catch(Exception e){
			   
		}
	}
	
	/**
	 * Permite recuperar las notificaciones pendientes  de determinado cliente destinatario 
	 * @param emailCliente
	 * @return 
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesPendientesPara(String emailCliente){
		return notificacionDAO.obtenerNotificacionesPara(emailCliente, Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
	}

	/**
	 * Permite recuperar las notificaciones ACEPTADAS de determinado cliente destinatario 
	 * @param emailCliente
	 * @return 
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesAceptadasPara(String emailCliente) {
		return notificacionDAO.obtenerNotificacionesPara(emailCliente, Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA);
	}
	/**
	 * Permite recuperar las notificaciones RECHAZADAS de determinado cliente destinatario 
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
	public List<InvitacionAGCC> obtenerInvitacionPendientePorIDdeGrupo(String emailCliente, Integer idGrupo){
		return notificacionDAO.obtenerInvitacionPendientePorIDdeGrupo(emailCliente, idGrupo);
	}

	@Override
	public void notificar(String emailOrigen, String emailDestino, String mensaje, String idDispositivo) {
		
		Notificacion notificacion = new Notificacion(emailOrigen, emailDestino, mensaje,Constantes.ESTADO_NOTIFICACION_NO_LEIDA );
		this.guardar(notificacion, idDispositivo);
		
	}

	@Override
	public void eliminarNotificacion(Notificacion notificacion) {
		notificacionDAO.eliminar(notificacion);
	}
	

	/*
	 * Este método realiza notificaciones:
	 *   - Email al cliente con la descripcion del pedido
	 *   - notificacion interna para registrar la confirmación exitosa
	 *   
	 */
	@Override
	public void enviarAClienteSuPedidoConfirmado(String emailVendedor, String emailCliente,Pedido pedidoConfirmado){
		mailService.enviarEmailConfirmacionPedido(emailVendedor,emailCliente,pedidoConfirmado);

		String mensaje = Constantes.CONFIRMACION_COMPRA_NOTIFICACION;
		this.notificar(emailVendedor, emailCliente, mensaje , null);
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
	public void notificarConfirmacionPedidoColectivo(Integer idGrupo, String emailAdministrador, String alias,
			String emailClienteDestino, String nombreUsuario, String nombreVendedor) {

		String mensaje = Constantes.CONFIRMACION_PEDIDO_COLECTIVO;
		mensaje = mensaje.replaceAll("<grupo>",alias);
		mensaje = mensaje.replaceAll("<vendedor>",nombreVendedor);
		
		//--------------Notificación interna
		this.notificar(emailAdministrador,emailClienteDestino,mensaje, null);
		//--------------Mail de respaldo (Deprecado, queda comentado para en un futuro habilitarlo segun opcion del usuario)
		mailService.enviarEmailNotificacionChasqui(emailAdministrador,nombreUsuario,emailClienteDestino, mensaje, Constantes.CONFIRMACIÓN_DE_COMPRA_SUBJECT);
	}

	/**
	 * Este método notifica a un usuario que otro usuario ha iniciado el pedido colectivo
	 * TODO: No hace nada si el destinatario y el originante son el mismo
	 */
	@Override
	public void notificarNuevoPedidoEnGCC(Integer idGrupo, String alias, String emailOriginante, String emailDestinatario, String nicknameDestinatario,
			String nombreVendedor) {
		String mensaje =Constantes.NUEVO_PEDIDO_NOTIFICACION_OTROMIEMBRO;

		mensaje = mensaje.replaceAll("<grupo>",alias);
		mensaje = mensaje.replaceAll("<usuario>",emailOriginante);
		mensaje = mensaje.replaceAll("<vendedor>",nombreVendedor);
				
		//--------------Notificación interna
		this.notificar(emailOriginante,emailDestinatario,mensaje, null);
		//--------------Mail de respaldo (Desestimado, queda comentado para futura opcion de mandar mail si lo desea el usuario)
		//mailService.enviarEmailNotificacionChasqui(emailOriginante,nicknameDestinatario,emailDestinatario, mensaje , Constantes.NUEVO_PEDIDO_EN_GCC_SUBJECT);
	}

	@Override
	public void notificarInvitacionAGCCClienteRegistrado(Cliente adminGCC, String emailInvitado, GrupoCC grupo, String idDispositivo) throws IOException, MessagingException, TemplateException {
		String mensaje = Constantes.TXT_INVITACION_GCC;
		mensaje = mensaje.replaceAll("<usuario>", adminGCC.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		
		this.invitar(adminGCC.getEmail(), emailInvitado, mensaje, idDispositivo,grupo.getId());
		mailService.enviarEmailInvitadoRegistrado(adminGCC, emailInvitado, grupo.getVendedor().getUrl(),grupo.getVendedor().getNombre());
	}


	/**
	 * Crea un objeto InvitacionAGCC con estado NO_LEIDO para persistirlo. 
	 * Envía un mail de invitación a GCC
	 */
	private void invitar(String emailOrigen, String emailDestino, String msg, String idDispositivo, Integer idGrupo){
		InvitacionAGCC invitacion = new InvitacionAGCC(emailOrigen, emailDestino, msg, idGrupo);
		this.guardar(invitacion, idDispositivo);
	}

	@Override
	public void notificarInvitacionAGCCClienteNoRegistrado(Cliente adminGCC, String emailInvitado, GrupoCC grupo,
			String iddisp) throws Exception {
		String mensaje = Constantes.TXT_INVITACION_GCC;
		mensaje = mensaje.replaceAll("<usuario>", adminGCC.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		
		this.invitar(adminGCC.getEmail(), emailInvitado, mensaje, iddisp,grupo.getId());
		mailService.enviarmailInvitadoSinRegistrar(adminGCC, emailInvitado, grupo.getVendedor().getUrl(), grupo.getVendedor().getNombreCorto(), grupo.getVendedor().getNombre(), grupo.getId());	
	}

	@Override
	public void notificarInvitacionAGCCAceptada(GrupoCC grupo, Cliente invitado) {
		String mensaje = Constantes.TXT_INVITACION_GCC_ACEPTADA;
		mensaje = mensaje.replaceAll("<usuario>", invitado.getUsername());
		mensaje = mensaje.replaceAll("<alias>", grupo.getAlias());
		mensaje = mensaje.replaceAll("<vendedor>", grupo.getVendedor().getNombre());
		
		this.notificar(invitado.getEmail(), grupo.getAdministrador().getEmail(), mensaje, null);
		mailService.enviarEmailDeInvitacionAGCCAceptada(grupo, invitado);
	}
	
	@Override
	public void notificarNuevoAdministrador(Cliente administradorAnterior, Cliente nuevoAdministrador, GrupoCC grupo) {
		
	//TODO:
	/**
	 * Los notificar de este metodo no envian notificaciones.
	 * Por eso se decidion enviar un email.
	 * Si es posible arreglarlos.
	 */
	/**
		String mensajeNuevoAdministrador = Constantes.TXT_NUEVO_ADMINISTRADOR;
		mensajeNuevoAdministrador = mensajeNuevoAdministrador.replaceAll("<administradorAnterior>", administradorAnterior.getUsername());
		mensajeNuevoAdministrador = mensajeNuevoAdministrador.replaceAll("<alias>", grupo.getAlias());
		 
		this.notificar(administradorAnterior.getEmail(), nuevoAdministrador.getEmail(), mensajeNuevoAdministrador, null);

		String mensajeAnteriorAdministrador = Constantes.TXT_ANTERIOR_ADMINISTRADOR;
		mensajeAnteriorAdministrador = mensajeAnteriorAdministrador.replaceAll("<alias>", grupo.getAlias());
		mensajeAnteriorAdministrador = mensajeAnteriorAdministrador.replaceAll("<nuevoAdministrador>", nuevoAdministrador.getUsername());
		
		this.notificar(nuevoAdministrador.getEmail(), administradorAnterior.getEmail(), mensajeAnteriorAdministrador, null);
	**/
		mailService.enviarEmailNuevoAdministrador(administradorAnterior, nuevoAdministrador, grupo);
	}

	@Override
	public void notificarPedidoVencido(Cliente cliente, DateTime fechaCreacion, String emailVendedor, String nombreVendedor) {
		String mensaje = Constantes.PEDIDO_VENCIDO_NOTIFICACION;
		
		mensaje = mensaje.replaceAll("<timestamp>", this.dateTimeToString(fechaCreacion));
		mensaje = mensaje.replaceAll("<vendedor>", nombreVendedor);
		
		this.notificar(emailVendedor, cliente.getEmail(), mensaje, null);
		//TODO hace falta enviar mail de respaldo? mailService.env(grupo, invitado);
		mailService.enviarEmailVencimientoPedido(nombreVendedor, cliente, this.dateTimeToString(fechaCreacion), cantidadDeMinutosParaExpiracion.toString());
	}

	private String dateTimeToString(DateTime fecha){
		//"29/10/2012 a las 14:44 Hs "
		Integer horas = fecha.getHourOfDay();
		String horasResultantes;
		Integer minutos = fecha.getMinuteOfHour();
		String minutosResultantes;
		
		if(horas < 10){
			horasResultantes = "0" + horas.toString() + ":";
		}else{
			horasResultantes = horas.toString() + ":";
		}
		if(minutos < 10){
			minutosResultantes = "0" + minutos.toString() + " Hs ";
		}else{
			minutosResultantes = minutos.toString() + " Hs ";
		}
		
		
		String fechaResultante =				
		Integer.toString(fecha.getDayOfMonth())   + "/"       +
		Integer.toString(fecha.getMonthOfYear())  + "/"       +
		Integer.toString(fecha.getYear())         + " a las " +
		horasResultantes +
		minutosResultantes;
		
		return fechaResultante;
	}

}
