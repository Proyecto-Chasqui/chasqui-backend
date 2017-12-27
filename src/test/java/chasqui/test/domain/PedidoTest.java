package chasqui.test.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.model.Categoria;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Fabricante;
import chasqui.model.Pedido;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.test.builders.DireccionBuilder;
import chasqui.view.composer.Constantes;

public class PedidoTest {

	
	Pedido pedido;
	Pedido pedidoInvalido;
	Pedido pedidoInvalidoFecha;
	Pedido pedidoAbierto;
	ProductoPedido pp;
	Variante variedad;
	Categoria categoria;
	Vendedor vendedor ;
	Cliente cliente;
	private Fabricante productor;
	
	@Before
	public void setUp() {
		vendedor = new Vendedor("Vendedor","vendedoruserrname","vendedor@mail.com","contraseña","demo.chasqui.com");
		vendedor.setMontoMinimoPedido(1);
		vendedor.setId(1);

		categoria = new Categoria(vendedor, "nombreCategoria");
		productor = new Fabricante("Productor");
		cliente = new Cliente();
		List<Direccion> direcciones = new ArrayList<Direccion>();
		direcciones.add(DireccionBuilder.unaDireccion().build());
		cliente.setDireccionesAlternativas(direcciones);
		cliente.setPedidos(new ArrayList<Pedido>());
		pedido = new Pedido();
		pedidoInvalido = new Pedido();
		pedidoAbierto = new Pedido(vendedor,cliente,false, new DateTime().plusMinutes(10));
		cliente.agregarPedido(pedidoAbierto);
		pedidoInvalidoFecha = new Pedido();
		Set<ProductoPedido>ps = new HashSet<ProductoPedido>();
		
		pedido.setAlterable(true);
		pedido.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
		pedido.setFechaDeVencimiento(new DateTime().plusMonths(-2));
		
		pedidoInvalido.setAlterable(true);
		pedidoInvalido.setEstado(Constantes.ESTADO_PEDIDO_CANCELADO);
		pedidoInvalido.setFechaDeVencimiento(new DateTime().plusMonths(-2));
		
		pedidoInvalidoFecha.setAlterable(true);
		pedidoInvalidoFecha.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
		pedidoInvalidoFecha.setFechaDeVencimiento(new DateTime().plusMonths(-2));
		pedidoAbierto.setProductosEnPedido(ps);
		
		pedidoAbierto.setAlterable(true);
		pedidoAbierto.setId(1);
		pedidoAbierto.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
		pedidoAbierto.setFechaDeVencimiento(new DateTime().plusMonths(2));
		pedidoAbierto.setMontoActual(0.0);
		
		pp = new ProductoPedido();
		pp.setId(1);
		pp.setIdVariante(4);
		pp.setImagen("a");
		pp.setNombreProducto("nombre");
		pp.setNombreVariante("variante");
		pp.setPrecio(10.0);
		pp.setCantidad(1);
	}

	@Test
	public void testConfirmate() throws EstadoPedidoIncorrectoException {
		pedido.entregarte();
		assertEquals(Constantes.ESTADO_PEDIDO_ENTREGADO,pedido.getEstado());
		assertFalse(pedido.getAlterable());
	}
	
	@Test
	public void testConfirmatePedidoAbierto() throws EstadoPedidoIncorrectoException, PedidoInexistenteException {
		variedad = new Variante();
		variedad.setPrecio(5.0);
		variedad.setId(1);
		variedad.setProducto(new Producto("nombre", categoria,productor));
		
		cliente.agregarProductoAPedido(variedad, 1, 3, new DateTime().plusMinutes(10));
		Pedido pedidoActualDe = cliente.obtenerPedidoActualDe(1);
		pedidoActualDe.confirmarte();
		assertEquals(Constantes.ESTADO_PEDIDO_CONFIRMADO,pedidoAbierto.getEstado());
		assertFalse(pedidoAbierto.getAlterable());
	}
	
	@Test(expected = EstadoPedidoIncorrectoException.class)
	public void testConfirmatePedidoCancelado() throws EstadoPedidoIncorrectoException {
		pedidoInvalido.entregarte();
		assertEquals(Constantes.ESTADO_PEDIDO_CANCELADO,pedidoInvalido.getEstado());
		assertTrue(pedido.getAlterable());
	}
	
	
	@Test
	public void testVigente() {
		assertFalse(pedido.estaVigente());
	}
	
	@Test
	public void testVigenteAbierto() {
		assertTrue(pedidoAbierto.estaVigente());
	}
	
	@Test
	public void testVigenteCancelado() {
		assertFalse(pedidoInvalido.estaVigente());
	}
	
//	@Test Comentado por huenu el 2017.09.14
//	public void testVigenteInvalidoFecha() {
//		assertFalse(pedidoInvalidoFecha.estaVigente());
//	}
	
	@Test
	public void testPedidoAbiertoAgregarProducto() throws EstadoPedidoIncorrectoException{
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),0);
		pedidoAbierto.agregarProductoPedido(pp, new DateTime());
		assertEquals(pp.getCantidad(), new Integer(1));
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),1);
	}
	
	@Test
	public void testPedidoAbiertoAgregarProductoYaExistente() throws EstadoPedidoIncorrectoException{
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),0);
		pedidoAbierto.agregarProductoPedido(pp, new DateTime());
		pedidoAbierto.agregarProductoPedido(pp, new DateTime());
		assertEquals(pp.getCantidad(), new Integer(2));
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),1);
	}
	
	@Test
	public void testPedidoAbiertoEliminarProducto() throws EstadoPedidoIncorrectoException{
		pedidoAbierto.agregarProductoPedido(pp, new DateTime());
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),1);
		pedidoAbierto.eliminar(pp);
		assertEquals(pedidoAbierto.getProductosEnPedido().size(),0);
	}
	
	@Test
	public void testPedidoAbiertoSumarAlMontoActual(){
		pedidoAbierto.sumarAlMontoActual(10.0, 2);
		assertEquals(pedidoAbierto.getMontoActual(),new Double(20.0));
	}
	
	@Test
	public void testPedidoAbiertoRestarAlMontoActual(){
		pedidoAbierto.sumarAlMontoActual(10.0, 2);
		pedidoAbierto.restarAlMontoActual(10.0, 1);
		assertEquals(pedidoAbierto.getMontoActual(),new Double(10.0));
	}

}
