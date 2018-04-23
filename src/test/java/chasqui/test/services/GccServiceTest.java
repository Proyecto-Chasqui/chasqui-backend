package chasqui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.constraints.AssertFalse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.NoAlcanzaMontoMinimoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoSinProductosException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Nodo;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.services.impl.PedidoServiceImpl;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

@ContextConfiguration(locations = { "file:src/test/java/dataSource-Test.xml",
		"file:src/main/resources/beans/service-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class GccServiceTest extends GenericSetUp {
	@Autowired
	GrupoService grupoService;
	@Autowired
	InvitacionService invitacionService;
	@Autowired
	NotificacionService notificacionService;
	@Autowired
	PedidoService pedidoService;

	String alias = "Alias";
	String descripcion = "Este es un grupo de compras colectivas";
	Integer idGrupo;

	@Before
	public void setUp() throws Exception {

		super.setUp();

		grupoService.altaGrupo(vendedor.getId(), alias, descripcion, clienteJuanPerez.getEmail());
		List<GrupoCC> grupos = grupoService.obtenerGruposDeCliente(clienteJuanPerez.getEmail(), vendedor.getId());
		idGrupo = grupos.get(0).getId();

	}

	@After
	public void tearDown() {
		try {
			grupoService.eliminarGrupoCC(idGrupo);

			this.limpiarNotificacionesPara(clienteJuanPerez.getEmail());
			this.limpiarNotificacionesPara(clienteFulano.getEmail());
			
		} catch (GrupoCCInexistenteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.tearDown();
	}

	@Test
	public void testObtenerGrupoCreado() throws VendedorInexistenteException {
		List<GrupoCC> grupos = grupoService.obtenerGruposDe(vendedor.getId());
		assertTrue(grupos != null);
		assertEquals(1, grupos.size());
		assertEquals(this.alias, grupos.get(0).getAlias());
	}

	@Test
	public void chequearEstructuraGrupoRecienCreado() throws GrupoCCInexistenteException {
		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);
		assertEquals(Constantes.ESTADO_PEDIDO_ABIERTO, grupo.getPedidoActual().getEstado());
		assertEquals(new Double(0), grupo.getPedidoActual().getMontoTotal());
		assertEquals(1, grupo.getCache().size());

	}

	@Test
	public void administradorEsMiembroConInvitacionAceptadaYPedidoInexistente() throws GrupoCCInexistenteException {
		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);
		assertEquals(1, grupo.getCache().size());
		MiembroDeGCC miembroUnico = grupo.getCache().get(0);
		assertEquals(clienteJuanPerez.getEmail(), miembroUnico.getEmail());
		assertEquals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA, miembroUnico.getEstadoInvitacion());
		assertEquals(Constantes.ESTADO_PEDIDO_INEXISTENTE, miembroUnico.getEstadoPedido());
		assertEquals(clienteJuanPerez.getUsername(), miembroUnico.getNickname());
	}

	private void limpiarNotificacionesPara(String email) {
		List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesPendientesPara(email);
		notificaciones.addAll(notificacionService.obtenerNotificacionesAceptadasPara(email));
		notificaciones.addAll(notificacionService.obtenerNotificacionesRechazadasPara(email));
		for (Notificacion notificacion : notificaciones) {
			notificacionService.eliminarNotificacion(notificacion);
		}
	}

	@Test
	public void invitarUsuarioNuevo()
			throws Exception {

		String mailUsuarioNoRegistrado = "mengano@gmail.com";
		grupoService.invitarAGrupo(idGrupo, mailUsuarioNoRegistrado, clienteJuanPerez.getEmail());

		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);

		assertEquals(2, grupo.getCache().size());
		MiembroDeGCC miembroInvitado = grupo.getCache().get(1);
		assertEquals(mailUsuarioNoRegistrado, miembroInvitado.getEmail());

		assertEquals(Constantes.ESTADO_NOTIFICACION_NO_LEIDA, miembroInvitado.getEstadoInvitacion());
		assertEquals(Constantes.ESTADO_PEDIDO_INEXISTENTE, miembroInvitado.getEstadoPedido());

	}

	@Test
	public void aceptarInvitacionUsuarioNuevo() throws Exception {

		// Invitacion a usuario que no está en el sistema
		String mailUsuarioNoRegistrado = "mengano@gmail.com";
		grupoService.invitarAGrupo(idGrupo, mailUsuarioNoRegistrado, clienteJuanPerez.getEmail());

		// El nuevo usuario se registra
		Cliente mengano = setupClienteConParams(mailUsuarioNoRegistrado, "Mengano",
				setUpDirConParams("direccion de mi amigo", "calle 10", 2659, "1897", "", "localidad"));
		usuarioService.guardarUsuario(mengano);

		// El nuevo usuario confirma la invitacion
		InvitacionAGCC i = invitacionService.obtenerInvitacionAGCCporIDGrupo(mailUsuarioNoRegistrado, idGrupo);
		grupoService.confirmarInvitacionGCC(i.getId(), mailUsuarioNoRegistrado);

		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);

		assertEquals(2, grupo.getCache().size());
		MiembroDeGCC miembroInvitado = grupo.getCache().get(1);
		assertEquals(mailUsuarioNoRegistrado, miembroInvitado.getEmail());

		assertEquals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA, miembroInvitado.getEstadoInvitacion());
		assertEquals(Constantes.ESTADO_PEDIDO_INEXISTENTE, miembroInvitado.getEstadoPedido());

	}

	@Test
	public void crearPedidoIndividual()
			throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, ConfiguracionDeVendedorException,
			PedidoVigenteException, PedidoInexistenteException, VendedorInexistenteException, GrupoCCInexistenteException {
		Cliente juan = (Cliente) usuarioService.obtenerUsuarioPorEmail(clienteJuanPerez.getEmail());
		usuarioService.inicializarDirecciones(juan);

		List<Direccion> dirs = clienteJuanPerez.getDireccionesAlternativas();

		grupoService.nuevoPedidoIndividualPara(idGrupo, clienteJuanPerez.getEmail(), vendedor.getId());

		Pedido pedido = grupoService.obtenerPedidoIndividualEnGrupo(idGrupo, clienteJuanPerez.getEmail());

		assertTrue(pedido != null);

		// -----------------------------------Verificar notificaciones
		//-----------------------------------El administrador no tiene notificaciones
		List<Notificacion> notificacionAdministrador = notificacionService
				.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(0, notificacionAdministrador.size());
		
		assertTrue(pedido.getPedidoColectivo()!=null);
	}

	@Test
	public void confirmarPedidoColectivoConDomicilio() throws Exception {

		// Invitacion fulano <-- juan perez
		
		this.invitarYAceptar(idGrupo,clienteJuanPerez.getEmail(), clienteFulano.getEmail());

		// ---------------------------------- Nuevo pedido para juan en el grupo
		this.pedirYConfirmarEnGrupoPara(idGrupo,clienteJuanPerez.getEmail(),vendedor.getId());
		
		
		List<Notificacion> notificacionesJuanPerez = notificacionService
				.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(2, notificacionesJuanPerez.size());

		List<Notificacion> notificacionesFulano = notificacionService
				.obtenerNotificacionesPendientesPara(clienteFulano.getEmail());
		assertEquals(1, notificacionesFulano.size());	
		
		// --------------- Nuevo pedido para FULANO en el grupo

		
		grupoService.nuevoPedidoIndividualPara(idGrupo, clienteFulano.getEmail(), vendedor.getId());

		notificacionesJuanPerez = notificacionService.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(2, notificacionesJuanPerez.size());
		
		Pedido pedido2 = grupoService.obtenerPedidoIndividualEnGrupo(idGrupo, clienteFulano.getEmail());

		AgregarQuitarProductoAPedidoRequest reqProd2 = new AgregarQuitarProductoAPedidoRequest();
		reqProd2.setCantidad(15);
		reqProd2.setIdPedido(pedido2.getId());
		reqProd2.setIdVariante(variante.getId());
		pedidoService.agregarProductosAPedido(reqProd2, clienteFulano.getEmail());

		ConfirmarPedidoSinDireccionRequest reqConfirmarFulano = new ConfirmarPedidoSinDireccionRequest();
		reqConfirmarFulano.setIdPedido(pedido2.getId());


		grupoService.confirmarPedidoIndividualEnGCC(clienteFulano.getEmail(), reqConfirmarFulano);
		

		List<Notificacion> notificacionesfulano = notificacionService.obtenerNotificacionesPendientesPara(clienteFulano.getEmail());
		assertEquals(2, notificacionesfulano.size());

		
		// ---------------------------------- CONFIRMAR PEDIDO COLECTIVO
		grupoService.confirmarPedidoColectivo(idGrupo, clienteJuanPerez.getEmail(),direccionCasa.getId(),null,"",null);

		// -----------------------------------Verificar notificaciones

		notificacionesfulano = notificacionService.obtenerNotificacionesPendientesPara(clienteFulano.getEmail());
		assertEquals(3, notificacionesfulano.size());

		notificacionesJuanPerez = notificacionService.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(4, notificacionesJuanPerez.size());
	}

	private void pedirYConfirmarEnGrupoPara(Integer idG, String email, Integer idVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, ConfiguracionDeVendedorException, PedidoVigenteException, PedidoInexistenteException, VendedorInexistenteException, GrupoCCInexistenteException, ProductoInexistenteException, RequestIncorrectoException, EstadoPedidoIncorrectoException, PedidoSinProductosException {
		grupoService.nuevoPedidoIndividualPara(idG, email,idVendedor);
		Pedido pedido = grupoService.obtenerPedidoIndividualEnGrupo(idG, email);

		AgregarQuitarProductoAPedidoRequest reqProd1 = new AgregarQuitarProductoAPedidoRequest();
		reqProd1.setCantidad(10);
		reqProd1.setIdPedido(pedido.getId());
		reqProd1.setIdVariante(variante.getId());
		pedidoService.agregarProductosAPedido(reqProd1, email);

		ConfirmarPedidoSinDireccionRequest reqConfirmar = new ConfirmarPedidoSinDireccionRequest();
		reqConfirmar.setIdPedido(pedido.getId());

		grupoService.confirmarPedidoIndividualEnGCC(email, reqConfirmar);
	}

	private void invitarYAceptar(Integer idG, String remitenteMail, String destinatarioMail) throws Exception {
		grupoService.invitarAGrupo(idG, destinatarioMail, remitenteMail);
		Integer idInvitacion = invitacionService.obtenerInvitacionAGCCporIDGrupo(destinatarioMail, idGrupo)
				.getId();
		grupoService.confirmarInvitacionGCC(idInvitacion, destinatarioMail);

	}

	@Test(expected = DomicilioInexistenteException.class)
	public void confirmarPedidoIndividualEnGCCSinDomicilio() throws GrupoCCInexistenteException,
			UsuarioInexistenteException, ClienteNoPerteneceAGCCException, ConfiguracionDeVendedorException,
			PedidoVigenteException, PedidoInexistenteException, RequestIncorrectoException,
			DomicilioInexistenteException, EstadoPedidoIncorrectoException, VendedorInexistenteException {

		grupoService.nuevoPedidoIndividualPara(idGrupo, clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = grupoService.obtenerPedidoIndividualEnGrupo(idGrupo, clienteJuanPerez.getEmail());

		AgregarQuitarProductoAPedidoRequest request = new AgregarQuitarProductoAPedidoRequest();
		request.setCantidad(10);
		request.setIdPedido(p.getId());
		request.setIdVariante(vendedor.getProductos().get(0).getVariantes().get(0).getId());
		pedidoService.agregarProductosAPedido(request, clienteJuanPerez.getEmail());

		ConfirmarPedidoRequest req = new ConfirmarPedidoRequest();
		req.setIdPedido(p.getId());
		req.setIdDireccion(70);// ID inexistente

		pedidoService.confirmarPedido(clienteJuanPerez.getEmail(), req);
	}

	@Test
	public void listarGruposSinDomicilio() throws Exception {

		String nuevoMail = "mengano@chasuqi.com";
		Cliente clienteMenganoSinDomicilio = setupClienteConParams(nuevoMail, "mengano", null);
		usuarioService.guardarUsuario(clienteMenganoSinDomicilio);

		grupoService.altaGrupo(vendedor.getId(), alias, descripcion, nuevoMail);

		List<GrupoCC> grupos = grupoService.obtenerGruposDeCliente(nuevoMail, vendedor.getId());

		assertEquals(1, grupos.size());

		Integer idGrupoMengano = grupos.get(0).getId();
		//assertEquals(null, grupos.get(0).getDomicilioEntrega()); //TODO reemplazar por un assert apropiado. Se eliminó la relación del grupo con Domicilio de entrega 2017.09.21
		

		grupoService.eliminarGrupoCC(idGrupoMengano);
	}

	@Test
	public void invitarUsuarioExistenteEnChasqui()
			throws Exception {

		grupoService.invitarAGrupo(idGrupo, clienteFulano.getEmail(), clienteJuanPerez.getEmail());

		GrupoCC grupo = grupoService.obtenerGrupo(idGrupo);

		assertEquals(2, grupo.getCache().size());
		MiembroDeGCC miembroInvitado = grupo.getCache().get(1);
		assertEquals(clienteFulano.getEmail(), miembroInvitado.getEmail());

		assertEquals(Constantes.ESTADO_NOTIFICACION_NO_LEIDA, miembroInvitado.getEstadoInvitacion());
		assertEquals(Constantes.ESTADO_PEDIDO_INEXISTENTE, miembroInvitado.getEstadoPedido());

	}
	
	@Test
	public void confirmarPedidoColectivoConDomicilioConCuestionario() throws Exception {

		// Invitacion fulano <-- juan perez
		
		this.invitarYAceptar(idGrupo,clienteJuanPerez.getEmail(), clienteFulano.getEmail());

		// ---------------------------------- Nuevo pedido para juan en el grupo
		this.pedirYConfirmarEnGrupoPara(idGrupo,clienteJuanPerez.getEmail(),vendedor.getId());
		
		
		List<Notificacion> notificacionesJuanPerez = notificacionService
				.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(2, notificacionesJuanPerez.size());

		List<Notificacion> notificacionesFulano = notificacionService
				.obtenerNotificacionesPendientesPara(clienteFulano.getEmail());
		assertEquals(1, notificacionesFulano.size());	
		
		// --------------- Nuevo pedido para FULANO en el grupo

		
		grupoService.nuevoPedidoIndividualPara(idGrupo, clienteFulano.getEmail(), vendedor.getId());

		notificacionesJuanPerez = notificacionService.obtenerNotificacionesPendientesPara(clienteJuanPerez.getEmail());
		assertEquals(2, notificacionesJuanPerez.size());
		
		Pedido pedido2 = grupoService.obtenerPedidoIndividualEnGrupo(idGrupo, clienteFulano.getEmail());

		AgregarQuitarProductoAPedidoRequest reqProd2 = new AgregarQuitarProductoAPedidoRequest();
		reqProd2.setCantidad(15);
		reqProd2.setIdPedido(pedido2.getId());
		reqProd2.setIdVariante(variante.getId());
		pedidoService.agregarProductosAPedido(reqProd2, clienteFulano.getEmail());

		ConfirmarPedidoSinDireccionRequest reqConfirmarFulano = new ConfirmarPedidoSinDireccionRequest();
		reqConfirmarFulano.setIdPedido(pedido2.getId());


		grupoService.confirmarPedidoIndividualEnGCC(clienteFulano.getEmail(), reqConfirmarFulano);
		

		List<Notificacion> notificacionesfulano = notificacionService.obtenerNotificacionesPendientesPara(clienteFulano.getEmail());
		assertEquals(2, notificacionesfulano.size());

		
		// ---------------------------------- CONFIRMAR PEDIDO COLECTIVO
		List<OpcionSeleccionadaRequest> opcionesSeleccionadas = new ArrayList<OpcionSeleccionadaRequest>();
		OpcionSeleccionadaRequest opr = new OpcionSeleccionadaRequest();
		opr.setNombre("Cargo");
		opr.setOpcionSeleccionada("Docente");
		opcionesSeleccionadas.add(opr);
		grupoService.confirmarPedidoColectivo(idGrupo, clienteJuanPerez.getEmail(),direccionCasa.getId(),null,"",opcionesSeleccionadas);

		// -----------------------------------Verificar notificaciones

		GrupoCC g= grupoService.obtenerGrupo(idGrupo);
		
		PedidoColectivo p = g.getHistorial().getPedidosGrupales().get(0);
		assertTrue(p.getRespuestasAPreguntas().size()==1);
	}
}
