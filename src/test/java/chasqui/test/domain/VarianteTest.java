package chasqui.test.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import chasqui.model.Variante;

public class VarianteTest {
	
	Variante v;
	Variante varianteSinStock;
	
	@Before
	public void setUp(){
	
		v = new Variante();
		v.setStock(4);
		v.setCantidadReservada(0);
		
		varianteSinStock = new Variante();
		varianteSinStock.setStock(4);
		varianteSinStock.setCantidadReservada(3);
	}
	
	
	@Test
	public void testVarianteTieneStock(){
		assertTrue(v.tieneStockParaReservar(2));
	}
	
	@Test
	public void testVarianteReservar(){
		v.reservarCantidad(2);
		assertEquals(v.getCantidadReservada(),new Integer(2));
	}
	
	@Test
	public void testVarianteEliminarReserva(){
		v.reservarCantidad(2);
		v.eliminarReserva(1);
		assertEquals(v.getCantidadReservada(),new Integer(1));
	}
	
	@Test
	public void testVarianteNoTieneStock(){
		assertFalse(varianteSinStock.tieneStockParaReservar(2));
	}

}
