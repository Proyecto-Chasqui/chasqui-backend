package chasqui.test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Pedido;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.view.composer.Constantes;



public class ClienteTest {
	
	Cliente cliente;
	Cliente clienteSinDireccionPredeterminada;
	Direccion nuevaDireccion;
	Direccion direccionPredeterminada;
	DireccionRequest dirRequest;
	Pedido pedido;
	Pedido pedidoNoVigente;
	Pedido pedidoAgregar;
	Variante variante;
	ConfirmarPedidoRequest confirmarRequest;
	
	
	@Before
	public void setUp(){
		cliente = new Cliente();
		cliente.setNombre("nombre");
		cliente.setApellido("apellido");
		cliente.setUsername("nickname");
		cliente.setEmail("email");
		cliente.setTelefonoFijo("43434343");
		cliente.setTelefonoMovil("1414141414141");
		cliente.setEmail(Constantes.MAIL_SIN_CONFIRMAR);
		
		clienteSinDireccionPredeterminada = new Cliente();
		clienteSinDireccionPredeterminada.setNombre("nombre");
		clienteSinDireccionPredeterminada.setApellido("apellido");
		clienteSinDireccionPredeterminada.setUsername("nickname");
		clienteSinDireccionPredeterminada.setEmail("email");
		clienteSinDireccionPredeterminada.setTelefonoFijo("43434343");
		clienteSinDireccionPredeterminada.setTelefonoMovil("1414141414141");
		clienteSinDireccionPredeterminada.setDireccionesAlternativas(new ArrayList<Direccion>());
		
		
		dirRequest = new DireccionRequest();
		dirRequest.setAlias("aliasreq");
		dirRequest.setAltura(1231);
		dirRequest.setCalle("callereq");
		dirRequest.setPredeterminada(true);
		dirRequest.setDepartamento("4d");
		dirRequest.setLocalidad("avellanedareq");
		dirRequest.setLatitud("15");
		dirRequest.setLongitud("15");
		
		List<Direccion> direccionesAlternativas = new ArrayList<Direccion>();
		direccionPredeterminada = new Direccion();
		direccionPredeterminada.setAlias("alias");
		direccionPredeterminada.setId(1);
		direccionPredeterminada.setAltura(123);
		direccionPredeterminada.setCalle("calle");
		direccionPredeterminada.setCodigoPostal("1231");
		direccionPredeterminada.setDepartamento("44s");
		direccionPredeterminada.setPredeterminada(true);
		direccionesAlternativas.add(direccionPredeterminada);
		
		nuevaDireccion = new Direccion();
		nuevaDireccion.setAlias("alias");
		nuevaDireccion.setAltura(123);
		nuevaDireccion.setCalle("calle");
		nuevaDireccion.setCodigoPostal("1231");
		nuevaDireccion.setDepartamento("44s");
		nuevaDireccion.setPredeterminada(false);
		cliente.setDireccionesAlternativas(direccionesAlternativas);		
		List<Pedido>pss = new ArrayList<Pedido>();
		pedido = new Pedido();
		pedido.setMontoActual(0.0);
		pedido.setProductosEnPedido(new HashSet<ProductoPedido>());
		pedido.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
		pedido.setPerteneceAPedidoGrupal(false);
		pedido.setMontoMinimo(0.0);
		pedido.setFechaCreacion(new DateTime());
		pedido.setIdVendedor(1);
		pedido.setId(1);
		pedido.setFechaDeVencimiento(new DateTime().plusMonths(2));
		
		pedidoNoVigente = new Pedido();
		pedidoNoVigente.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
		pedidoNoVigente.setFechaCreacion(new DateTime());
		pedidoNoVigente.setPerteneceAPedidoGrupal(false);
		pedidoNoVigente.setIdVendedor(2);
		pedidoNoVigente.setFechaDeVencimiento(new DateTime().plusMonths(-12));
		pedidoNoVigente.setId(2);
		pss.add(pedido);
		pss.add(pedidoNoVigente);
		cliente.setPedidos(pss);
		pedidoAgregar = new Pedido();
		
		variante = new Variante();
		variante.setId(1);
		variante.setNombre("frutilla");
		variante.setPrecio(4.33);
		variante.setStock(10);
		Producto p = new Producto();
		p.setNombre("mermelada");
		p.setFabricante(new Fabricante("test"));
		variante.setProducto(p);
		List<Imagen>img = new ArrayList<Imagen>();
		Imagen i = new Imagen();
		i.setPath("path");
		img.add(i);
		variante.setImagenes(img);
		confirmarRequest = new ConfirmarPedidoRequest();
		confirmarRequest.setIdPedido(1);
		confirmarRequest.setIdDireccion(1);
		
	}

	@Test
	public void testObtenerDireccionDetenerminada() {
		assertEquals(cliente.obtenerDireccionPredeterminada(),direccionPredeterminada);
	}
	
	@Test
	public void testClienteSinDireccionPredeterminada(){
		assertNull(clienteSinDireccionPredeterminada.obtenerDireccionPredeterminada());
	}
	
	@Test
	public void testagregarDireccion(){
		cliente.agregarDireccion(dirRequest);
		assertEquals(cliente.getDireccionesAlternativas().size(), 2);
		assertEquals(cliente.obtenerDireccionPredeterminada().getAlias(), "aliasreq");
	}
	
	@Test
	public void testEditarDireccion() throws DireccionesInexistentes{
		cliente.editarDireccionCon(dirRequest, 1);
		assertEquals(cliente.obtenerDireccionPredeterminada().getAlias(),"aliasreq");
		assertEquals(cliente.getDireccionesAlternativas().size(),1);
	}
	
