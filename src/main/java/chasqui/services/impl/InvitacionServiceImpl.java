package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.model.Cliente;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Notificacion;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.view.composer.Constantes;

@Deprecated
@Auditada
public class InvitacionServiceImpl implements InvitacionService{

	@Autowired
	MailService mailService;
	@Autowired
	NotificacionService notificacionService;
	
//	@Override
//	public void notificarClienteRegistrado(Cliente cliente, String emailInvitado, String urlVendedor, String msg, String idDispositivo, Integer idGrupo, String nombreVendedor) throws IOException, MessagingException, TemplateException {
//		this.generarNotificacion(cliente.getEmail(), emailInvitado, msg, idDispositivo,idGrupo);
//		mailService.enviarEmailInvitadoRegistrado(cliente, emailInvitado, urlVendedor, nombreVendedor);		
//	}

//	@Override
//	public void notificarClienteNoRegistrado(Cliente cliente, String emailInvitado, String urlVendedor, String msg, String idDispositivo, Integer idGrupo,String nombreVendedor) throws IOException, MessagingException, TemplateException {
//		this.generarNotificacion(cliente.getEmail(), emailInvitado, msg, idDispositivo, idGrupo);
//		mailService.enviarmailInvitadoSinRegistrar(cliente, emailInvitado, urlVendedor, nombreVendedor);
//	}


	/**
	 * Obtiene todas las notificaciones de un determinado Cliente destinatario
	 */
	@Override
	public List<Notificacion> obtenerNotificacionesGCCPara(String emailCliente) {
		List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesPendientesPara(emailCliente);
		notificaciones.addAll(notificacionService.obtenerNotificacionesAceptadasPara(emailCliente));
		notificaciones.addAll(notificacionService.obtenerNotificacionesRechazadasPara(emailCliente));
		return notificaciones;
	}

	@Override
	public void aceptarInvitacionAGCC(InvitacionAGCC invitacion) {
		invitacion.setEstado(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA);
		notificacionService.guardar(invitacion, null); //TODO ver como manejar el iddispositivo en android
	}
	
	@Override
	public void rechazarInvitacionAGCC(InvitacionAGCC invitacion){
		invitacion.setEstado(Constantes.ESTADO_NOTIFICACION_LEIDA_RECHAZADA);
		notificacionService.guardar(invitacion, null);
	}
	
	
	@Override
	public void emailInvitacionAChasqui(Cliente administrador, String emailInvitado) throws Exception {
		mailService.enviarEmailDeInvitacionChasqui(administrador, emailInvitado);
	}

	@Override
	public InvitacionAGCC obtenerInvitacionAGCCporID(Integer idInvitacion) {
		InvitacionAGCC notif = notificacionService.obtenerNotificacionPorID(idInvitacion);
		return  notif;
	}

	@Override
	public InvitacionAGCC obtenerInvitacionAGCCporIDGrupo(String emailCliente, Integer idGrupo) {
		List<InvitacionAGCC> notificaciones = notificacionService.obtenerInvitacionPendientePorIDdeGrupo(emailCliente, idGrupo);
		for (InvitacionAGCC notificacion : notificaciones) {
			if (notificacion.getIdGrupo().equals(idGrupo)) {
				return notificacion;
			}
		}
		return null;
	}

	@Override
	public void eliminarInvitacion(Integer idGrupo, String emailCliente) {
		InvitacionAGCC invitacion = this.obtenerInvitacionAGCCporIDGrupo(emailCliente, idGrupo);
		notificacionService.eliminarNotificacion(invitacion);
		
	}


}
