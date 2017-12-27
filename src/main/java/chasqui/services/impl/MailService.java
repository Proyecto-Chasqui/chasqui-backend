package chasqui.services.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

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
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.security.Encrypter;
import chasqui.security.PasswordGenerator;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.UsuarioService;
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
	private Encrypter encrypter;
	@Autowired
	private PasswordGenerator passwordGenerator;
	@Autowired
	private InvitacionService invitacionService;
	
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
	
	public void enviarEmailNotificacionPedido(String destino,String cuerpoEmail,String nombreUsuario, String apellidoUsuario) throws IOException, MessagingException, TemplateException{

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("cuerpo", cuerpoEmail);
		params.put("nombreUsuario", nombreUsuario); //Modificado nombre a nombreUsuario 02-06
		params.put("apellido", apellidoUsuario);

		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_NOTIFICACION_PEDIDO, destino, Constantes.SUBJECT_ALERT_VENCIMIENTO, params);
	}
		
	public void enviarmailInvitadoSinRegistrar(Cliente clienteOrigen, String destino, String urlVendedor, String nombreVendedor,Integer idGrupo) throws Exception  {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("usuarioOrigen", clienteOrigen.getUsername());
		params.put("mailOrigen",clienteOrigen.getEmail());
		params.put("vendedor", nombreVendedor);
		String slash = (urlVendedor.endsWith("/"))?"":"/";
		String idInvitacion = invitacionService.obtenerInvitacionAGCCporIDGrupo(destino, idGrupo).getId().toString();
		params.put("urlRegistracion", urlVendedor +slash + "#/registro/gcc/" + encrypter.encryptURL(idInvitacion));
			 		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_INVITAR_GCC_NO_REGISTRADO, destino, Constantes.SUBJECT_INVITACION_NO_REGISTRADO, params);
	}
			
	public void enviarEmailInvitadoRegistrado(Cliente clienteOrigen, String destino, String urlVendedor, String nombreVendedor) throws IOException, MessagingException, TemplateException  {		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("usuarioOrigen", clienteOrigen.getUsername());
		params.put("mailOrigen",clienteOrigen.getEmail());
		params.put("vendedor", nombreVendedor);	
		params.put("urlRegistracion", urlVendedor );//TODO revisar
		
		this.enviarMailEnThreadAparte(Constantes.TEMPLATE_INVITAR_GCC_REGISTRADO, destino,Constantes.SUBJECT_INVITACION_REGISTRADO, params);
		
	}
	
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
		params.put("usuarioInvitado",invitado.getUsername());
		params.put("vendedor", grupo.getVendedor().getNombre());
		params.put("nombreUsuario", grupo.getAdministrador().getUsername());

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
	 */
	public void enviarEmailConfirmacionPedido(final String emailVendedor,final String emailCliente, final Pedido p){


		String tablaContenidoPedido = armarTablaContenidoDePedido(p);
		String tablaDireccionEntrega = armarTablaDireccionDeEntrega(p.getDireccionEntrega());
		String cuerpoCliente = armarCuerpoCliente();
		String cuerpoVendedor = armarCuerpoVendedor(emailCliente);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("cuerpo", cuerpoCliente);
		params.put("tablaContenidoPedido",tablaContenidoPedido);
		params.put("tablaDireccionDeEntrega", tablaDireccionEntrega);
		params.put("agradecimiento",Constantes.AGRADECIMIENTO);

		this.enviarMailEnThreadAparte(Constantes.CONFIRMACION_COMPRA_TEMPLATE_URL, emailCliente, Constantes.CONFIRMACIÓN_DE_COMPRA_SUBJECT, params);
		
		Map<String,Object> paramsVendedor = new HashMap<String,Object>();
		paramsVendedor.put("cuerpo", cuerpoVendedor);
		paramsVendedor.put("tablaContenidoPedido",tablaContenidoPedido);
		paramsVendedor.put("tablaDireccionDeEntrega", tablaDireccionEntrega);
		paramsVendedor.put("agradecimiento",Constantes.AGRADECIMIENTO);

		this.enviarMailEnThreadAparte(Constantes.CONFIRMACION_COMPRA_TEMPLATE_URL, emailVendedor, Constantes.CONFIRMACIÓN_DE_COMPRA_SUBJECT, paramsVendedor);
		
	}
	
	public void enviarEmailVencimientoPedido(String nombreVendedor, Cliente cliente, String fechaCreacionPedido,
			String cantidadDeMinutosParaExpiracion) {
		

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("vendedor", nombreVendedor);
		params.put("fechaCreacionPedido",fechaCreacionPedido);
		params.put("cantidadDeMinutosParaExpiracion", cantidadDeMinutosParaExpiracion);
		params.put("agradecimiento", Constantes.AGRADECIMIENTO);
		params.put("nombreUsuario", cliente.getUsername());
		
		this.enviarMailEnThreadAparte(Constantes.VENCIMIENTO_PEDIDO_TEMPLATE, cliente.getEmail(), Constantes.VENCIMIENTO_DE_PEDIDO_SUBJECT, params);
		
	}
	
