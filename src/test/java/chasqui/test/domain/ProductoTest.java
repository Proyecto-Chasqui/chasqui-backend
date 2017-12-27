package chasqui.test.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import chasqui.model.Producto;
import chasqui.model.Variante;

public class ProductoTest {
	
	Producto p;
	Variante v;
	Producto pSinDestacada;
	Variante noDestacada;
	
	@Before
	public void setUp(){
		
		p = new Producto();
		List<Variante>vs = new ArrayList<Variante>();
		p.setVariantes(vs);
		v = new Variante();
		v.setDestacado(true);
		vs.add(v);
		
		
		
		pSinDestacada = new Producto();
		noDestacada = new Variante();
		List<Variante>vss = new ArrayList<Variante>();
		vss.add(noDestacada);
		noDestacada.setDestacado(false);
		pSinDestacada.setVariantes(vss);
	}
	
	
	@Test
	public void testTieneVarianteDestacada(){
		assertTrue(p.tieneVarianteDestacada());
	}
	
	@Test
	public void testNoTieneVarianteDestacada(){
		assertFalse(pSinDestacada.tieneVarianteDestacada());
	}

}
