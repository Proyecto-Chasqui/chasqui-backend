package chasqui.test.services;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Direccion;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

@ContextConfiguration(locations = {
"file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PuntoDeRetiroServiceTest extends GenericSetUp {
	
	@Autowired PuntoDeRetiroService puntoDeRetiroService;
	@Autowired UsuarioService usuarioService;
	@Autowired VendedorService vendedorService;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
	}
		
	@After
	public void tearDown(){
		super.tearDown();
	}
	
	@Test
	public void testGuardarObtenerPuntoDeRetiro() throws VendedorInexistenteException{
		Direccion dir = setUpDirConParams(DIRECCION_ALIAS,DIRECCION_CALLE, DIRECCION_ALTURA, DIRECCION_CODIGO_POSTAL, DIRECCION_DEPARTAMENTO, DIRECCION_LOCALIDAD);
		PuntoDeRetiro pr = new PuntoDeRetiro(dir);	
		pr.setNombre("prtest");
		pr.setDescripcion("test");
		pr.setDisponible(true);
		pr.setIdExterno("id externo");
		Vendedor v = vendedorService.obtenerVendedor("MatLock");
		v.agregarPuntoDeRetiro(pr);
		usuarioService.guardarUsuario(v);
		PuntoDeRetiro prenbd = puntoDeRetiroService.obtenerPuntoDeRetiroConId(pr.getId());
		assertEquals(prenbd.getNombre(),"prtest");
	}
	
	@Test
	public void testEditarPuntoDeRetiro() throws VendedorInexistenteException{
		Direccion dir = setUpDirConParams(DIRECCION_ALIAS,DIRECCION_CALLE, DIRECCION_ALTURA, DIRECCION_CODIGO_POSTAL, DIRECCION_DEPARTAMENTO, DIRECCION_LOCALIDAD);
		PuntoDeRetiro pr = new PuntoDeRetiro(dir);	
		pr.setNombre("prtest");
		pr.setDescripcion("test");
		pr.setDisponible(true);
		pr.setIdExterno("id externo");
		Vendedor v = vendedorService.obtenerVendedor("MatLock");
		v.agregarPuntoDeRetiro(pr);
		usuarioService.guardarUsuario(v);
		PuntoDeRetiro prenbd = puntoDeRetiroService.obtenerPuntoDeRetiroConId(pr.getId());
		prenbd.setNombre("prTestEdit");
		prenbd.setDescripcion("testEdit");
		prenbd.setIdExterno("IdExternoEdit");
		prenbd.setDisponible(false);
		puntoDeRetiroService.guardarPuntoDeRetiro(prenbd);
		PuntoDeRetiro prenbdeeditado = puntoDeRetiroService.obtenerPuntoDeRetiroConId(prenbd.getId());		
		assertEquals(prenbdeeditado.getNombre(),"prTestEdit");
		assertEquals(prenbdeeditado.getDescripcion(),"testEdit");
		assertEquals(prenbdeeditado.getIdExterno(),"IdExternoEdit");
		assertEquals(prenbdeeditado.getDisponible(),false);
	}
	
	
	@Test
	public void testEditarDireccion() throws VendedorInexistenteException{
		Direccion dir = setUpDirConParams(DIRECCION_ALIAS,DIRECCION_CALLE, DIRECCION_ALTURA, DIRECCION_CODIGO_POSTAL, DIRECCION_DEPARTAMENTO, DIRECCION_LOCALIDAD);
		PuntoDeRetiro pr = new PuntoDeRetiro(dir);	
		pr.setNombre("prtest");
		pr.setDescripcion("test");
		pr.setDisponible(true);
		pr.setIdExterno("id externo");
		Vendedor v = vendedorService.obtenerVendedor("MatLock");
		v.agregarPuntoDeRetiro(pr);
		usuarioService.guardarUsuario(v);
		PuntoDeRetiro prenbd = puntoDeRetiroService.obtenerPuntoDeRetiroConId(pr.getId());
		prenbd.setAltura(DIRECCION2_ALTURA);
		prenbd.setCalle(DIRECCION2_CALLE);
		prenbd.setCodigoPostal(DIRECCION2_CODIGO_POSTAL);
		prenbd.setDepartamento(DIRECCION2_DEPARTAMENTO);
		prenbd.setLocalidad(DIRECCION2_LOCALIDAD);
		puntoDeRetiroService.guardarPuntoDeRetiro(prenbd);
		PuntoDeRetiro prenbddiredit = puntoDeRetiroService.obtenerPuntoDeRetiroConId(prenbd.getId());
		assertEquals(prenbddiredit.getAltura(),DIRECCION2_ALTURA);
		assertEquals(prenbddiredit.getCalle(),DIRECCION2_CALLE);
		assertEquals(prenbddiredit.getCodigoPostal(),DIRECCION2_CODIGO_POSTAL);
		assertEquals(prenbddiredit.getDepartamento(),DIRECCION2_DEPARTAMENTO);
		assertEquals(prenbddiredit.getLocalidad(), DIRECCION2_LOCALIDAD);
	}
	
}
