package chasqui.test.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Pedido;
import chasqui.model.Zona;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.composer.Constantes;

@ContextConfiguration(locations = {
"file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PedidoServiceTest extends GenericSetUp{

	
	@Autowired PedidoService pedidoService;
	@Autowired ZonaService zonaService;
	
	Zona zona;
	
	AgregarQuitarProductoAPedidoRequest agregarRequest;
	ConfirmarPedidoRequest confirmarRequest;
	DireccionRequest direccionRequest;
	@Before
	public void setUp() throws Exception{
		super.setUp();
		direccionRequest = new DireccionRequest();
		direccionRequest.setLatitud("-34.711850226378736");
		direccionRequest.setLongitud("-58.286359906196594");
		
		
		zona = new Zona();
		Polygon geoZona = (Polygon) new WKTReader().read("POLYGON ((-34.520142 -58.454132, -34.690609  -58.739777, -35.028240 -58.292084, -34.770641 -57.970734,-34.520142 -58.454132))");
		zona.setGeoArea(geoZona);
		zona.setIdVendedor(vendedor.getId());
		zonaService.guardar(zona);

		//pedidoService.guardar(pedido);
		
        //pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		
		agregarRequest = new AgregarQuitarProductoAPedidoRequest();
		agregarRequest.setCantidad(10);
		agregarRequest.setIdVariante(variante.getId());

		confirmarRequest = new ConfirmarPedidoRequest();
		confirmarRequest.setIdDireccion(direccionCasa.getId());

	}
	
	@After
	public void tearDown(){
		super.tearDown();
	}
	
	@Test
	public void testDebeDevolverLosPedidosEnDeterminadaZona() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido pedido= pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		pedido.setDireccionEntrega(new Direccion(direccionRequest));
		pedidoService.guardar(pedido);
		
		List<Pedido>ps = pedidoService.obtenerPedidosDeVendedorEnDeterminadaZona(vendedor.getId(), new DateTime().plusDays(-2).toDate(), new DateTime().plusDays(3).toDate(), Constantes.ESTADO_PEDIDO_ABIERTO, zona.getId());
		assertEquals(1,ps.size());
	}
	
	@Test
	public void testNoDebeDevolverPedidosEnDeterminadaZona() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido pedido= pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		DireccionRequest dr = new DireccionRequest();
		dr.setLatitud("15");
		dr.setLongitud("15");
		pedido.setDireccionEntrega(new Direccion(dr));
		pedidoService.guardar(pedido);
		List<Pedido>ps = pedidoService.obtenerPedidosDeVendedorEnDeterminadaZona(vendedor.getId(), new DateTime().plusDays(-2).toDate(), new DateTime().plusDays(3).toDate(), Constantes.ESTADO_PEDIDO_ABIERTO, zona.getId());
		assertEquals(ps.size(),0);
	}
	
	@Test
	public void testObtenerPedidosDeVendedor() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		List<Pedido>p = pedidoService.obtenerPedidosDeVendedor(vendedor.getId());
		assertTrue(p != null);
		assertEquals(1,p.size());
		assertEquals(clienteJuanPerez.getEmail(),p.get(0).getCliente().getEmail());
	}
	
	@Test
	public void totalPedidosVendedor() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		assertEquals(1,pedidoService.totalPedidosParaVendedor(vendedor.getId()));
	}
	
	@Test
	public void obtenerPedidosDeVendedorCon() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		List<Pedido>ps = pedidoService.obtenerPedidosDeVendedor(vendedor.getId(), new DateTime().plusDays(-2).toDate(), new DateTime().plusDays(3).toDate(), Constantes.ESTADO_PEDIDO_ABIERTO);
		assertTrue(ps != null);
		assertTrue(ps.size()>0);
		assertEquals(ps.get(0).getCliente().getEmail(),clienteJuanPerez.getEmail());
	}
	
	@Test
	public void noExistenPedidosDeVendedor(){
		List<Pedido>ps = pedidoService.obtenerPedidosDeVendedor(vendedor.getId(), new DateTime().plusDays(-2).toDate(), new DateTime().plusDays(3).toDate(), Constantes.ESTADO_PEDIDO_CANCELADO);
		assertEquals(ps.size(),0);
	}
	
	@Test
	public void unicoPedidoVigenteParaCliente() throws UsuarioInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		List<Pedido> pedidos = pedidoService.obtenerPedidosVigentesEnTodosLosCatalogosPara(clienteJuanPerez.getEmail());
	    assertEquals(1, pedidos.size());	
	}
	

	@Test(expected=VendedorInexistenteException.class)
	public void testCrearPedidoIndividualVendedorInexistente() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), 45);
	}

	
