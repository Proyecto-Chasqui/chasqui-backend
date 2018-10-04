package chasqui.test.services;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Categoria;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Nodo;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.UsuarioService;


public class GenericSetUp {

	private static final String IMAGEN_PATH = "hola";
	private static final int VARIANTE_CANT_RESERVADA = 0;
	private static final int VARIANTE_STOCK = 40;
	private static final double VARIANTE_PRECIO = 10.10;
	protected static final String VARIANTE_DESCRIPCION = "descripcion";
	protected static final String VARIANTE_NOMBRE = "Frutilla";
	protected static final String PRODUCTOR_NOMBRE = "Nombre del productor";
	protected static final String CLIENTE_TELEFONO_CELULAR = "1559205493";
	protected static final String CLIENTE_TELEFONO_FIJO = "4224224224";
	protected static final String CLIENTE_PASSWORD = "12345678";
	protected static final String CLIENTE_NICK_NAME = "Jp";
	protected static final String CLIENTE_MAIL = "jperez@gmail.com";
	protected static final String CLIENTE_APELLIDO = "Perez";
	protected static final String CLIENTE2_NICK_NAME = "Fulanito";
	protected static final String CLIENTE2_MAIL = "fulano@gmail.com";
	
	Vendedor vendedor;
	Cliente clienteJuanPerez;
	Cliente clienteFulano;
	Direccion direccionCasa,direccionFulano;
	List<Direccion> dds;
	Caracteristica caracteristica;
	CaracteristicaProductor caracteristicaProductor;
	List<Caracteristica> listaCaracteristicas;
	List<CaracteristicaProductor> listaCaracteristicasProductor;
	Nodo nodo;
	Nodo nodo2;
	Fabricante productor;
	Categoria categ;
	Variante variante;
	Producto producto;
	List<Variante> variantes;

	List<Producto> productos;
	List<Fabricante> productores;
	
	@Autowired Encrypter encrypter;
	@Autowired UsuarioService usuarioService;
	@Autowired CaracteristicaService caracteristicaService;
	@Autowired CaracteristicaService NodoService;

	protected static String DIRECCION_ALIAS = "Alias";
	protected static String DIRECCION_CALLE = "Calle";
	protected static String DIRECCION_LOCALIDAD = "Localidad";
	protected static Integer DIRECCION_ALTURA = 1;
	protected static Integer DIRECCION_LONGITUD = 1;
	protected static Integer DIRECCION_LATITUD = 1;
	protected static String DIRECCION_CODIGO_POSTAL = "1";
	protected static String DIRECCION_DEPARTAMENTO = "8b";

	protected static String DIRECCION2_ALIAS = "Alias";
	protected static String DIRECCION2_CALLE = "Calle";
	protected static String DIRECCION2_LOCALIDAD = "Localidad";
	protected static Integer DIRECCION2_ALTURA = 1;
	protected static Integer DIRECCION2_LONGITUD = 1;
	protected static Integer DIRECCION2_LATITUD = 1;
	protected static String DIRECCION2_CODIGO_POSTAL = "1";
	protected static String DIRECCION2_DEPARTAMENTO = "8b";
	
	protected static String CLIENTE_NOMBRE = "Juan";
	protected static String CATEGORIA_NOMBRE = "Nombre categoria";
	
	public void setUp() throws Exception{

		
		setupCaracteristicas();
		caracteristicaService.guardaCaracteristicasProducto(listaCaracteristicas);
		caracteristicaService.guardarCaracteristicaProductor(listaCaracteristicasProductor);
		
		setupVendedor();
		usuarioService.guardarUsuario(vendedor);	
	
		

		direccionCasa = setUpDirConParams(DIRECCION_ALIAS,DIRECCION_CALLE, DIRECCION_ALTURA, DIRECCION_CODIGO_POSTAL, DIRECCION_DEPARTAMENTO, DIRECCION_LOCALIDAD);
		clienteJuanPerez = setupClienteConParams(CLIENTE_MAIL, CLIENTE_NICK_NAME, direccionCasa);	
		usuarioService.guardarUsuario(clienteJuanPerez);
		

		direccionFulano = setUpDirConParams(DIRECCION2_ALIAS,DIRECCION2_CALLE, DIRECCION2_ALTURA, DIRECCION2_CODIGO_POSTAL, DIRECCION2_DEPARTAMENTO, DIRECCION2_LOCALIDAD);
		clienteFulano = setupClienteConParams(CLIENTE2_MAIL, CLIENTE2_NICK_NAME, direccionFulano);		
		usuarioService.guardarUsuario(clienteFulano);
		
		setupProductosYProductores();

		cargarProductosAlvendedor();
		
		usuarioService.guardarUsuario(vendedor);		
	}
	
	
	public void tearDown(){
		usuarioService.deleteObject(vendedor);
		usuarioService.deleteObject(clienteJuanPerez);
		usuarioService.deleteObject(clienteFulano);
		caracteristicaService.eliminarCaracteristica(caracteristica);
		caracteristicaService.eliminarCaracteristicaProductor(caracteristicaProductor);
	}
	
	

	protected Direccion setUpDirConParams(String alias, String calle, Integer altura, String cp, String depto, String localidad){

		Direccion direccion = new Direccion();
		direccion.setAlias(alias);
		direccion.setCalle(calle);
		direccion.setAltura(altura);
		direccion.setCodigoPostal(cp);
		direccion.setDepartamento(depto);
		direccion.setLocalidad(localidad);
		direccion.setPredeterminada(true);
		return direccion;
	}
	
