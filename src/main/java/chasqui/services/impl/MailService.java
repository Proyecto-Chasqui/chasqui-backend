package chasqui.services.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.UsuarioExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.security.PasswordGenerator;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;
import freemarker.template.Configuration;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailService {
	@Autowired
	private JavaMailSender mailSender;	
	@Autowired
	private UsuarioService usuarioService;	
	@Autowired
	private VendedorService vendedorService;
	@Autowired
	private Encrypter encrypter;
	@Autowired
	private PasswordGenerator passwordGenerator;
	@Autowired
	private InvitacionService invitacionService;
	@Autowired
	private GrupoService grupoService;
	
	public static final Logger logger = Logger.getLogger(MailService.class);
	
/*
 * ***********************************************
 * METODOS PUBLICOS 
 * ***********************************************
 */
	
	public void enviarEmailBienvenidaVendedor(String destino,String usuario,String password) throws IOException, MessagingException, TemplateException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("nombreUsuario", usuario);
		params.put("passwordUsuario", password);
		

		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_BIENVENIDA_VENDEDOR, destino, Constantes.SUBJECT_BIENVENIDO, params);
	}
	
	public void enviarEmailBienvenidaCliente(String destino, String nombre, String apellido) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("email", destino);
		params.put("nombreApellido", nombre + " " + apellido);
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_BIENVENIDA_CLIENTE, destino, Constantes.SUBJECT_BIENVENIDO, params);
	}
	
	public void enviarEmailNotificacionPedido(String destino,String cuerpoEmail,String nombreUsuario, String apellidoUsuario, Vendedor vendedor) throws IOException, MessagingException, TemplateException{

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("cuerpo", cuerpoEmail);
		params.put("nombreUsuario", nombreUsuario);
		params.put("apellido", apellidoUsuario);
		String catalogo = this.generarUrlCatalogo(vendedor.getUrl(), vendedor.getNombreCorto());
		//CatalogoVendedor es la URL al catalogo de productos del vendedor que envia este correo.
		//Actualmente no se utiliza. Pero deberia estar en el logo y baner del email.
		params.put("catalogoVendedor", catalogo);
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_NOTIFICACION_PEDIDO, destino, Constantes.SUBJECT_ALERT_VENCIMIENTO, params);
	}
		
	public void enviarmailInvitadoSinRegistrar(Cliente clienteOrigen, String destino, String urlVendedor, String nombreCorto, String nombreVendedor, Integer idGrupo) throws Exception  {
		Map<String,Object> params = new HashMap<String,Object>();
		
		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);
		String idInvitacion = invitacionService.obtenerInvitacionAGCCporIDGrupo(destino, idGrupo).getId().toString();
		
		params.put("nombreEmisor",clienteOrigen.getNombre());
		params.put("apellidoEmisor",clienteOrigen.getApellido());
		params.put("aliasGrupo",grupo.getAlias());
		params.put("nombreVendedor", nombreVendedor);
		String slash = (urlVendedor.endsWith("/"))?"":"/";
		params.put("urlRegistracion", urlVendedor +slash + "#/" + nombreCorto + "/registro/gcc/" + encrypter.encryptURL(idInvitacion));
		String catalogo = this.generarUrlCatalogo(urlVendedor, nombreCorto);
		params.put("catalogoVendedor", catalogo);
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_INVITAR_GCC_NO_REGISTRADO, destino, Constantes.SUBJECT_INVITACION_NO_REGISTRADO, params);
	}
			
	public void enviarEmailInvitadoRegistrado(Cliente clienteOrigen, String destino, String aliasGrupo, String urlVendedor, String nombreCorto, String nombreVendedor) throws IOException, MessagingException, TemplateException, UsuarioInexistenteException  {		
		Map<String,Object> params = new HashMap<String,Object>();
		Cliente clienteInvitado = usuarioService.obtenerClientePorEmail(destino);
		params.put("nombreInvitado", clienteInvitado.getNombre());
		params.put("nombreEmisor", clienteOrigen.getNombre());
		params.put("apellidoEmisor", clienteOrigen.getApellido());
		params.put("aliasGrupo", aliasGrupo);
		params.put("vendedor", nombreVendedor);	
		
		params.put("mailOrigen",clienteOrigen.getEmail());
		
		String slash = (urlVendedor.endsWith("/"))?"":"/";
		params.put("urlRegistracion", urlVendedor +slash + "#/" + nombreCorto + "/registro" );
		String catalogo = this.generarUrlCatalogo(urlVendedor, nombreCorto);
		params.put("catalogoVendedor", catalogo);
		
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_INVITAR_GCC_REGISTRADO, destino,Constantes.SUBJECT_INVITACION_REGISTRADO, params);
		
	}
	
	/**
	 * Esta invitacion no esta siendo utilizada actualmente,
	 * serviria para hacer una invitacion no relacionada con un grupo.
	 * Para esto es necesario que referencie el catalogo como otros metodos de envio de email.
	 * Ademas no deberia estar hardcodeado un vendedor
	 * @param clienteOrigen
	 * @param destino
	 * @throws Exception
	 */
	@Deprecated
	public void enviarEmailDeInvitacionChasqui(Cliente clienteOrigen, String destino) throws Exception {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("usuarioOrigen", clienteOrigen.getUsername());
		params.put("mailOrigen",clienteOrigen.getEmail());
		params.put("vendedor", "Puente Del Sur");

		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_INVITACION_CHASQUI, destino, Constantes.SUBJECT_CONOCES_CHASQUI, params);
	}
	
	public void enviarEmailDeInvitacionAGCCAceptada(GrupoCC grupo, Cliente invitado) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("aliasGrupo", grupo.getAlias());
		params.put("nombreInvitado", invitado.getNombre());
		params.put("apellidoInvitado", invitado.getApellido());
		params.put("usuarioInvitado",invitado.getUsername());
		params.put("vendedor", grupo.getVendedor().getNombre());
		params.put("nombreEmisor", grupo.getAdministrador().getNombre());
		String catalogo = this.generarUrlCatalogo(grupo.getVendedor().getUrl(), grupo.getVendedor().getNombreCorto());
		params.put("catalogoVendedor", catalogo);
		
		String subject = Constantes.SUBJECT_INVITACION_GCC_ACEPTADA.replaceAll("<usuario>", invitado.getUsername());

		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_ACEPTAR_INVITACION_GCC, grupo.getAdministrador().getEmail(), subject, params);
	}
	
	@Transactional
	public void enviarEmailRecuperoContraseña(String destino, String usuario) throws Exception{
	
		String password = passwordGenerator.generateRandomToken(); 
		usuarioService.modificarPasswordUsuario(destino, encrypter.encrypt(password));
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("nombreUsuario", usuario);
		params.put("passwordUsuario", password);
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_RECUPERO, destino, Constantes.AVISO_DE_RECUPERO_DE_CONTRASEÑA, params);
		
		
	}

	@Transactional
	public void enviarEmailRecuperoContraseñaCliente(String email) throws Exception {
		if (usuarioService.existeUsuarioCon(email)) {
			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			this.enviarEmailRecuperoContraseña(email, c.getUsername());
		}
		else {
			throw new UsuarioExistenteException() ;
		}
	}




	/**
	 * Este método debe enviar notificaciones genericas
	 * @param emailOrigen
	 * @param emailClienteDestino
	 * @param mensaje
	 */
	public void enviarEmailNotificacionChasqui(String emailOrigen, String nombreUsuario, final String emailClienteDestino, String mensaje, final String subject) {

		final Map<String,Object> params = new HashMap<String,Object>();
		params.put("nombreUsuario", nombreUsuario);
		params.put("cuerpo", mensaje);
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_NOTIFICACION, emailClienteDestino, subject, params);
		
	}

	/**
	 * Este método envía un mail con el detalle del pedido CONFIRMADO al cliente y al vendedor.
	 * @param emailVendedor
	 * @param emailCliente
	 * @param p
	 * @throws UsuarioInexistenteException 
	 */
	public void enviarEmailConfirmacionPedido(final String emailVendedor,final String emailCliente, final Pedido p) throws UsuarioInexistenteException{
		Direccion direccion = null;
		String textoEnEmail = "";
		String textoDeDireccionDeEntrega = "";
		if(p.getDireccionEntrega() != null) {
			direccion = p.getDireccionEntrega();
			textoDeDireccionDeEntrega = "Dirección de envio";
		}
		if(p.getPuntoDeRetiro() != null) {
			direccion = p.getPuntoDeRetiro().getDireccion();
			textoDeDireccionDeEntrega ="Dirección de retiro";
		}
		Vendedor vendedor = (Vendedor) usuarioService.obtenerUsuarioPorEmail(emailVendedor);
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);
		String catalogo = this.generarUrlCatalogo(vendedor.getUrl(), vendedor.getNombreCorto());
		
		String tablaContenidoPedido = armarTablaContenidoDePedido(p);
		String tablaDireccionDeEntrega = armarTablaDireccionDeEntrega(direccion,textoDeDireccionDeEntrega);
		String cuerpoCliente = armarCuerpoCliente(cliente.getNombre(), vendedor.getNombre());
		String cuerpoVendedor = armarCuerpoVendedor(emailCliente);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("cuerpo", cuerpoCliente);
		params.put("tablaContenidoPedido",tablaContenidoPedido);
		params.put("tablaDireccionDeEntrega", tablaDireccionDeEntrega);
		params.put("sugerencia",Constantes.SUGERENCIA.replace("<nombreVendedor>", vendedor.getNombre()));
		params.put("textoDetalle", textoEnEmail);
		params.put("catalogoVendedor", catalogo);

		this.enviarMailEnThreadAparte(Constantes.CONFIRMACION_COMPRA_TEMPLATE_URL, emailCliente,formarTag(p) + Constantes.CONFIRMACIÓN_DE_COMPRA_SUBJECT, params);
		
		Map<String,Object> paramsVendedor = new HashMap<String,Object>();
		paramsVendedor.put("cuerpo", cuerpoVendedor);
		paramsVendedor.put("tablaContenidoPedido",tablaContenidoPedido);
		paramsVendedor.put("tablaDireccionDeEntrega", tablaDireccionDeEntrega);
		paramsVendedor.put("sugerencia","");
		paramsVendedor.put("catalogoVendedor", catalogo);
		params.put("textoDetalle", textoEnEmail);

		this.enviarMailEnThreadAparte(Constantes.CONFIRMACION_COMPRA_TEMPLATE_URL, emailVendedor, formarTag(p) + Constantes.CONFIRMACIÓN_DE_COMPRA_SUBJECT, paramsVendedor);
		
	}
	
	public void enviarEmailVencimientoPedido(String nombreVendedor, Cliente cliente, Pedido pedido, String fechaCreacionPedido,
			String cantidadDeMinutosParaExpiracion) throws VendedorInexistenteException {
		
		Map<String,Object> params = new HashMap<String,Object>();

		Vendedor vendedor = vendedorService.obtenerVendedor(nombreVendedor);
		String catalogo = this.generarUrlCatalogo(vendedor.getUrl(), vendedor.getNombreCorto());
		params.put("catalogoVendedor", catalogo);
		
		params.put("vendedor", nombreVendedor);
		params.put("fechaCreacionPedido",fechaCreacionPedido);
		params.put("cantidadDeMinutosParaExpiracion", cantidadDeMinutosParaExpiracion);
		params.put("agradecimiento", Constantes.AGRADECIMIENTO);
		params.put("nombreUsuario", cliente.getUsername());
		
		if(pedido.getPerteneceAPedidoGrupal()){
			params.put("tipoDePedido", "grupal");
			params.put("aliasColectivo", "del grupo " + pedido.getPedidoColectivo().getColectivo().getAlias());
		}else{
			params.put("tipoDePedido", "individual");
			params.put("aliasColectivo", "");
		}
		
		this.enviarMailEnThreadAparte(Constantes.VENCIMIENTO_PEDIDO_TEMPLATE, cliente.getEmail(), formarTag(pedido) + Constantes.VENCIMIENTO_DE_PEDIDO_SUBJECT, params);
		
	}
	
	public void enviarEmailPreparacionDePedido(Pedido pedido) {
		Map<String,Object> params = new HashMap<String,Object>();
		Direccion direccion;
		String textoEnEmail = "";
		String textoDeDireccionDeEntrega = "";
		if(pedido.getDireccionEntrega() != null) {
			direccion = pedido.getDireccionEntrega();
			
			textoEnEmail = "Hola "+ this.generateSpan(pedido.getCliente().getNombre(), "00adee") +", tu pedido de "+ this.generateSpan(pedido.getNombreVendedor(), "00adee") +" está preparado para ser entregado. El detalle de tu pedido es:";
			textoDeDireccionDeEntrega = "Será enviado a la siguiente dirección";
		}else {
			direccion = pedido.getPuntoDeRetiro().getDireccion();
			textoEnEmail = "Su pedido de " + pedido.getNombreVendedor() +" esta preparado para que lo pueda pasar a retirar. El detalle de su pedido es el siguiente:";
			textoDeDireccionDeEntrega ="Dirección donde puede pasar a retirar su pedido";
		}
		
		String tablaContenidoPedido = armarTablaContenidoDePedido(pedido);
		String tablaDireccionEntrega = armarTablaDireccionDeEntrega(direccion,textoDeDireccionDeEntrega);
		
		params.put("tablaContenidoPedido", tablaContenidoPedido);
		params.put("tablaDireccionEntrega", tablaDireccionEntrega);
		params.put("textoDetalle", textoEnEmail);
		params.put("textoDeDireccionDeEntrega", textoDeDireccionDeEntrega);
		params.put("sugerencia", Constantes.SUGERENCIA.replace("<nombreVendedor>", this.generateSpan(pedido.getNombreVendedor(), "00adee")));
		
		this.enviarMailEnThreadAparte(Constantes.PEDIDO_PREPARADO_TEMPLATE, pedido.getCliente().getEmail(), formarTag(pedido) +Constantes.PEDIDO_PREPARADO_SUBJECT, params);
		
	}
	
	/**
	 * Adds the html tag <span> to the string
	 * e.g., The String "Duck" with the code "00adee" returns:
	 * <span style="font-weight: bold; color: #00adee;">Duck</span>
	 * @param str
	 * @param colorCode Hexadecimal RGB Code RRGGBB
	 * @return
	 */
	private String generateSpan(String str, String colorCode){
		return "<span style=\"font-weight: bold; color: #"+colorCode+";\">"+ str +"</span>";
	}
	
	public void enviarEmailCierreDePedidoColectivo(PedidoColectivo pedidoColectivo) {
		Map<String,Object> params = new HashMap<String,Object>();
		Direccion direccion;
		
		String catalogo = this.generarUrlCatalogo(pedidoColectivo.getColectivo().getVendedor().getUrl(), pedidoColectivo.getColectivo().getVendedor().getNombreCorto());
		params.put("catalogoVendedor", catalogo);
		
		String textoEnEmail = "";
		String textoDeDireccionDeEntrega = "";
		if(pedidoColectivo.getDireccionEntrega() != null) {
			direccion = pedidoColectivo.getDireccionEntrega();
			textoEnEmail = "Su pedido colectivo hecho en <b>"+ pedidoColectivo.getColectivo().getVendedor().getNombre() +" </b>ha sido confirmado. El detalle de su pedido es el siguiente:";
			textoDeDireccionDeEntrega = "La dirección elegida es la siguiente:";
		}else {
			direccion = pedidoColectivo.getPuntoDeRetiro().getDireccion();
			textoEnEmail = "Su pedido colectivo hecho en <b>"+ pedidoColectivo.getColectivo().getVendedor().getNombre() + " </b>ha sido confirmado. El detalle de su pedido es el siguiente:";
			textoDeDireccionDeEntrega ="El punto de retiro elegido es el siguiente:";
		}
		//Genero tabla de contenido de pedido de cada persona
		String tablaContenidoDePedidoColectivo = this.armarTablaContenidoDePedidoColectivo(pedidoColectivo);
		//La direccion del grupo
		String tablaDireccionEntrega = armarTablaDireccionDeEntrega(direccion,textoDeDireccionDeEntrega);
		
		List<String> emailsClientesDestino = obtenerEmails(pedidoColectivo);
		
		params.put("tablaContenidoDePedidoColectivo", tablaContenidoDePedidoColectivo);
		params.put("tablaDireccionEntrega", tablaDireccionEntrega);
		params.put("agradecimiento", Constantes.AGRADECIMIENTO);
		params.put("textoDetalle", textoEnEmail);
		
		//se envia todo a todos los integrantes del grupo
		this.enviarMailEnThreadAparte(Constantes.PEDIDOS_COLECTIVOS_CONFIRMADOS_TEMPLATE, pedidoColectivo.getColectivo().getAdministrador().getEmail(), formarTag(pedidoColectivo) + Constantes.PEDIDO_COLECTIVO_CONFIRMADO, params);
		
	}
	
	public void enviarEmailPreparacionDePedidoColectivo(PedidoColectivo pedidoColectivo) {
		Map<String,Object> params = new HashMap<String,Object>();
		Direccion direccion;
		String catalogo = this.generarUrlCatalogo(pedidoColectivo.getColectivo().getVendedor().getUrl(), pedidoColectivo.getColectivo().getVendedor().getNombreCorto());
		params.put("catalogoVendedor", catalogo);
		String textoEnEmail = "";
		String textoDeDireccionDeEntrega = "";
		if(pedidoColectivo.getDireccionEntrega() != null) {
			direccion = pedidoColectivo.getDireccionEntrega();
			textoEnEmail = "Hola, "+ this.generateSpan(pedidoColectivo.getColectivo().getAdministrador().getNombre(), "00adee") +". El pedido colectivo del grupo "+ this.generateSpan(pedidoColectivo.getColectivo().getAlias(), "00adee") +" está preparado para ser entregado. El detalle del pedido es el siguiente:";
			textoDeDireccionDeEntrega = "Será enviado a la siguiente dirección";
		}else {
			direccion = pedidoColectivo.getPuntoDeRetiro().getDireccion();
			textoEnEmail = "Su pedido colectivo hecho en <b>"+ pedidoColectivo.getColectivo().getVendedor().getNombre() +" </b>esta preparado para que lo pueda pasar a retirar. El detalle de su pedido es el siguiente:";
			textoDeDireccionDeEntrega ="Dirección donde puede pasar a retirar su pedido";
		}
		//Genero tabla de contenido de pedido de cada persona
		String tablaContenidoDePedidoColectivo = this.armarTablaContenidoDePedidoColectivo(pedidoColectivo);
		//La direccion del grupo
		String tablaDireccionEntrega = armarTablaDireccionDeEntrega(direccion,textoDeDireccionDeEntrega);
		
		List<String> emailsClientesDestino = obtenerEmails(pedidoColectivo);
		
		params.put("tablaContenidoDePedidoColectivo", tablaContenidoDePedidoColectivo);
		params.put("tablaDireccionEntrega", tablaDireccionEntrega);
		params.put("sugerencia", Constantes.SUGERENCIA.replace("<nombreVendedor>", this.generateSpan(pedidoColectivo.getColectivo().getVendedor().getNombre(), "00adee")));
		params.put("textoDetalle", textoEnEmail);
		
		
		//se envia todo a todos los integrantes del grupo
		this.enviarMailsEnThreadAparte(Constantes.PEDIDOS_PREPARADOS_TEMPLATE, emailsClientesDestino, formarTag(pedidoColectivo) +Constantes.PEDIDO_COLECTIVO_PREPARADO, params);
		
	}
	
	private List<String> obtenerEmails(PedidoColectivo pedidoColectivo) {
		List<String> emails = new ArrayList<>();
		for(String e: pedidoColectivo.getPedidosIndividuales().keySet()) {
			if (pedidoColectivo.getPedidosIndividuales().get(e).getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
				emails.add(e);
			}
		}
		emails.add(pedidoColectivo.getColectivo().getAdministrador().getEmail());
		return emails;
	}

	private String armarTablaContenidoDePedidoColectivo(PedidoColectivo pedidoColectivo) {
		
		String tablaContenidoDePedidoColectivo ="";
		
		Iterator<Entry<String, Pedido>> it = pedidoColectivo.getPedidosIndividuales().entrySet().iterator();
		while (it.hasNext()) {
		    Entry<String, Pedido> pair = it.next();
		    Pedido pedido = pair.getValue();
		    if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
		    	tablaContenidoDePedidoColectivo += armarInformacionDelCliente(pedido.getCliente());
		    	tablaContenidoDePedidoColectivo += this.armarTablaContenidoDePedido(pedido);
		    }
		}
		
		return tablaContenidoDePedidoColectivo;
		
	}

	private String armarInformacionDelCliente(Cliente cliente) {
		String informacionDelCliente = "";
		informacionDelCliente += cliente.getNombre() + " " + cliente.getApellido();
		
		
		return informacionDelCliente;
	}

	public void enviarEmailNuevoAdministrador(Cliente administradorAnterior, Cliente nuevoAdministrador, GrupoCC grupo) {
		Map<String,Object> params = new HashMap<String,Object>();
		
		String catalogo = this.generarUrlCatalogo(grupo.getVendedor().getUrl(), grupo.getVendedor().getNombreCorto());
		params.put("nombreNuevoAdmin", nuevoAdministrador.getNombre());
		params.put("apellidoNuevoAdmin", nuevoAdministrador.getApellido());
		params.put("nombreViejoAdmin", administradorAnterior.getNombre());
		params.put("apellidoViejoAdmin", administradorAnterior.getApellido());
		params.put("nombreGrupo", grupo.getAlias());
		params.put("catalogoVendedor", catalogo);
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_NUEVO_ADMINISTRADOR, nuevoAdministrador.getEmail(), Constantes.NUEVO_ADMINISTRADOR_SUBJECT, params);
	}
	
