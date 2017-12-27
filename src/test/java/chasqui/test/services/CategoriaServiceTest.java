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

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Categoria;
import chasqui.services.interfaces.CategoriaService;

@ContextConfiguration(locations = { "file:src/test/java/dataSource-Test.xml",
		"file:src/main/resources/beans/service-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoriaServiceTest extends GenericSetUp {

	@Autowired
	CategoriaService categoriaService;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void testObtenerCategoriasDe() throws VendedorInexistenteException {
		List<Categoria> css = categoriaService.obtenerCategoriasDe(vendedor.getId());
		assertTrue(css != null);
		assertEquals(css.get(0).getNombre(), CATEGORIA_NOMBRE);
	}

	@Test(expected = VendedorInexistenteException.class)
	public void testObtenerCategoriasDeVendedorInexistente() throws VendedorInexistenteException {
		categoriaService.obtenerCategoriasDe(5);
	}

}