	@Test(expected=DireccionesInexistentes.class)
	public void testEditarDireccionInexistente() throws DireccionesInexistentes{
		cliente.editarDireccionCon(dirRequest, 4);
	}
	
	@Test
	public void testEliminarDireccion() throws DireccionesInexistentes{
		cliente.eliminarDireccion(1);
		assertEquals(cliente.getDireccionesAlternativas().size(),0);
	}
	
	@Test(expected=DireccionesInexistentes.class)
	public void testEliminarDireccionInexistente() throws DireccionesInexistentes{
		cliente.eliminarDireccion(4);
		assertEquals(cliente.getDireccionesAlternativas().size(),0);
	}
	
	@Test
	public void testObtenerPedido() throws PedidoInexistenteException{
		assertEquals(cliente.obtenerPedidoActualDe(1),pedido);
	}
	
	@Test(expected=PedidoInexistenteException.class)
	public void testObtenerPedidoVendedorInexistente() throws PedidoInexistenteException{
		cliente.obtenerPedidoActualDe(4);
	}
	
// Este test no tiene sentido hasta no revisar el historial	
//	@Test(expected=PedidoInexistenteException.class)
//	public void testObtenerPedidoVendedorNoVigente() throws PedidoInexistenteException{
//		cliente.obtenerPedidoActualDe(2);
//	} N
	
	@Test
	public void testAgregarPedido(){
		cliente.agregarPedido(pedidoAgregar);
		assertEquals(cliente.getPedidos().size(), 3);
	}
	
	@Test
	public void testContienePedidoVigente(){
		assertTrue(cliente.contienePedido(1));
	}
	
//	@Test
//	public void testNoContienePedidoVigente(){
//		assertFalse(cliente.contienePedido(2));
//	}
	
	@Test
	public void testVarianteCorrespondeConPedido(){
		assertTrue(cliente.tienePedidoDeVendedor(1, 1));
	}
	
	@Test
	public void testVarianteNoCorrespondeConIdPedido(){
		assertFalse(cliente.tienePedidoDeVendedor(1, 2));
	}
	
	@Test
	public void testAgregarProductoAPedido() throws PedidoInexistenteException, EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 1, new DateTime().plusMinutes(10));
		assertEquals(cliente.obtenerPedidoActualDe(1).getProductosEnPedido().size(),1);
		assertEquals(cliente.obtenerPedidoActualDe(1).getMontoActual(),new Double(4.33));
	}
	
	@Test
	public void testNoContieneProductoEnPedido(){
		assertFalse(cliente.contieneProductoEnPedido(variante, 1));
	}
	
	@Test
	public void testContieneProductoEnPedido() throws EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 1, new DateTime());
		assertTrue(cliente.contieneProductoEnPedido(variante, 1));
	}
	
	
	@Test
	public void testContieneCantidadDeProductoEnPedido() throws EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 1, new DateTime());
		assertTrue(cliente.contieneCantidadDeProductoEnPedido(variante,1, 1));
	}
	
	@Test
	public void testNoContieneCantidadDeProductoEnPedido() throws EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 1, new DateTime());
		assertFalse(cliente.contieneCantidadDeProductoEnPedido(variante,1, 4));
	}
	
	@Test
	public void testEliminarProductoEnPedido() throws PedidoInexistenteException, EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 1, new DateTime().plusMinutes(10));
		cliente.eliminarProductoEnPedido(variante.getId(), variante.getPrecio(), 1, 1);
		assertEquals(cliente.obtenerPedidoActualDe(1).getProductosEnPedido().size(),0);
	}
	
	@Test
	public void testEliminarCantidadDeProductoEnPedido() throws PedidoInexistenteException, EstadoPedidoIncorrectoException{
		cliente.agregarProductoAPedido(variante, 1, 2, new DateTime().plusMinutes(10));
		cliente.eliminarProductoEnPedido(variante.getId(), variante.getPrecio(), 1, 1);
		assertEquals(cliente.obtenerPedidoActualDe(1).getProductosEnPedido().size(),1);
	}
	
	@Test
	public void testEliminarPedido() throws EstadoPedidoIncorrectoException{
		cliente.vencerPedido(1);
		assertFalse(cliente.contienePedido(1));
	}
	
	@Test
	public void testConfirmarPedido() throws EstadoPedidoIncorrectoException{
		cliente.confirmarPedido(confirmarRequest.getIdPedido(),confirmarRequest.getIdDireccion(), confirmarRequest.getIdPuntoDeRetiro());
		assertEquals(2,cliente.getPedidos().size()); 
		// 2017-06-30 Los pedidos confirmados no se remueven de la colección
		//assertTrue(cliente.getHistorialPedidos() != null);
		//assertEquals(cliente.getHistorialPedidos().getPedidos().size(),1);
	}
	
	@Test
	public void testContieneDireccionConId(){
		assertTrue(cliente.contieneDireccion(1));
	}
	
	@Test 
	public void testPedidos(){
		assertEquals(2,cliente.getPedidos().size());
		cliente.agregarPedido(null);
		assertEquals(2,cliente.getPedidos().size());
	}
	
	@Test
	public void clienteMailConfirmado(){
		cliente.confirmarMail();
		assertEquals(cliente.getEstado(),Constantes.MAIL_CONFIRMADO);
	}

	@Test
	public void clienteSinMailConfirmar(){
		assertEquals(cliente.getEstado(), Constantes.MAIL_SIN_CONFIRMAR);
	}
}
