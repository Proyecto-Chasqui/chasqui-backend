package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.Cliente;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Notificacion;

@Deprecated
public interface InvitacionService {

//	@Transactional
//	public void notificarClienteRegistrado(Cliente cliente, String userDestino, String urlVendedor, String msj, String idDispositivo, Integer idGrupo,String nombreVendedor) throws IOException, MessagingException, TemplateException;

//	@Transactional
//	public void notificarClienteNoRegistrado(Cliente cliente, String userDestino, String urlVendedor, String msj, String idDispositivo, Integer idGrupo,String nombreVendedor) throws IOException, MessagingException, TemplateException;

	@Transactional
	public void emailInvitacionAChasqui(Cliente administrador, String emailInvitado) throws Exception;

	@Transactional
	void aceptarInvitacionAGCC(InvitacionAGCC invitacion);
	
	@Transactional
	List<Notificacion> obtenerNotificacionesGCCPara(String emailCliente);
	
	@Transactional
	InvitacionAGCC obtenerInvitacionAGCCporIDGrupo(String emailCliente, Integer idGrupo);

	@Transactional
	InvitacionAGCC obtenerInvitacionAGCCporID(Integer idInvitacion);

	@Transactional
	public void eliminarInvitacion(Integer idGrupo, String emailCliente);
	@Transactional
	public void rechazarInvitacionAGCC(InvitacionAGCC invitacion);
	
}