	private void cargarProductosAlvendedor() {
		List<Categoria> cs = new ArrayList<Categoria>();
		categ = new Categoria(vendedor,CATEGORIA_NOMBRE);
		categ.setProductos(productos);
		cs.add(categ);

		
		vendedor.setFabricantes(productores);
		vendedor.setCategorias(cs);
		productor.setIdVendedor(vendedor.getId());
	}

	private void setupProductosYProductores() {

		productores = new ArrayList<Fabricante>();
		productor = new Fabricante();
		productor.setNombre(PRODUCTOR_NOMBRE);
		productor.setCaracteristica(caracteristicaProductor);
		productores.add(productor);

		//PRODUCTO
		producto = new Producto();
		producto.setNombre("Mermelada");
		producto.setCategoria(categ);
		producto.setFabricante(productor);
		producto.setCaracteristicas(listaCaracteristicas);
		//VARIANTE
		variante = new Variante();
		variante.setNombre(VARIANTE_NOMBRE);
		variante.setDescripcion(VARIANTE_DESCRIPCION);
		variante.setDestacado(false);
		variante.setPrecio(VARIANTE_PRECIO);
		variante.setStock(VARIANTE_STOCK);
		variante.setCantidadReservada(VARIANTE_CANT_RESERVADA);
		//variante.setPrecio(13.0);

		
		List<Imagen>imgs = new ArrayList<Imagen>();
		Imagen imagen = new Imagen();
		imagen.setPath(IMAGEN_PATH);
		imgs.add(imagen);
		variante.setImagenes(imgs);
		
		variante.setProducto(producto);
		variantes = new ArrayList<Variante>();
		variantes.add(variante);
		
		producto.setVariantes(variantes);
		producto.setFabricante(productor);
		
		productos = new ArrayList<Producto>();
		productos.add(producto);
		
		
	}

	private void setupCaracteristicas() {

		listaCaracteristicasProductor = new ArrayList<CaracteristicaProductor>();
		listaCaracteristicas = new ArrayList<Caracteristica>();
		
		caracteristicaProductor = new CaracteristicaProductor();
		caracteristicaProductor.setNombre("caracteristicaProductor");
		caracteristicaProductor.setEliminada(false);
		listaCaracteristicasProductor.add(caracteristicaProductor);
		
		caracteristica = new Caracteristica();
		caracteristica.setNombre("caracteristica");
		caracteristica.setEliminada(false);
		listaCaracteristicas.add(caracteristica);
		
		
	}

	private void setupVendedor() throws Exception {
		DateTime cierre = new DateTime().plusDays(1);
		vendedor = new Vendedor();
		vendedor.setUsername("MatLock");
		vendedor.setNombre("MatLock - Nombre");
		vendedor.setPassword(encrypter.encrypt("federico"));
		vendedor.setEmail("floresfederico_993@hotmail.com");
		vendedor.setIsRoot(false);
		vendedor.setMontoMinimoPedido(213);
		vendedor.setUrl("vendedor.proyectochasqui.com");
		List<String> opciones = new ArrayList<String>();
		opciones.add("si");
		opciones.add("no");
		vendedor.setPreguntasDePedidosIndividuales(generarPreguntas(opciones, "Tiene Factura"));
		vendedor.setPreguntasDePedidosColectivos(generarPreguntas(opciones, "Tiene Factura"));
		//vendedor.setFechaCierrePedido(cierre);
		
		
	}
	
	private List<PreguntaDeConsumo> generarPreguntas(List<String> opciones, String nombre){
		List<PreguntaDeConsumo> lista = new ArrayList<PreguntaDeConsumo>();
		lista.add(new PreguntaDeConsumo("Tiene Factura",true,opciones));
		return lista;
	}


	protected Cliente setupClienteConParams(String mail, String nick, Direccion dir) throws Exception{

		SingUpRequest request= new SingUpRequest();
		
		request.setApellido(CLIENTE_APELLIDO);
		request.setNombre(CLIENTE_NOMBRE);
		request.setEmail(mail);
		request.setNickName(nick);
		request.setPassword(CLIENTE_PASSWORD);
		request.setTelefonoFijo(CLIENTE_TELEFONO_FIJO);
		request.setTelefonoMovil(CLIENTE_TELEFONO_CELULAR);
		
		dds = new ArrayList<Direccion>();
		dds.add(dir);
		Cliente nuevo = new Cliente(request, "token");
		nuevo.setDireccionesAlternativas(dds);
		return nuevo;
	}
//
//	private void setupCliente() throws Exception{
//
//		SingUpRequest request= new SingUpRequest();
//		
//		request.setApellido(CLIENTE_APELLIDO);
//		request.setNombre(CLIENTE_NOMBRE);
//		request.setEmail(CLIENTE_MAIL);
//		request.setNickName(CLIENTE_NICK_NAME);
//		request.setPassword(CLIENTE_PASSWORD);
//		request.setTelefonoFijo(CLIENTE_TELEFONO_FIJO);
//		request.setTelefonoMovil(CLIENTE_TELEFONO_CELULAR);
//		direccionCasa = new Direccion();
//		direccionCasa.setAlias(DIRECCION_ALIAS);
//		direccionCasa.setCalle(DIRECCION_CALLE);
//		direccionCasa.setAltura(DIRECCION_ALTURA);
//		direccionCasa.setCodigoPostal(DIRECCION_CODIGO_POSTAL);
//		direccionCasa.setDepartamento(DIRECCION_DEPARTAMENTO);
//		direccionCasa.setPredeterminada(true);
//		
//		dds = new ArrayList<Direccion>();
//		dds.add(direccionCasa);
//		clienteJuanPerez = new Cliente(request, "token");
//		clienteJuanPerez.setDireccionesAlternativas(dds);
//	}
}
