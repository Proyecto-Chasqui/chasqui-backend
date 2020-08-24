package chasqui.services.interfaces;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

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
import freemarker.template.TemplateException;

public interface NotificacionService {

	@Transactional
	public void guardar(Notificacion n, String idDispositivo);

	@Transactional
	List<Notificacion> obtenerNotificacionesPendientesPara(String emailCliente);

	@Transactional
	List<Notificacion> obtenerNotificacionesAceptadasPara(String emailCliente);

	@Transactional
	List<Notificacion> obtenerNotificacionesRechazadasPara(String emailCliente);

	@Transactional
	public InvitacionAGCC obtenerNotificacionPorID(Integer idInvitacion);

	@Transactional
	public void notificar(String emailAdministrador, String emailClienteDestino, String mensaje, String idDispositivo);

	@Transactional
	public void eliminarNotificacion(Notificacion notificacion);

	@Transactional
	void enviarAClienteSuPedidoConfirmado(String emailVendedor, String emailCliente, Pedido pedidoConfirmado) throws UsuarioInexistenteException;

	@Transactional
	public void notificarConfirmacionCompraOtroMiembro(String emailVendedor, String email, String nombreCliente,
			String alias);

	/**
	 * Este método notifica a un usuario que otro usuario ha iniciado el pedido
	 * colectivo TODO: No hace nada si el destinatario y el originante son el
	 * mismo Este método debe generar una notificación interna y enviar un mail
	 * de respaldo
	 * 
	 * @param idGrupo
	 * @param alias
	 * @param emailOriginante
	 * @param emailDEstinatario
	 * @param nicknameUsuarioDestino
	 * @param nombreVendedor
	 */
	public void notificarNuevoPedidoEnGCC(Integer idGrupo, String alias, String emailOriginante,
			String emailDestinatario, String nicknameUsuarioDestino, String nombreVendedor);

	/**
	 * Notifica a los miembros del grupo que el administrador ha confirmado el
	 * pedido colectivo Este método debe generar una notificación interna y
	 * enviar un mail de respaldo
	 * 
	 * @param idGrupo
	 * @param emailAdministrador
	 * @param alias
	 * @param emailClienteDestino
	 * @param nombreUsuario
	 * @param nombreVendedor
	 */
	@Transactional
	void notificarConfirmacionPedidoColectivo(Integer idGrupo, String emailAdministrador, String alias,
			String emailClienteDestino, String nombreUsuario, String nombreVendedor);

	/**
	 * Notifica a un usuario de Chasqui que ha sido invitado a un grupo de
	 * compras colectivas Este método debe generar una notificación interna y
	 * enviar un mail de respaldo
	 * 
	 * @param administrador
	 * @param emailInvitado
	 * @param grupo
	 * @throws TemplateException
	 * @throws MessagingException
	 * @throws IOException
	 * @throws UsuarioInexistenteException 
	 */
	@Transactional
	public void notificarInvitacionAGCCClienteRegistrado(Cliente administrador, String emailInvitado, GrupoCC grupo,
			String idDispositivo) throws IOException, MessagingException, TemplateException, UsuarioInexistenteException;

	/**
	 * Notifica a un POTENCIAL usuario de Chasqui que ha sido invitado a un
	 * grupo de compras colectivas Este método debe generar una notificación
	 * interna y enviar un mail de respaldo
	 * 
	 * @param administrador
	 * @param emailInvitado
	 * @param grupo
	 * @throws EncrypterException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @throws IOException
	 */
	@Transactional
	public void notificarInvitacionAGCCClienteNoRegistrado(Cliente administrador, String emailInvitado, GrupoCC grupo,
			String iddisp) throws IOException, MessagingException, TemplateException, EncrypterException;

	/**
	 * 
	 * @param grupo
	 * @param cliente2
	 */
	@Transactional
	public void notificarInvitacionAGCCAceptada(GrupoCC grupo, Cliente cliente2);

	/**
	 * Notificar al Cliente de que su pedido ha expirado y por eso se liberó la
	 * reserva de productos
	 * 
	 * @param cliente
	 * @param fechaCreacion
	 * @param pedido
	 * @param emailVendedor
	 * @param nombreVendedor
	 */
	void notificarPedidoVencido(Cliente cliente, DateTime fechaCreacion, Pedido pedido, String emailVendedor, String nombreVendedor);

	public List<InvitacionAGCC> obtenerInvitacionPendientePorIDdeGrupo(String emailCliente,	Integer idGrupo);

	public void notificarNuevoAdministrador(Cliente administradorAnterior, Cliente nuevoAdministrador, GrupoCC grupo, String colectivo);

	public void notificarSolicitudCreacionNodo(Nodo nodo, String solicitudNodoAprobado);

	public void notificarSolicitudCreacionNodoAVendedor(Integer idVendedor, String nombre, Cliente usuario) throws VendedorInexistenteException;

	public void notificarCancelacionDeSolicitudCreacionNodoAVendedor(Integer idVendedor, String nombreNodo,
			Usuario usuarioSolicitante) throws VendedorInexistenteException;

	public void enviarEmailDeSolicitudDePertenenciaANodo(Nodo nodo, Cliente usuario);

	public void notificarCancelacionDeSolicitudDePertenenciaANodo(SolicitudPertenenciaNodo solicitudpertenencia);

	public void notificarGestionDeSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia);

	void notificarConfirmacionNodoCompraOtroMiembro(String emailVendedor, String emailCliente, String nombreCliente,
			String alias);

	public void enviarEmailDeAvisoDeCambioDeTipoDeNodoAVendedor(Nodo nodo);

	void notificarSolicitudDePertenenciaANodo(String emailCliente, String emailAdministrador, String nombreCliente,
			String aliasNodo);

	void notificarGestionDeSolicitudDePertenenciaANodo(String accion, String emailCliente, String emailAdministrador,
			String aliasNodo);

	void notificarCancelacionDeSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia);

}
