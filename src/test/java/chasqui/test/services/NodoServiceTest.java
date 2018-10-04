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

import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Nodo;
import chasqui.services.interfaces.NodoService;
import chasqui.view.composer.Constantes;

@ContextConfiguration(locations = { "file:src/test/java/dataSource-Test.xml",
		"file:src/main/resources/beans/service-beans.xml" })
@RunWith(SpringJUnit4ClassRunner.class)

public class NodoServiceTest extends GenericSetUp {

	@Autowired
	NodoService nodoService;

	private static final String NODO_ALIAS = "Alias";
	private static final String OTRO_NODO_ALIAS = "Otro Nodo";

	private static final String DESCRIPCION_NODO = "descripcion del nodo";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		nodo = new Nodo();
		nodo.setAdministrador(clienteJuanPerez);
		nodo.setAlias(NODO_ALIAS);
		nodo.setEstado(Constantes.ESTADO_NODO_APROBADO);
		nodo.setTipo(Constantes.NODO_ABIERTO);
		nodo.setVendedor(vendedor);
		nodoService.guardarNodo(nodo);
	}
	
	@Test
	public void testaltaNodoSinUsuario() throws NodoYaExistenteException, VendedorInexistenteException{
		nodoService.altaNodoSinUsuario("Nodo Sin Usuario", CLIENTE2_MAIL, DIRECCION2_LOCALIDAD, DIRECCION2_CALLE, DIRECCION_ALTURA, CLIENTE_TELEFONO_FIJO, vendedor.getId(), DESCRIPCION_NODO);
		Nodo nodo = nodoService.obtenerNodoPorAlias("Nodo Sin Usuario");
		assertEquals(nodo.getEmailAdministradorNodo(), CLIENTE2_MAIL);
		assertEquals(nodo.getAdministrador() , null);
	}
	
	@Test
	public void testObtenerNodoPorId() {
		Nodo nodoNuevo = nodoService.obtenerNodoPorId(nodo.getId());
		assertTrue(nodo != null);
		assertEquals(nodo.getAlias(), NODO_ALIAS);
	}

	@Test
	public void testObtenerNodosDelVendedor() throws VendedorInexistenteException {
		List<Nodo> nodos = nodoService.obtenerNodosDelVendedor(vendedor.getId());
		assertTrue(nodos != null);
		assertEquals(nodos.get(0).getAlias(), NODO_ALIAS);
	}

	@Test
	public void testEliminarNodo() throws VendedorInexistenteException {
		List<Nodo> nodos = nodoService.obtenerNodosDelVendedor(vendedor.getId());
		assertTrue(nodos != null);
		nodoService.eliminarNodo(nodos.get(0).getId());
		nodos = nodoService.obtenerNodosDelVendedor(vendedor.getId());
		assertEquals(0, nodos.size());
	}

	@Test
	public void testAltayAprobacionNodo() throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException {
		nodoService.altaNodo(OTRO_NODO_ALIAS, CLIENTE_MAIL, DIRECCION_LOCALIDAD, DIRECCION_CALLE, DIRECCION_ALTURA,
				CLIENTE_TELEFONO_FIJO, vendedor.getId(), DESCRIPCION_NODO);
		Nodo nodo = nodoService.obtenerNodoPorAlias(OTRO_NODO_ALIAS);
		assertEquals(Constantes.ESTADO_NODO_SOLICITADO, nodo.getEstado());
		nodoService.aprobarNodoPorAlias(OTRO_NODO_ALIAS);
		nodo = nodoService.obtenerNodoPorAlias(OTRO_NODO_ALIAS);
		assertEquals(Constantes.ESTADO_NODO_APROBADO, nodo.getEstado());
	}

	@Test
	public void testAltaNodoAbiertoPorDefault() throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException {
		nodoService.altaNodo(OTRO_NODO_ALIAS, CLIENTE_MAIL, DIRECCION_LOCALIDAD, DIRECCION_CALLE, DIRECCION_ALTURA,
				CLIENTE_TELEFONO_FIJO, vendedor.getId(), DESCRIPCION_NODO);
		Nodo nodo = nodoService.obtenerNodoPorAlias(OTRO_NODO_ALIAS);
		assertEquals(Constantes.NODO_ABIERTO, nodo.getTipo());
	}

	@Test
	public void testAprobarNodo() throws VendedorInexistenteException {
		List<Nodo> nodos = nodoService.obtenerNodosDelVendedor(vendedor.getId());
		assertTrue(nodos != null);
		nodoService.aprobarNodoPorId(nodos.get(0).getId());
		nodo = nodoService.obtenerNodosDelVendedor(vendedor.getId()).get(0);
		assertEquals(nodo.getEstado(), Constantes.ESTADO_NODO_APROBADO);
	}

	@After
	public void tearDown() {

		List<Nodo> nodos;
		try {
			nodos = nodoService.obtenerNodosDelVendedor(vendedor.getId());
			for (Nodo nodo : nodos) {
				nodoService.eliminarNodo(nodo.getId());
			}

		} catch (VendedorInexistenteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.tearDown();
	}

	@Test(expected = UsuarioInexistenteException.class)
	public void testAltaNodoClienteNoExiste() throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException {
		nodoService.altaNodo(OTRO_NODO_ALIAS, "", DIRECCION_LOCALIDAD, DIRECCION_CALLE, DIRECCION_ALTURA,
				CLIENTE_TELEFONO_FIJO, vendedor.getId(), DESCRIPCION_NODO);
	}
}
