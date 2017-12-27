package chasqui.test.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import chasqui.model.Direccion;
import chasqui.service.rest.request.DireccionRequest;

public class DireccionTest {
	
	Direccion d;
	DireccionRequest request;
	
	
	@Before
	public void setUp(){
		d = new Direccion();
		d.setPredeterminada(true);
		request = new DireccionRequest();
		request.setAlias("alias");
		request.setAltura(1111);
		request.setCodigoPostal("12312");
		request.setCalle("calle");
	}
	
	@Test
	public void testEditarDireccion(){
		assertTrue(d.getAlias() == null);
		d.modificarCon(request);
		assertEquals(d.getAlias(), "alias");
		assertEquals(d.getAltura(),new Integer(1111));
		assertTrue(d.getPredeterminada());
		
	}
	
}
