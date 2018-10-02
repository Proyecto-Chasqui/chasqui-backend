package chasqui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import chasqui.exceptions.CaracteristicaInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.model.Caracteristica;
import chasqui.model.Variante;
import chasqui.service.rest.request.ByCategoriaRequest;
import chasqui.service.rest.request.ByMedallaRequest;
import chasqui.service.rest.request.ByProductorRequest;
import chasqui.service.rest.request.MedallaRequest;
import chasqui.services.interfaces.ProductoService;

@ContextConfiguration(locations = {
"file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductoServiceTest extends GenericSetUp {

	
	@Autowired ProductoService productoService;
	
//	Producto p;
//	Variante v;
	ByCategoriaRequest byCategoriaRequest;
	ByProductorRequest byProductorRequest;
	ByMedallaRequest byMedallaRequest;
	MedallaRequest MedallaRequest;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
//		p = new Producto();
//		p.setFabricante(productor);
//		p.setNombre("mermelada");
//		p.setCategoria(categ);
//		List<Caracteristica> cs = new ArrayList<Caracteristica>();
//		cs.add(caracteristica);
//		p.setCaracteristicas(cs);
//		v = new Variante();
//		v.setNombre("frutilla");
//		v.setDescripcion("descripcion");
//		v.setDestacado(false);
//		v.setStock(4);
//		v.setPrecio(40.0);
//		v.setProducto(p);
//		v.setCantidadReservada(0);
//		List<Variante>vs = new ArrayList<Variante>();
//		vs.add(v);
//		p.setVariantes(vs);
//		List<Producto>ps = new ArrayList<Producto>();
//		ps.add(p);
//		categ.setProductos(ps);
//		productor.setProductos(ps);
		
		byCategoriaRequest = new ByCategoriaRequest();
		byCategoriaRequest.setCantItems(1);
		byCategoriaRequest.setIdCategoria(categ.getId());
		byCategoriaRequest.setPrecio("Down");
		byCategoriaRequest.setPagina(1);
		
		byProductorRequest = new ByProductorRequest();
		byProductorRequest.setCantItems(1);
		byProductorRequest.setIdProductor(productor.getId());
		byProductorRequest.setPrecio("Down");
		byProductorRequest.setPagina(1);
		
		byMedallaRequest = new ByMedallaRequest();
		byMedallaRequest.setCantItems(1);
		byMedallaRequest.setIdMedalla(caracteristica.getId());
		byMedallaRequest.setPrecio("Down");
		byMedallaRequest.setPagina(1);
		byMedallaRequest.setIdVendedor(vendedor.getId());
		
		MedallaRequest = new MedallaRequest();
		MedallaRequest.setMedallaId(caracteristica.getId());
		
		//Mara
		variante.setStock(4);
		variante.setPrecio(40.0);
		usuarioService.guardarUsuario(vendedor);		
	}
	
	@After
	public void tearDown(){
		super.tearDown();
	}
	
	
	//busqueda de variantes por categoria
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorCategoriaOrdenamientoInvalido() throws RequestIncorrectoException{
		byCategoriaRequest.setPrecio(null);
		productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
	}
	
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorCategoriaPaginaNull() throws RequestIncorrectoException{
		byCategoriaRequest.setPagina(null);
		productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorCategoriaCantItemsNull() throws RequestIncorrectoException{
		byCategoriaRequest.setCantItems(null);
		productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorIdCategoriaNull() throws RequestIncorrectoException{
		byCategoriaRequest.setIdCategoria(null);
		productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
	}
	
	@Test()
	public void testObtenerVariantesPorCategoria() throws RequestIncorrectoException{
		List<Variante>vs =productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
		assertTrue(vs != null);
		assertEquals(vs.get(0).getNombre(),VARIANTE_NOMBRE);
	}
	
	
	@Test()
	public void testObtenerVariantesPorCategoriaSinStock() throws RequestIncorrectoException{
		variante.setCantidadReservada(4);
		usuarioService.guardarUsuario(vendedor);
		List<Variante>vs =productoService.obtenerVariantesPorCategoria(byCategoriaRequest);
		assertTrue(vs != null);
		assertEquals(vs.size(),0);
	}
	
	// busqueda de variantes por productor
	
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorMedallaOrdenamientoInvalido() throws RequestIncorrectoException{
		byProductorRequest.setPrecio(null);
		productoService.obtenerVariantesPorProductor(byProductorRequest);
	}
	
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorProductorPaginaNull() throws RequestIncorrectoException{
		byProductorRequest.setPagina(null);
		productoService.obtenerVariantesPorProductor(byProductorRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorProductorCantItemsNull() throws RequestIncorrectoException{
		byProductorRequest.setCantItems(null);
		productoService.obtenerVariantesPorProductor(byProductorRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorIdProductorNull() throws RequestIncorrectoException{
		byProductorRequest.setIdProductor(null);
		productoService.obtenerVariantesPorProductor(byProductorRequest);
	}
	
	@Test()
	public void testObtenerVariantesPorProductor() throws RequestIncorrectoException{
		List<Variante>vs =productoService.obtenerVariantesPorProductor(byProductorRequest);
		assertTrue(vs != null);
		assertEquals(vs.get(0).getNombre(),VARIANTE_NOMBRE);
	}
	
	@Test()
	public void testObtenerVariantesPorProductorSinStock() throws RequestIncorrectoException{
		variante.setCantidadReservada(4);
		usuarioService.guardarUsuario(vendedor);
		List<Variante>vs =productoService.obtenerVariantesPorProductor(byProductorRequest);
		assertTrue(vs != null);
		assertEquals(vs.size(),0);
	}
	
	// OBTENER VARIANTES POR MEDALLAS
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorProductorOrdenamientoInvalido() throws RequestIncorrectoException{
		byMedallaRequest.setPrecio(null);
		productoService.obtenerVariantesPorMedalla(byMedallaRequest);
	}
	
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorMedallaPaginaNull() throws RequestIncorrectoException{
		byMedallaRequest.setPagina(null);
		productoService.obtenerVariantesPorMedalla(byMedallaRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorMedallaCantItemsNull() throws RequestIncorrectoException{
		byMedallaRequest.setCantItems(null);
		productoService.obtenerVariantesPorMedalla(byMedallaRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorIdMedallaNull() throws RequestIncorrectoException{
		byMedallaRequest.setIdMedalla(null);
		productoService.obtenerVariantesPorMedalla(byMedallaRequest);
	}
	
	@Test(expected = RequestIncorrectoException.class)
	public void testObtenerVariantesPorMedallaVendedorNull() throws RequestIncorrectoException{
		byMedallaRequest.setIdVendedor(null);
		productoService.obtenerVariantesPorMedalla(byMedallaRequest);
	}
	
	@Test()
	public void testObtenerVariantesPorMedalla() throws RequestIncorrectoException{
		List<Variante>vs =productoService.obtenerVariantesPorMedalla(byMedallaRequest);
		assertTrue(vs != null);
		assertEquals(vs.get(0).getNombre(),VARIANTE_NOMBRE);
	}
	
	@Test
	public void testObtenerVariantesPorMedallaSinStock() throws RequestIncorrectoException{
		variante.setCantidadReservada(4);
		usuarioService.guardarUsuario(vendedor);
		List<Variante>vs =productoService.obtenerVariantesPorMedalla(byMedallaRequest);
		assertTrue(vs != null);
		assertEquals(vs.size(),0);
	}
	
	
	@Test
	public void testObtenerVariantesPorMedallaDeProductor() throws RequestIncorrectoException{
		List<Variante>vs =productoService.obtenerProductosConMedallaEnProductor(caracteristicaProductor.getId());
		assertTrue(vs != null);
		assertEquals(vs.size(),1);
		assertEquals(variante.getId(),vs.get(0).getId());
		assertEquals(variante.getNombre(),vs.get(0).getNombre());
		assertEquals(variante.getDescripcion(),vs.get(0).getDescripcion());
		assertEquals(variante.getDestacado(),vs.get(0).getDestacado());
	}
	
	@Test
	public void testObtenerVariantePorId(){
		Variante v2 = productoService.obtenerVariantePor(variante.getId());
		assertEquals(v2.getId(),variante.getId());
		assertEquals(v2.getNombre(),variante.getNombre());
	}
	
	@Test
	public void testObtenerMedallaPorId(){
		Caracteristica c2 = productoService.obtenerMedalla(caracteristica.getId());
		assertEquals(c2.getId(),caracteristica.getId());
		assertEquals(c2.getNombre(),caracteristica.getNombre());
	}
	
	@Test(expected=CaracteristicaInexistenteException.class)
	public void testObtenerMedallaPorIdInvalida(){
		productoService.obtenerMedalla(10);
	}
	
	
}
