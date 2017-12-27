package chasqui.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletContext;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Sessions;

import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Categoria;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.service.rest.response.FabricanteResponse;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.UsuarioService;

public class StartupHelper {

	@Autowired
	private String usuarioDemo;
	@Autowired
	private String passwordDemo;
	@Autowired
	private String mailDemo;
	@Autowired
	private String imagenDemo;
	@Autowired
	private Integer montoMinimoDemo;
	@Autowired
	private String urlDemo;
	@Autowired
	private String serverRelativePath;
	@Autowired
	private Encrypter encrypter;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CaracteristicaService caracteristicaService;

	private ArrayList<CaracteristicaProductor> listaSellosProductor;
	private ArrayList<Caracteristica> listaSellos;
	
	public void loadDemo() throws Exception {
		if (!mailDemo.equals("")) {
			//Si se deja el mail en blanco no se genera el usuario demo
			Vendedor demo;
			try {
				demo = (Vendedor) usuarioService.obtenerUsuarioPorEmail(mailDemo);
			} catch (UsuarioInexistenteException e) {
				demo = new Vendedor();
				this.cargarCategorias(demo);
				//this.cargarSellos();
				//this.cargarProductores(demo);
			}

			demo.setUsername(usuarioDemo);
			demo.setNombre(usuarioDemo);
			demo.setPassword(encrypter.encrypt(passwordDemo));
			demo.setEmail(mailDemo);
			demo.setIsRoot(false);
			demo.setMontoMinimoPedido(montoMinimoDemo);
			demo.setUrl(urlDemo);

			Imagen img = new Imagen();
			img.setNombre("perfil.jpg");
			img.setPath(imagenDemo);
			demo.setImagenPerfil(img.getPath());



			usuarioService.guardarUsuario(demo);			
		}

	}

	// TODO importar categorías del properties
	private void cargarCategorias(Vendedor demo) {

		demo.agregarCategoria(new Categoria(demo, "panificados"));
		demo.agregarCategoria(new Categoria(demo, "bebidas"));
		demo.agregarCategoria(new Categoria(demo, "limpieza"));

	}


	private void cargarSellos() {

		listaSellosProductor = new ArrayList<CaracteristicaProductor>();
		listaSellos = new ArrayList<Caracteristica>();

		listaSellosProductor.add(new CaracteristicaProductor("Cooperativa", "/imagenes/usuarios/ROOT/cooperativa.png", "Una cooperativa es una forma de organización..."));
		listaSellosProductor.add(new CaracteristicaProductor("Recuperada", "/imagenes/usuarios/ROOT/recuperada.png", "Una fábrica recuperada es una empresa recuperada por sus trabajadores"));

		listaSellos.add(new Caracteristica("Orgánico", "/imagenes/usuarios/ROOT/organico.png","Los productos orgánicos..."));
		listaSellos.add(new Caracteristica("Agroecológico", "/imagenes/usuarios/ROOT/agroecologia.png","Los productos agroecológicos..."));
		listaSellos.add(new Caracteristica("Reciclado", "/imagenes/usuarios/ROOT/reciclado.png","Los productos reciclados..."));

		caracteristicaService.guardaCaracteristicasProducto(listaSellos);
		caracteristicaService.guardarCaracteristicaProductor(listaSellosProductor);
		
		
	}

	
	private void cargarProductores(Vendedor demo)
			throws JsonParseException, JsonMappingException, IOException, UsuarioInexistenteException {

		

		ArrayList<Fabricante> productores = new ArrayList<Fabricante>();
		Fabricante productor = new Fabricante();
		productor.setNombre("Burbuja Latina");
		productor.setDescripcionCorta("descripcion Corta Del Productor");
		productor.setDescripcionLarga("descripcion larga");
		productor.setCaracteristica(caracteristicaService.buscarCaracteristicasProductor().get(0));
		productores.add(productor);

		//PRODUCTO
		Producto producto = new Producto();
		producto.setNombre("Detergente");
		producto.setCategoria(demo.getCategorias().get(0));
		producto.setFabricante(productor);
		producto.setCaracteristicas(listaSellos);
		//VARIANTE
		Variante variante = new Variante();
		variante.setNombre("Envase grande");
		variante.setDescripcion("Este detergente es orgánico");
		variante.setDestacado(false);
		variante.setPrecio(40.0);
		variante.setStock(100);
		variante.setCantidadReservada(0);
		
		
		List<Imagen>imgs = new ArrayList<Imagen>();
		
		Imagen imagen = new Imagen();
		imagen.setPath("/imagenes/usuarios/demo/detergente.jpg");
		///home/huenu/workspace/chasqui-diciembre/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/chasqui/imagenes//usuarios/demo/detergente.jpg
		imagen.setAbsolutePath(serverRelativePath);
		imagen.setPreview(true);
		imgs.add(imagen);
		variante.setImagenes(imgs);
	
		
		variante.setProducto(producto);
		ArrayList<Variante> variantes = new ArrayList<Variante>();
		variantes.add(variante);
		
		producto.setVariantes(variantes);
		producto.setFabricante(productor);
		
		ArrayList<Producto> productos = new ArrayList<Producto>();
		productos.add(producto);
		

		demo.getCategorias().get(0).setProductos(productos);
		demo.setFabricantes(productores);
		//demo.setCategorias(cs);
		productor.setIdVendedor(demo.getId());


		
		
//		Vendedor demo = (Vendedor) usuarioService.obtenerUsuarioPorEmail(demoEmail);
//
//		List<FabricanteResponse> listaFabricantes;
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
////		listaFabricantes = mapper.readValue(listaProductoresDemo, new TypeReference<List<FabricanteResponse>>() {		});
//
//		for (FabricanteResponse fabricanteResponse : listaFabricantes) {
//			Fabricante productor = new Fabricante();
//			productor.setNombre(fabricanteResponse.getNombreProductor());
//			productor.setDescripcionCorta(fabricanteResponse.getDescripcionCorta());
//			productor.setDescripcionLarga(fabricanteResponse.getDescripcionLarga());
//			productor.setIdVendedor(demo.getId());
//			productor.setLocalidad(fabricanteResponse.getDireccion().getLocalidad());
//			productor.setCalle(fabricanteResponse.getDireccion().getCalle());
//			productor.setAltura(fabricanteResponse.getDireccion().getAltura());
//
//			// TODO importar de properties
//			Producto producto = new Producto();
//			producto.setCategoria(demo.getCategorias().get(1));
//			producto.setNombre("Vino de la costa");
//			List<Variante> variantes = new ArrayList<Variante>();
//			Variante v = new Variante();
//			v.setCodigo("var001");
//			v.setNombre("Blanco");
//			v.setPrecio(40.0);
//			v.setDescripcion("El vino de la costa...");
//			v.setStock(100);
//			v.setProducto(producto);
//
//			variantes.add(v);
//			producto.setVariantes(variantes);
//			productor.agregarProducto(producto);
//
//			demo.agregarProductor(productor);
//
//		}
//
//		usuarioService.guardarUsuario(demo);
	}
}
