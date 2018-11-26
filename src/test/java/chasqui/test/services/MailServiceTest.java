package chasqui.test.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.UsuarioService;
import freemarker.template.TemplateException;

@ContextConfiguration(locations = { "file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Component
public class MailServiceTest extends GenericSetUp {

	@Autowired
	public MailService mailService;
	@Autowired UsuarioService usuarioService;
	@Autowired GrupoService grupoService;
	@Autowired NotificacionService notificacionService;
	
	/*
	 * Destinataria debe reemplazarce con la direccion de
	 * correo electrico en la que se desea recibir todos los templates.
	 */
	public String destinatario = "destinatario@dominio.com";
	public String destinatarioSecundario = "destinatarioSecundario@dominio.com";
	public String nombreDeUsuario = "User93";
	public String passwordFalsa = "passw0rd1234";
	Vendedor vendedorDestinatario;
	
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		vendedorDestinatario = new Vendedor();
		vendedorDestinatario.setUsername("Username");
		vendedorDestinatario.setNombre("Nombre");
		vendedorDestinatario.setPassword(encrypter.encrypt("federico"));
		vendedorDestinatario.setEmail(destinatario);
		vendedorDestinatario.setIsRoot(false);
		vendedorDestinatario.setMontoMinimoPedido(213);
		vendedorDestinatario.setUrl("vendedor.proyectochasqui.com/");
		vendedorDestinatario.setNombreCorto("MiniNombre");
		List<String> opciones = new ArrayList<String>();
		opciones.add("si");
		opciones.add("no");
		vendedorDestinatario.setPreguntasDePedidosIndividuales(generarPreguntas(opciones, "Tiene Factura"));
		vendedorDestinatario.setPreguntasDePedidosColectivos(generarPreguntas(opciones, "Tiene Factura"));
		usuarioService.guardarUsuario(vendedorDestinatario);
	}
	
	private List<PreguntaDeConsumo> generarPreguntas(List<String> opciones, String nombre){
		List<PreguntaDeConsumo> lista = new ArrayList<PreguntaDeConsumo>();
		lista.add(new PreguntaDeConsumo("Tiene Factura",true,opciones));
		return lista;
	}
	
	@Test
	public void testEnviarEmailDeBienvenidaDeVendedor() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailBienvenida.ftl
		
		mailService.enviarEmailBienvenidaVendedor(this.destinatario, this.nombreDeUsuario, this.passwordFalsa);
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeBienvenidaAlCliente() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailBienvenidaCliente.ftl
		
		mailService.enviarEmailBienvenidaCliente(this.destinatario, "Rocio", "Otel");;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeConfirmacionDePedido() throws IOException, MessagingException, TemplateException, EstadoPedidoIncorrectoException, UsuarioInexistenteException {
		//Se envia el email del template emailConfirmacionPedido.ftl

		DateTime fechaVencimiento = new DateTime();
		Pedido pedido= new Pedido(vendedorDestinatario, clienteFulano, false, fechaVencimiento.plusHours(24));
		ProductoPedido prodPed = new ProductoPedido(variante, 5,"N/D");
		pedido.agregarProductoPedido(prodPed, fechaVencimiento.plusHours(48));
		pedido.sumarAlMontoActual(prodPed.getPrecio(), prodPed.getCantidad());
		pedido.setDireccionEntrega(direccionCasa);
		mailService.enviarEmailConfirmacionPedido(this.destinatario, this.destinatario, pedido);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeInvitacionAGCCAceptada() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailInvitacionAGCCAceptada.ftl
		
		clienteFulano.setEmail(destinatario);
		vendedor.setEmail(destinatario);
		GrupoCC grupoDeCompras = new GrupoCC(clienteFulano, "La casita de beltran", "El equipo de desarrollo tiene hambre y se organiza!");
		grupoDeCompras.setVendedor(vendedor);
		mailService.enviarEmailDeInvitacionAGCCAceptada(grupoDeCompras, this.clienteJuanPerez);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeInvitacionAChasqui() throws Exception {
		//Se envia el email del template emailInvitacion.ftl
		
		clienteFulano.setEmail(destinatario);
		
		mailService.enviarEmailDeInvitacionChasqui(this.clienteFulano, this.destinatario);
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeInvitacionAGrupoAUsuarioRegistrado() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailInvitadoRegistrado.ftl 
		
		this.vendedor.setNombre("nombre del vendedor");
		this.vendedor.setUrl("urlVendedor");
		this.vendedor.setNombreCorto("nombreCorto");
		
		mailService.enviarEmailInvitadoRegistrado(this.clienteFulano, this.destinatario, this.vendedor.getUrl(), this.vendedor.getNombreCorto(), this.vendedor.getNombre());
		assertEquals(true , true);
	}

	@Test
	public void testEnviarEmailNotificandoElPedido() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailNotificacionPedido.ftl 
		
		this.vendedor.setNombre("nombre del vendedor");
		this.vendedor.setUrl("urlVendedor");
		this.vendedor.setNombreCorto("nombreCorto");
		
		String cuerpoEmail = "Hola, este es el cuerpo del email, el original se obtiene en Chasqui.Properties";
		mailService.enviarEmailNotificacionPedido(this.destinatario, cuerpoEmail, "nombreDelUsuario", "apellidoDelUsuario", vendedor);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeNuevoAdministrador() throws IOException, MessagingException, TemplateException {
		//Se envia el email del template emailNuevoAdministrador.ftl 
		
		clienteFulano.setEmail(this.destinatario);
		clienteJuanPerez.setEmail(this.destinatario);
		vendedor.setEmail(this.destinatario);
		GrupoCC grupoDeCompras = new GrupoCC(clienteFulano, "La casita de beltran", "El equipo de desarrollo tiene hambre y se organiza!");
		grupoDeCompras.setVendedor(vendedor);
		
		mailService.enviarEmailNuevoAdministrador(this.clienteFulano, this.clienteJuanPerez, grupoDeCompras);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDePreparacionDePedido() throws IOException, MessagingException, TemplateException, EstadoPedidoIncorrectoException {
		//Se envia el email del template emailPedidoPreparado.ftl 
		
		this.vendedor.setEmail(this.destinatario);
		this.clienteFulano.setEmail(this.destinatario);
		
		DateTime fechaVencimiento = new DateTime().plusHours(24);
		Pedido pedido = new Pedido(this.vendedor, this.clienteFulano, false, fechaVencimiento);
		ProductoPedido prodPed = new ProductoPedido(variante, 5,"N/D");
		pedido.setDireccionEntrega(direccionCasa);
		pedido.agregarProductoPedido(prodPed, fechaVencimiento.plusHours(48));
		pedido.sumarAlMontoActual(prodPed.getPrecio(), prodPed.getCantidad());
		
		
		mailService.enviarEmailPreparacionDePedido(pedido);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDePreparacionDePedidoColectivo() throws IOException, MessagingException, TemplateException, EstadoPedidoIncorrectoException {
		//Se envia el email del template emailPedidosPreparados.ftl 
		
		this.vendedor.setEmail(this.destinatario);
		this.clienteFulano.setEmail(this.destinatario);
		this.clienteJuanPerez.setEmail(this.destinatarioSecundario);
		
		GrupoCC grupoDeCompras = new GrupoCC(clienteFulano, "La casita de beltran", "El equipo de desarrollo tiene hambre y se organiza!");
		grupoDeCompras.setVendedor(vendedor);

		PedidoColectivo pedidoColectivo = new PedidoColectivo();
		pedidoColectivo.setColectivo(grupoDeCompras);
		
		DateTime fechaVencimiento = new DateTime().plusHours(24);
		Pedido pedidoFulano = new Pedido(this.vendedor, this.clienteFulano, true, fechaVencimiento);
		Pedido pedidoPerez = new Pedido(this.vendedor, this.clienteJuanPerez, true, fechaVencimiento);
		
		ProductoPedido prodPedidoCincoUnidades = new ProductoPedido(variante, 5,"N/D");
		ProductoPedido prodPedidoVeintiCuatroUnidades = new ProductoPedido(variante, 24,"N/D");

		pedidoFulano.agregarProductoPedido(prodPedidoCincoUnidades, fechaVencimiento.plusHours(48));
		pedidoFulano.sumarAlMontoActual(prodPedidoCincoUnidades.getPrecio(), prodPedidoCincoUnidades.getCantidad());
		
		pedidoPerez.agregarProductoPedido(prodPedidoVeintiCuatroUnidades, fechaVencimiento.plusHours(48));
		pedidoPerez.sumarAlMontoActual(prodPedidoVeintiCuatroUnidades.getPrecio(), prodPedidoVeintiCuatroUnidades.getCantidad());
		
		pedidoColectivo.setDireccionEntrega(direccionCasa);
		
		pedidoColectivo.agregarPedidoIndividual(pedidoFulano);
		pedidoColectivo.agregarPedidoIndividual(pedidoPerez);
		
		mailService.enviarEmailPreparacionDePedidoColectivo(pedidoColectivo);
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeRecuperoDeContraseñaVendedor() throws Exception {
		//Se envia el email del template emailRecupero.ftl 
		
		mailService.enviarEmailRecuperoContraseña(vendedorDestinatario.getEmail(), "NombreDeUsuario");;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeRecuperoDeContraseñaCliente() throws Exception {
		//Se envia el email del template emailRecupero.ftl
		Vendedor vendedor = (Vendedor) usuarioService.obtenerUsuarioPorEmail(vendedorDestinatario.getEmail());
		vendedor.setEmail("null@gmail.com");//esto se hace porque sino hay duplicados.
		usuarioService.guardarUsuario(vendedor);
		
		this.clienteFulano.setEmail(this.destinatario);
		usuarioService.guardarUsuario(clienteFulano);
		mailService.enviarEmailRecuperoContraseñaCliente(this.destinatario);;
		assertEquals(true , true);
	}
	
	@Test
	public void testEnviarEmailDeVencimientoDePedido() throws IOException, MessagingException, TemplateException, EstadoPedidoIncorrectoException, VendedorInexistenteException {
		//Se envia el email del template emailVencimientoAutomatico.ftl
		//En Grupo e Individual.
		
		this.clienteFulano.setEmail(this.destinatario);
		DateTime fechaCreacionPedido = new DateTime();
		fechaCreacionPedido.minusHours(24);

		DateTime fechaVencimiento = new DateTime().plusHours(24);
		Pedido pedido= new Pedido(vendedor, clienteFulano, false, fechaVencimiento.plusHours(24));
		ProductoPedido prodPed = new ProductoPedido(variante, 5,"N/D");
		pedido.agregarProductoPedido(prodPed, fechaVencimiento.plusHours(48));
		pedido.sumarAlMontoActual(prodPed.getPrecio(), prodPed.getCantidad());
		pedido.setDireccionEntrega(direccionCasa);
		
		mailService.enviarEmailVencimientoPedido(vendedor.getUsername(), this.clienteFulano, pedido, this.dateTimeToString(fechaCreacionPedido), "15");;

		clienteFulano.setEmail(this.destinatario);
		clienteJuanPerez.setEmail(this.destinatario);
		vendedor.setEmail(this.destinatario);
		GrupoCC grupoDeCompras = new GrupoCC(clienteFulano, "La casita de beltran", "El equipo de desarrollo tiene hambre y se organiza!");
		grupoDeCompras.setVendedor(vendedor);

		PedidoColectivo pedidoColectivo = new PedidoColectivo();
		pedidoColectivo.setDireccionEntrega(direccionCasa);
		pedidoColectivo.setColectivo(grupoDeCompras);
		pedidoColectivo.agregarPedidoIndividual(pedido);
		
		pedido.setPerteneceAPedidoGrupal(true);
		pedido.setPedidoColectivo(pedidoColectivo);

		mailService.enviarEmailVencimientoPedido(vendedor.getUsername(), this.clienteFulano, pedido, this.dateTimeToString(fechaCreacionPedido), "15");;
		
		assertEquals(true , true);
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
	
	@Test
	public void testEnviarEmailDeInvitadoSinRegistrar() throws Exception {
		//Se envia el email del template emailInvitadoSinRegistrar.ftl
		
		Integer idGrupo = null;
		GrupoCC grupo = null;
		String emailCliente = "emailCliente@gmail.com";
		GrupoCC grupoDeCompras = new GrupoCC(clienteFulano, "La casita de beltran", "El equipo de desarrollo tiene hambre y se organiza!");
		grupoDeCompras.setVendedor(vendedor);
		grupoDeCompras.getCache().get(0).setEmail(emailCliente);;
		this.vendedor.setEmail(this.destinatario);
		this.vendedor.setUrl("urlVendedor");
		this.clienteFulano.setEmail(emailCliente);

		usuarioService.guardarUsuario(clienteFulano);
		usuarioService.guardarUsuario(vendedor);
		grupoService.guardarGrupo(grupoDeCompras);
		
		java.util.List<GrupoCC> grupos = grupoService.obtenerGruposDe(vendedor.getId());
		
		for (GrupoCC grupoCC : grupos) {
			if(grupoCC.getAlias().equals("La casita de beltran")){
				idGrupo = grupoCC.getId();
				grupo = grupoCC;
			}
		}
		
		grupo.invitarAlGrupo(this.destinatario);
		notificacionService.notificarInvitacionAGCCClienteNoRegistrado(clienteFulano, this.destinatario, grupo, null);
		
		//el email ya es enviado en el metodo "notificarInvitacionAGCCClienteNoRegistrado".
		mailService.enviarmailInvitadoSinRegistrar(this.clienteFulano, this.destinatario, this.vendedor.getUrl(), this.vendedor.getNombreCorto(), this.vendedor.getNombre(), idGrupo);;
		assertEquals(true , true);
	}
}