/*
 * ***********************************************
 * METODOS PRIVADOS 
 * ***********************************************
 */
	
	private void enviarMailEnThreadAparte(final String template,final String emailClienteDestino, final String subject, final Map<String,Object> params ){
		new Thread(){
			private void enviar(String templateURL, String asunto, String destino, Map<String,Object> params ) throws TemplateException, MessagingException, IOException{
				Template template = this.obtenerTemplate(templateURL); 
				MimeMessage m = mailSender.createMimeMessage();
				m.setSubject(MimeUtility.encodeText(asunto,"UTF-8","B"));
				MimeMessageHelper helper = new MimeMessageHelper(m,true,"UTF-8");
				StringWriter writer = new StringWriter();
				ClassPathResource resource = new ClassPathResource("templates/imagenes/chasqui.png");
				helper.setFrom("administrator-chasqui-noreply@chasqui.org");
				helper.setTo(destino);
				template.process(params, writer);
				
				writer.flush();
				writer.close();
				helper.setText(writer.toString(),true);
				helper.addInline("logochasqui", resource);
				try {
					mailSender.send(m); //TODO comentar si no funca el SMTP	
				} catch (Exception e) {
					e.printStackTrace();
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

		
	
	
	
	
	private String armarCuerpoCliente(){
		return "Datos de confirmación de compra";
	}

	
	private String armarCuerpoVendedor(String usuario){
		return "El usuario: "+ usuario +" ha confirmado su compra (Los detalles del mismo se encuentran debajo y también pueden visualizarse en el panel de administración)";
	}
	
	
	
	
	private String armarTablaDireccionDeEntrega(Direccion d){
		if (d!=null) {
			String departamento =  d.getDepartamento() != null ? d.getDepartamento() : "---";
			String tabla = "<table border="+ "0" +">"
					+ "<tr><td>Calle:</td><td>" + d.getCalle() + "</td></tr>"
					+ "<tr><td>Altura:</td>" + d.getAltura() + "</td></tr>"
					+ "<tr><td>Departamento:</td>" + departamento + "</td></tr>"
					+ "<tr><td>Cod. posta:</td>" + d.getCodigoPostal() + "</td></tr>"
					+ "<tr><td>Localidad:</td>" + d.getLocalidad() + "</td></tr>";
			
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
		tabla += footer;
		return tabla;
	}

	
	
	private String armarFilaDetalleProducto(ProductoPedido pp){
		return  "<tr><td>"+ pp.getNombreProducto() + pp.getNombreVariante() + "</td><td>" +pp.getPrecio()+ "</td><td> "+ pp.getCantidad() +"</td></tr>";
	}	

	private String armarHeader() {
		return "<table border="+ "1" +"><tr><th>Producto</th><th>Precio por Unidad</th><th>Cantidad</th></tr>";
	}
	
	private String armarFooter(Double total){
		return "<tr><td colspan="+"2"+">Total:</td><td>"+total+"</td></tr></table>";
	}

}
