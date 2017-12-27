package chasqui.test.services;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import chasqui.dao.GrupoDAO;
import chasqui.dao.UsuarioDAO;
import chasqui.model.Cliente;
import chasqui.model.GrupoCC;
import chasqui.model.Zona;
import chasqui.security.Encrypter;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.ZonaService;
import chasqui.test.builders.ClienteBuilder;
import chasqui.test.builders.DireccionBuilder;
import chasqui.test.builders.GCCBuilder;

@ContextConfiguration(locations = {
"file:src/test/java/dataSource-Test.xml",
"file:src/main/resources/beans/service-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GeoServiceTest  extends GenericSetUp{
	
	//TODO:Hacer test de casos no felices y exceptions.
	//TODO:Crear test para areas solapadas y no solapadas.
	
	@Autowired GeoService geoService;
	@Autowired ZonaService zonaService;
	@Autowired UsuarioService usuarioService;
	@Autowired UsuarioDAO usuarioDAO;
	@Autowired GrupoDAO grupoDAO;
	@Autowired Encrypter encrypter;
	private final GeometryFactory geometryFactory = new GeometryFactory();
	Cliente cliente;
	Cliente otroCliente;
	GrupoCC gcc;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		
	}
	
	
	@After
	public void tearDown(){
		super.tearDown();
		if(cliente != null){
			usuarioDAO.deleteObject(cliente);
			cliente = null;
		}
		if(otroCliente!=null){
			usuarioDAO.deleteObject(otroCliente);
			otroCliente = null;
		}
		if(gcc!=null){
			grupoDAO.eliminarGrupoCC(gcc);
			gcc = null;
		}
	}
	
	@Test
	public void testProbarGeoJsonParser(){	
		
		geoService.crearZonasDesdeGeoJson("./src/main/resources/testAssets/zonas_pds_test.geojson");
		List<Zona> zonas = zonaService.buscarZonasBy(vendedor.getId());
		assertEquals(zonas.size(), 2);
		
	}	
	
	@Test
	public void testNoHayClientesCercanos() throws Exception{
		
		cliente = ClienteBuilder
				  .unCliente(encrypter)
				  .conDireccion(DireccionBuilder
						  		.unaDireccion()
			   					.conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15,15)))
			   					.build()
			   				)
				  .build();
		usuarioDAO.guardarUsuario(cliente);
		
		List<Cliente> clientes = geoService.obtenerClientesCercanos("unemail@gmail.com");
		assertEquals(clientes.size(),0);
	}
	
	@Test
	public void testHayClientesCercanos() throws Exception{
		
		cliente = ClienteBuilder
				  .unCliente(encrypter)
		          .conDireccion(DireccionBuilder
							    .unaDireccion()
							    .conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15,15)))
							    .build()
							    )
				  .build();
		usuarioDAO.guardarUsuario(cliente);
		
		otroCliente = ClienteBuilder
					  .unCliente(encrypter)
					  .conEmail("otroemail@gmail.com")
					  .conDireccion(DireccionBuilder
							  		.unaDireccion()
							  		.conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15.001,15.001)))
							  		.build()
							  		)
					  .build();
		usuarioDAO.guardarUsuario(otroCliente);
		
		List<Cliente> clientes = geoService.obtenerClientesCercanos("unemail@gmail.com");
		assertEquals(clientes.size(),1);
	}
	
	@Test
	public void testEstanLejos() throws Exception{
		
		cliente = ClienteBuilder
				  .unCliente(encrypter)
		          .conDireccion(DireccionBuilder
		        		  		.unaDireccion()
							    .conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15,15)))
							    .build()
							   )
		          .build();
		usuarioDAO.guardarUsuario(cliente);
		
		otroCliente = ClienteBuilder.unCliente(encrypter)
					  .conEmail("otroemail@gmail.com")
					  .conDireccion(DireccionBuilder
							  		.unaDireccion()
							  		.conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15.012,15.012)))
							  		.build()
									)
					  .build();		
		usuarioDAO.guardarUsuario(otroCliente);
		
		List<Cliente> clientes = geoService.obtenerClientesCercanos("unemail@gmail.com");
		assertEquals(clientes.size(),0);
	}
	
	//@Test
	// Se comenta este test por cambio en el modelo de GrupoCC (ya no tiene domicilio, sino que se especifica para cada pedido)
	// Podría usarse la dirección del último pedido
	public void testHayGCC_Cercanos() throws Exception {
		
		cliente = ClienteBuilder
				  .unCliente(encrypter)
		          .conDireccion(DireccionBuilder
		        		  		.unaDireccion()
							    .conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15,15)))
							    .build()
							   )
		          .build();
		usuarioDAO.guardarUsuario(cliente);
		
		gcc = GCCBuilder
				.unGCC()
				.conDomicilioDeEntrega(DireccionBuilder
										.unaDireccion()
										.conGeoUbicacion(geometryFactory.createPoint(new Coordinate(15.001,15.001)))
										.build())
				.build();
		grupoDAO.guardarGrupo(gcc);
		
		List<GrupoCC> gcc= geoService.obtenerGCC_CercanosACliente("unemail@gmail.com");
		assertEquals(gcc.size(),1);
		
	}
}
