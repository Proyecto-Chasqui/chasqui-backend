package chasqui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;

@ContextConfiguration(locations = {
"file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)

public class CaracteristicaServiceTest extends GenericSetUp{

	
	@Before
	public void setUp() throws Exception{
		super.setUp();
	}
	
	@After
	public void tearDown(){
		super.tearDown();
	}
	
	
	@Test
	public void testBuscarCaracteristicasProducto(){
		List<Caracteristica> cs = caracteristicaService.buscarCaracteristicasProducto();
		assertTrue(cs != null );
		assertEquals("caracteristica",cs.get(0).getNombre());
	}
	
	@Test
	public void testBuscarCaracteristicasProductor(){
		List<CaracteristicaProductor> cs = caracteristicaService.buscarCaracteristicasProductor();
		assertTrue(cs != null );
		assertEquals("caracteristicaProductor",cs.get(0).getNombre());
	}
	
	@Test
	public void testActualizarCaracteristica(){
		caracteristica.setNombre("CAMBIO");
		caracteristicaService.actualizarCaracteristica(caracteristica);
		Caracteristica nueva = caracteristicaService.buscarCaracteristicasProducto().get(0);
		assertEquals("CAMBIO",nueva.getNombre());
	}
	
	@Test
	public void testActualizarCaracteristicaProductor(){
		caracteristicaProductor.setNombre("PRODUCTOR");
		caracteristicaService.actualizarCaracteristicaProductor(caracteristicaProductor);
		CaracteristicaProductor nueva = caracteristicaService.buscarCaracteristicasProductor().get(0);
		assertEquals( "PRODUCTOR",nueva.getNombre());
	}
	
}
