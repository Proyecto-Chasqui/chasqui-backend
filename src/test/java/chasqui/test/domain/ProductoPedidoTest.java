package chasqui.test.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import chasqui.model.ProductoPedido;

public class ProductoPedidoTest {

	
	ProductoPedido pp;
	
	@Before
	public void setUp(){
		pp = new ProductoPedido();
		pp.setCantidad(40);
	}
	
	@Test
	public void restarCantidad(){
		assertEquals(pp.getCantidad(),new Integer(40));
		pp.restar(20);
		assertEquals(pp.getCantidad(),new Integer(20));		
	}
	
	@Test
	public void sumarCantidad(){
		assertEquals(pp.getCantidad(),new Integer(40));
		pp.sumarCantidad(20);
		assertEquals(pp.getCantidad(),new Integer(60));		
	}
	
}