/*
 * ***********************************************
 * METODOS PRIVADOS 
 * ***********************************************
 */
	
	private void enviarMailsEnThreadAparte(String pedidosPreparadosTemplate, List<String> emailsClientesDestino, String subject, Map<String, Object> params) {
		for(String emailDestino : emailsClientesDestino){
			enviarMailEnThreadAparte(pedidosPreparadosTemplate, emailDestino, subject, params);
		}
	}
	
	private void enviarMailEnThreadAparte(final String template,final String emailClienteDestino, final String subject, final Map<String,Object> params ){
		new Thread(){
			private void enviar(String templateURL, String asunto, String destino, Map<String,Object> params ) throws TemplateException, MessagingException, IOException{
				Template template = this.obtenerTemplate(templateURL); 
				MimeMessage m = mailSender.createMimeMessage();
				m.setSubject(MimeUtility.encodeText(asunto,"UTF-8","B"));
				MimeMessageHelper helper = new MimeMessageHelper(m,true,"UTF-8");
				StringWriter writer = new StringWriter();
				ClassPathResource resource = new ClassPathResource("templates/imagenes/logo.png");
				helper.setFrom("administrator-chasqui-noreply@chasqui.org");
				helper.setTo(destino);
				template.process(params, writer);				
				writer.flush();
				writer.close();
				helper.setText(writer.toString(),true);
				helper.addInline("logochasqui", resource);
				insertarLogoCorrespondiente(templateURL,helper);
				try {
					mailSender.send(m); //TODO comentar si no funca el SMTP	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			private void insertarLogoCorrespondiente(String template, MimeMessageHelper helper) throws MessagingException {
				if(Constantes.TEMPLATE_BIENVENIDA_CLIENTE.equals(template)||Constantes.TEMPLATE_BIENVENIDA_VENDEDOR.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/unnamed.png");
					helper.addInline("bienvenido", resource);
				}
				if(Constantes.CONFIRMACION_COMPRA_TEMPLATE_URL.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/confirmacion.png");
					helper.addInline("confirmacion", resource);
				}
				
				if(Constantes.TEMPLATE_INVITACION_CHASQUI.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/invitacion.png");
					helper.addInline("invitacion", resource);
				}
				
				if(Constantes.TEMPLATE_ACEPTAR_INVITACION_GCC.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/aceptadx.png");
					helper.addInline("aceptado", resource);
				}
				
				if(Constantes.TEMPLATE_INVITAR_GCC_REGISTRADO.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/grupodecomprascolectivas.png");
					helper.addInline("grupodecomprascolectivas", resource);
				}
				
				if(Constantes.TEMPLATE_INVITAR_GCC_NO_REGISTRADO.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/invitadxaparticipar.png");
					helper.addInline("invitadxaparticipar", resource);
				}
				
				if(Constantes.TEMPLATE_NUEVO_ADMINISTRADOR.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/administracion.png");
					helper.addInline("administracion", resource);
				}
				
				if(Constantes.PEDIDO_PREPARADO_TEMPLATE.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/pedidopreparado.png");
					helper.addInline("pedidopreparado", resource);
				}
				
				if(Constantes.PEDIDOS_PREPARADOS_TEMPLATE.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/pedidospreparados.png");
					helper.addInline("pedidospreparados", resource);
				}
				
				if(Constantes.TEMPLATE_RECUPERO.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/recuperacion.png");
					helper.addInline("recuperacion", resource);
				}
				
				if(Constantes.VENCIMIENTO_PEDIDO_TEMPLATE.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/vencimiento.png");
					helper.addInline("vencimiento", resource);
				}
				
				if(Constantes.PEDIDOS_COLECTIVOS_CONFIRMADOS_TEMPLATE.equals(template)) {
					ClassPathResource resource = new ClassPathResource("templates/imagenes/confirmacion.png");
					helper.addInline("confirmacion", resource);
				}
			}

			private Template obtenerTemplate(String nombreTemplate) throws IOException{
				Configuration c = new Configuration();
				c.setObjectWrapper(new SimpleObjectWrapper());
				c.setClassForTemplateLoading(MailService.class, "/templates/mail/");
				return c.getTemplate(nombreTemplate);
			}

			public void run(){
				try {
					this.enviar(template, subject, emailClienteDestino, params);
				} catch (TemplateException | MessagingException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

		
	
	
	
	
	private String armarCuerpoCliente(String nombre, String nombreVendedor){
		return "¡"+ nombre +" tu pedido en "+ nombreVendedor +" está confirmado!" +
				" <br> " +
				"Detalles de tu compra:";

		
	}

	
	private String armarCuerpoVendedor(String usuario){
		return "El usuario "+ usuario +" confirmó su compra (Los detalles de la misma se encuentran debajo y también pueden visualizarse en el panel de administración).";
	}
	
	
	
	
	private String armarTablaDireccionDeEntrega(Direccion d,String texto){
		if (d!=null) {
			String departamento =  d.getDepartamento() != null ? d.getDepartamento() : "---";
			String codigoPostal = d.getCodigoPostal() == null ?  "---" : d.getCodigoPostal();
			String localidad = d.getLocalidad() == null ? "---": d.getLocalidad();
			
			String tabla=
					"<br><br>"
					+"<table width=\"600\" cellpadding=\"0\" border=\"0\" bgcolor=\"#b8dee8\" align=\"center\">"
					   +"<thead bgcolor=\"#313231\">" 
					       +"<tr height=\"32\" width=\"100%\">"
					           +"<td colspan=\"2\"><font color=\"white\">" + texto +"</font></td>"
					       +"</tr>"
					  +"</thead>"
					  +"<tbody align=\"left\">"
					      +"<tr>"
					           +"<td bgcolor=\"#c1c1c1\" width=\"40%\"> Calle </td>"
					           +"<td>"+d.getCalle()+"</td>"
					      +"</tr>"
					      +"<tr>"
					           +"<td bgcolor=\"#c1c1c1\" width=\"40%\"> Altura</td>"
					           +"<td>"+d.getAltura()+"</td>"
					      +"</tr>"
					      +"<tr>"
					           +"<td bgcolor=\"#c1c1c1\" width=\"40%\"> Departamento</td>"
					           +"<td>"+departamento+"</td>"
					       +"</tr>"
					      +"<tr>"
					           +"<td bgcolor=\"#c1c1c1\" width=\"40%\"> Cod.Postal</td>"
					           +"<td>"+ codigoPostal +"</td>"
					       +"</tr>"
					      +"<tr>"
					           +"<td bgcolor=\"#c1c1c1\" width=\"40%\"> Localidad</td>"
					           +"<td>"+ localidad +"</td>"
					       +"</tr>"	   
					   +"</tbody>"
					+"</table>"
					+"<br><br>";
			
			return tabla;
				
		}
		else 
			return "";
	}
	

	private String armarTablaContenidoDePedido(Pedido p) {
		String tabla = armarHeader();
		String footer = armarFooter(p.getMontoActual());
		for(ProductoPedido pp : p.getProductosEnPedido()){
			tabla += armarFilaDetalleProducto(pp);
		}		
		tabla += footer + "<br>";
		return tabla;
	}

	
	private String armarHeader() {
		return "<table width=\"600\" cellpadding=\"0\" border=\"0\" bgcolor=\"#b8dee8\" align=\"center\">"
		   	   + "<thead bgcolor=\"#313231\">" 
		       +  "<tr height=\"32\">"
		       +     "<th><font color=\"white\">PRODUCTO</font></th>"
		       +     "<th><font color=\"white\">PRECIO POR UNIDAD</font></th>"
		       +     "<th><font color=\"white\">CANTIDAD</font></th>"
		       +  "</tr>"
		       + "</thead>"
		       + "<tbody>";
	}
	
	private String armarFilaDetalleProducto(ProductoPedido pp){
		return  "<tr>"
				+	"<td>"+pp.getNombreProducto()+"</td>"
				+	"<td>"+pp.getPrecio()+"</td>"
				+	"<td>"+pp.getCantidad()+"</td>"
				+"</tr>";
				
				
		//"<tr><td>"+ pp.getNombreProducto() + pp.getNombreVariante() + "</td><td>" +pp.getPrecio()+ "</td><td> "+ pp.getCantidad() +"</td></tr>";
	}	

	private String armarFooter(Double total){
		return   "</tbody>"
				 + "<tfoot bgcolor=\"#c1c1c1\">"
				 +"<tr height=\"32\">"
				 +	"<th>TOTAL</th>"
				 +	"<th>$"+total+"</th>"
				 +  "<th></th>"		   
				 +"</tr>"
				 + "</tfoot>"
				 + "</table>";
	}
	
	private String formarTag(PedidoColectivo p) {
		return "[ Pedido N° "+ p.getId()+" ] ";
	}
	
	private String formarTag(Pedido p) {
		return "[ Pedido N° "+ p.getId()+" ] ";
	}
	
	/**
	 * Genera la URL al catalo del vendedor.
	 * @param url La url del vendedor.
	 * @param nombreCorto El nombre corto del vendedor.
	 * @return El catalogo de productos para el vendedor.
	 */
	private String generarUrlCatalogo(String url, String nombreCorto){
		String slash = (url.endsWith("/"))?"":"/";
		return (url + slash + "#/" + nombreCorto + "/productos");
	}

}