//	@Test(expected=ConfiguracionDeVendedorException.class)
//	public void testCrearPedidoIndividualVendedorSinFechaCierre() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
//		vendedor.setFechaCierrePedido(null);
//		usuarioService.guardarUsuario(vendedor);
//		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
//	}
	

	@Test(expected=ConfiguracionDeVendedorException.class)
	public void testCrearPedidoIndividualVendedorSinMontoMinimo() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		vendedor.setMontoMinimoPedido(null);
		usuarioService.guardarUsuario(vendedor);
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
	}
	

	@Test(expected=PedidoVigenteException.class)
	public void testCrearPedidoIndividualUsuarioYaContienePedido() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{

		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());

		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
	}

	@Test
	public void testAgregarProductoAPedido() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, ProductoInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		agregarRequest.setIdPedido(p.getId());
		pedidoService.agregarProductosAPedido(agregarRequest, clienteJuanPerez.getEmail());
	}
	
	@Test(expected=ProductoInexistenteException.class)
	public void testAgregarProductoAPedidoVarianteNoExiste() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, ProductoInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		agregarRequest.setIdPedido(p.getId());
		agregarRequest.setIdVariante(14);
		pedidoService.agregarProductosAPedido(agregarRequest, clienteJuanPerez.getEmail());
	}
	
	@Test(expected=RequestIncorrectoException.class)
	public void testAgregarProductoAPedidoUsuarioNoPoseePedido() throws ProductoInexistenteException, UsuarioInexistenteException, PedidoVigenteException, RequestIncorrectoException, EstadoPedidoIncorrectoException, VendedorInexistenteException{
		pedidoService.agregarProductosAPedido(agregarRequest, clienteJuanPerez.getEmail());
	}
	
	@Test(expected=ProductoInexistenteException.class)
	public void testAgregarProductoAPedidoVarianteSinStock() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, ProductoInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		try {
			Pedido p =pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
			agregarRequest.setIdPedido(p.getId());			
			agregarRequest.setCantidad(58);
			pedidoService.agregarProductosAPedido(agregarRequest, clienteJuanPerez.getEmail());
		} catch (PedidoInexistenteException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testEliminarPedido() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		pedidoService.vencerPedido(p);
		
		Cliente juanperez = (Cliente) usuarioService.obtenerUsuarioPorEmail(clienteJuanPerez.getEmail());
		usuarioService.inicializarPedidos(juanperez);
		
		assertFalse(juanperez.contienePedido(p.getId()));
		
		List<Pedido> pedidos = pedidoService.obtenerPedidosVencidos();
		assertEquals(1, pedidos.size());
	}
	
	@Test
	public void testCancelarPedido() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		pedidoService.cancelarPedidoPara(clienteJuanPerez.getEmail(), p.getId());
		
		Cliente juanperez = (Cliente) usuarioService.obtenerUsuarioPorEmail(clienteJuanPerez.getEmail());
		usuarioService.inicializarPedidos(juanperez);
		
		assertFalse(juanperez.contienePedido(p.getId()));
		ArrayList<String> estados = new ArrayList<String>();
		estados.add(Constantes.ESTADO_PEDIDO_CANCELADO);
		
		List<Pedido> pedidos = pedidoService.obtenerPedidosConEstados(clienteJuanPerez.getEmail(), vendedor.getId(), estados);
		assertEquals(1, pedidos.size());
	}
	
	@Test(expected=EstadoPedidoIncorrectoException.class)
	public void testEliminarPedidoDosVeces() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, RequestIncorrectoException, VendedorInexistenteException, EstadoPedidoIncorrectoException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		//pedidoService.vencerPedidoPara(clienteJuanPerez.getEmail(), p.getId());
		//pedidoService.vencerPedidoPara(clienteJuanPerez.getEmail(), p.getId());
		pedidoService.vencerPedido(p);
		pedidoService.vencerPedido(p);
	}
	
//	@Test(expected=PedidoVigenteException.class)
//	public void testEliminarPedidoNoExistente() throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, EstadoPedidoIncorrectoException{
//		pedidoService.vencerPedidoPara(clienteJuanPerez.getEmail(), vendedor.getId());
//		pedidoService.vencerPedido(p);
//	}
	
	@Test(expected=EstadoPedidoIncorrectoException.class)
	public void testVencerPedidoConfirmado() throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, EstadoPedidoIncorrectoException, ConfiguracionDeVendedorException, VendedorInexistenteException, PedidoInexistenteException, DomicilioInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		confirmarRequest.setIdPedido(p.getId());
		pedidoService.confirmarPedido(clienteJuanPerez.getEmail(), confirmarRequest);
		//pedidoService.vencerPedidoPara(clienteJuanPerez.getEmail(), vendedor.getId());
		pedidoService.vencerPedido(p);
	}
	
	
	
	
	@Test(expected=DomicilioInexistenteException.class)
	public void testConfirmarPedido() throws ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, PedidoInexistenteException, DireccionesInexistentes, RequestIncorrectoException, DomicilioInexistenteException, EstadoPedidoIncorrectoException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(clienteJuanPerez.getEmail());
		usuarioService.inicializarDirecciones(cliente);
		List<Direccion> dirs = cliente.getDireccionesAlternativas();
		for (Direccion direccion : dirs) {
			usuarioService.eliminarDireccionDe(clienteJuanPerez.getEmail(), direccion.getId());
		}
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(), vendedor.getId());
		confirmarRequest.setIdPedido(p.getId());
		confirmarRequest.setIdDireccion(1);
		pedidoService.confirmarPedido(clienteJuanPerez.getEmail(), confirmarRequest);
		}
	
	@Test
	public void testCrearPedidoIndividual() throws PedidoInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, VendedorInexistenteException{
		pedidoService.crearPedidoIndividualPara(clienteJuanPerez.getEmail(), vendedor.getId());
		Pedido p = pedidoService.obtenerPedidoActualDe(clienteJuanPerez.getEmail(),vendedor.getId());
		assertEquals(p.getIdVendedor(),vendedor.getId());
	}
}
