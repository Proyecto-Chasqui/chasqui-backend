package chasqui.view.composer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

import chasqui.exceptions.StartUpException;
import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Categoria;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.Refresher;
import chasqui.view.renders.StartupErrorsRenderer;

@SuppressWarnings({"serial","deprecation"})
public class CargaStartUpComposer extends GenericForwardComposer<Component> implements Refresher{
	
	public AnnotateDataBinder binder;
	private Vendedor vendedor;	
	// Carga de excels
	private Label confirmationLabel; 
	private Fileupload uploadStartUp;
	private FileSaver fileSaver;
	@Autowired
	private UsuarioService usuarioService;
	private ProductorService productorService;
	private CaracteristicaService caracteristicaService;
	
	private Map<String, CaracteristicaProductor> caracteristicasProductor;
	private Map<String, Caracteristica> caracteristicasProducto;
	
	// Codigos
	

	Map<String,String> codigos;
	
	// Configuracion de las hojas del import
	
	private int productor_sheet = 0;
	private int producto_sheet = 1;
	
	// Configuracion de las columnas del import

	private int productor_nombre = 0;
	private int productor_sellos = 1;
	private int productor_descripcionCorta = 2;
	private int productor_descripcionLarga = 3;
	
	private int producto_nombre = 0;
	private int producto_precio = 1;
	private int producto_sellos = 2;
	private int producto_productor = 3;
	private int producto_categoria = 4;
	private int producto_stock = 5;
	private int producto_codigo = 6;
	private int producto_descripcion = 7;
	
	private String sellos_separator = ",";
	
	// Errores
	
	List<String> errores = new ArrayList<String>();
	public List<String> getErrores() {
		return errores;
	}

	public void setErrores(List<String> errores) {
		this.errores = errores;
	}


	private Listbox listboxErrores;
		
	public Listbox getListboxErrores() {
		return listboxErrores;
	}

	public void setListboxErrores(Listbox listboxErrores) {
		this.listboxErrores = listboxErrores;
	}

	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		// obtenerVendedorPorID
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		productorService = (ProductorService) SpringUtil.getBean("productorService");
		caracteristicaService = (CaracteristicaService) SpringUtil.getBean("caracteristicaService");
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");

		vendedor = (Vendedor) Executions.getCurrent().getArg().get("vendedor");
		vendedor = usuarioService.obtenerVendedorPorID(vendedor.getId());
		usuarioService.inicializarListasDe(vendedor);
		
		// Codigos de Sellos
		codigos = new HashMap<String, String>();
		codigos.put("Cooperativas", "COOPES");
		codigos.put("Agricultura Familiar", "AGRFML");
		codigos.put("Empresa Social", "EMPSOC");
		codigos.put("Recuperadas", "RECPER");
		codigos.put("Agroecológico", "AGRECO");
		codigos.put("Artesanal", "ARTESA");
		codigos.put("En Red", "EN_RED");
		codigos.put("Kilómetro Cero", "KMCERO");
		codigos.put("Orgánico", "ORGANI");
		codigos.put("Reciclado", "RECICL");
		
		caracteristicasProductor = new HashMap<String, CaracteristicaProductor>();		
		for(CaracteristicaProductor c: caracteristicaService.buscarCaracteristicasProductor()){
			caracteristicasProductor.put(nombreSelloToCodigo(c.getNombre()), c);
		}
		
		caracteristicasProducto = new HashMap<String, Caracteristica>();		
		for(Caracteristica c: caracteristicaService.buscarCaracteristicasProducto()){
			caracteristicasProducto.put(nombreSelloToCodigo(c.getNombre()), c);
		}
		
		listboxErrores.setItemRenderer(new StartupErrorsRenderer(this.self));
		
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}
	
	public void refresh() {
		this.binder.loadAll();	
	}
	
	public void onUpload$uploadStartUp(UploadEvent evt){
		
		errores = new ArrayList<String>();
		Media media = evt.getMedia();
		InputStream fin = (InputStream) media.getStreamData();
        		
		try {
			Workbook wb = WorkbookFactory.create(fin);
			verifyExcel(wb);
			readExcel(wb);
			confirmationLabel.setValue("Carga completa sin errores!");
			uploadStartUp.setVisible(false);
			listboxErrores.setVisible(false);
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StartUpException e){
			
		}
		
		binder.loadAll();
	}
	
	private void verifyExcel(Workbook wb) throws EncryptedDocumentException, InvalidFormatException, IOException, StartUpException{
        Sheet sheetProductores = wb.getSheetAt(productor_sheet);
        Sheet sheetProductos = wb.getSheetAt(producto_sheet);
        
        List<Fabricante> fabricantes = verifyProductores(sheetProductores);
        verifyProductos(sheetProductos, fabricantes);
	}
	
	
	private List<Fabricante> verifyProductores(Sheet sheetProductores) throws StartUpException{
		int cantidadFabricantes = sheetProductores.getLastRowNum();
		List<Fabricante> res = new ArrayList<Fabricante>();
		for(int i = 1; i<=cantidadFabricantes; i++){
			Row row = sheetProductores.getRow(i);
			String nombre = row.getCell(productor_nombre).toString();
			if(nombre == ""){
				errores.add("Productor en linea " + (i+1) + " sin nombre");
			}
			
			String[] sellos = cellContentToSellos(row.getCell(productor_sellos).toString());
			
			for(String sello: sellos){
				if(!verifySelloProductor(sello)){
					errores.add("Productor en linea " + (i+1) + " con sello invalido (" + sello + ")");
				}
			}
			
			res.add(new Fabricante(nombre));
		}
		return res;
	}
	
	
	private void verifyProductos(Sheet sheetProductos, List<Fabricante> productores) throws StartUpException{
		
		int cantidadProductos = sheetProductos.getLastRowNum();
		for(int i = 1; i<= cantidadProductos; i++){
			Row row = sheetProductos.getRow(i);
			
			// Verificacion de categoria
			try{
				rowStr(row, producto_categoria);
			} catch (Exception e){
				errores.add("Categoria en linea " + (i+1) + " sin nombre");
			}
			
			// Verificación del productor 
			Fabricante mockFabricante = new Fabricante(rowStr(row, producto_productor));
			
			if(productores.lastIndexOf(mockFabricante) < 0){
				errores.add("Productor en linea " + (i+1) + " de la lista de productos no presente en la lista de Productores");
			}
			
			// Verificacion del producto
			try{
				rowStr(row, producto_nombre);
			} catch (Exception e){
				errores.add("Producto en linea " + (i+1) + " sin nombre");
			}
			
			try{
				Double precio = rowDouble(row, producto_precio);
				if(precio < 0){
					errores.add("Producto en linea " + (i+1) + " con precio negativo");
				}
			} catch (Exception e){
				errores.add("Producto en linea " + (i+1) + " sin precio");
			}
			
			String[] sellos = cellContentToSellos(row.getCell(producto_sellos).toString());
			for(String sello: sellos){
				if(!verifySelloProducto(sello)){
					errores.add("Producto en linea " + (i+1) + " con sello invalido (" + sello + ")");
				}
			}
			
			try{
				int stock = rowInt(row, producto_stock);
				if(stock < 0){
					errores.add("Producto en linea " + (i+1) + " con stock negativo");
				}
			} catch (Exception e){
				errores.add("Producto en linea " + (i+1) + " sin stock");
			}
			
			
			try{
				rowStr(row, producto_codigo);
			} catch (Exception e){
				errores.add("Producto en linea " + (i+1) + " sin codigo");
			}
		}
		
		if(errores.size() > 0){
			throw new StartUpException("Algo fallo");
		}
	}
	
	
	private void readExcel(Workbook wb) throws IOException, EncryptedDocumentException, InvalidFormatException{
		Hibernate.initialize(vendedor.getCategorias());
		Hibernate.initialize(vendedor.getFabricantes());
		
        Sheet sheetProductores = wb.getSheetAt(productor_sheet);
        Sheet sheetProductos = wb.getSheetAt(producto_sheet);
        
        List<Fabricante> nuevosProductores = getNuevosProductoresFromSheet(sheetProductores);
        getNuevosProductosFromSheet(sheetProductos, nuevosProductores);
    
		usuarioService.guardarUsuario(vendedor);
		
	}
	
	private List<Fabricante> getNuevosProductoresFromSheet(Sheet sheet){
		List<Fabricante> res = new ArrayList<Fabricante>();
		int cantidadFabricantes = sheet.getLastRowNum();
		for(int i = 1; i<=cantidadFabricantes; i++){
			Row row = sheet.getRow(i);
			Fabricante nuevo = new Fabricante(row.getCell(productor_nombre).toString());
			
			//Seteo Sellos
			nuevo.setCaracteristicas(getSellosProductor(row.getCell(productor_sellos).toString()));
			
			// Seteo de la descripcion corta
			String descripcionCorta = row.getCell(productor_descripcionCorta).toString();
			descripcionCorta = (descripcionCorta == "") ? "Sin descripción" : descripcionCorta;
			nuevo.setDescripcionCorta(descripcionCorta);
			// Seteo de la descripcion larga
			String descripcionLarga = row.getCell(productor_descripcionLarga).toString();
			descripcionLarga = (descripcionLarga == "") ? "Sin descripción" : descripcionLarga;
			nuevo.setDescripcionLarga(descripcionLarga);
			
			productorService.guardar(nuevo);
			vendedor.agregarProductor(nuevo);
			usuarioService.guardarUsuario(vendedor);
			res.add(nuevo);
		}
		return res;
	}
	
	
	private void getNuevosProductosFromSheet(Sheet sheet, List<Fabricante> productores) throws IOException{
		List<Producto> productos = new ArrayList<Producto>();
		List<Categoria> categorias = new ArrayList<Categoria>();
		
		int cantidadProductos = sheet.getLastRowNum();
		for(int i = 1; i<= cantidadProductos; i++){
			Row row = sheet.getRow(i);
			Categoria nuevaCategoria = new Categoria(vendedor, rowStr(row, producto_categoria));
			if(categorias.lastIndexOf(nuevaCategoria) > -1){
				// Esta definicion es para no tener Categorias repetidas en la lista
				nuevaCategoria = categorias.get(categorias.lastIndexOf(nuevaCategoria));
			} else {
				nuevaCategoria.setVendedor(vendedor);
				vendedor.agregarCategoria(nuevaCategoria);
				usuarioService.guardarUsuario(vendedor);
				categorias.add(nuevaCategoria);
			}

			Fabricante mockFabricante = new Fabricante(rowStr(row, producto_productor));
			
			Fabricante productorDelProducto = productores.get(productores.lastIndexOf(mockFabricante));
			
			Producto nuevoProducto = new Producto(rowStr(row, producto_nombre), nuevaCategoria, productorDelProducto);
			nuevoProducto.setCaracteristicas(getSellosProducto(row.getCell(producto_sellos).toString()));
			
			Variante varianteDelProducto = new Variante();
			varianteDelProducto.setNombre(rowStr(row, producto_nombre));
			varianteDelProducto.setPrecio(rowDouble(row, producto_precio));
			varianteDelProducto.setStock(rowInt(row, producto_stock));
			varianteDelProducto.setCodigo(rowStr(row, producto_codigo));
			varianteDelProducto.setDescripcion(rowStr(row, producto_descripcion));
			varianteDelProducto.setCantidadReservada(0);
			varianteDelProducto.setDestacado(false);
			
			varianteDelProducto.setProducto(nuevoProducto);
			List<Imagen> imagenes = new ArrayList<Imagen>();
			imagenes.add(getImagenNoDisponible(varianteDelProducto.getNombre()));
			varianteDelProducto.setImagenes(imagenes);
			List<Variante> variantes = new ArrayList<Variante>();
			variantes.add(varianteDelProducto);
			nuevoProducto.setVariantes(variantes);
			
			nuevaCategoria.agregarProducto(nuevoProducto);
			nuevoProducto.setCategoria(nuevaCategoria);
			
			productorDelProducto.agregarProducto(nuevoProducto);
			nuevoProducto.setFabricante(productorDelProducto);
			
			Image imagenProductor = new Image();
			imagenProductor.setSrc(getImagenNoDisponible(productorDelProducto.getNombre()).getPath());
			productorDelProducto.setPathImagen(imagenProductor.getSrc());

			usuarioService.guardarUsuario(vendedor);

			productos.add(nuevoProducto);
		}
	}
	
	// Funciones para obtener valores con cierto tipo desde una celda
	private String rowStr(Row row, int i){
		return row.getCell(i).toString();
	}
	
	private Double rowDouble(Row row, int i){
		return new Double(row.getCell(i).getNumericCellValue());	
	}
	
	private int rowInt(Row row, int i){
		return new BigDecimal(row.getCell(i).getNumericCellValue()).setScale(0, RoundingMode.HALF_UP).intValue();	
	}
	
	
	// Seteo de la imagen por defecto de una variante
	private Imagen getImagenNoDisponible(String nombreElemento) throws IOException{
		ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
		String path = context.getRealPath("/imagenes/");
		String sourcePath = context.getRealPath("/imagenes/imagennodisponible.jpg");
		BufferedImage originalImage = ImageIO.read(new File(sourcePath));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		Imagen imagen = fileSaver.guardarImagen(path, vendedor.getUsername(), nombreElemento+"_ND", imageInByte);
		imagen.setNombre(nombreElemento + "_imagenNoDisponible");
		imagen.setPreview(false);
		return imagen;
	}
	
	private String[] cellContentToSellos(String cellContent){
		return cellContent.split(sellos_separator);
	}
	
	private boolean verifySelloProductor(String sello){
		return  sello.equals("COOPES") ||
				sello.equals("AGRFML") ||
				sello.equals("EMPSOC") ||
				sello.equals("RECPER") ||
				sello.equals("");
	}
	
	private boolean verifySelloProducto(String sello){
		return  sello.equals("AGRECO") ||
				sello.equals("ARTESA") ||
				sello.equals("EN_RED") ||
				sello.equals("KMCERO") ||
				sello.equals("ORGANI") ||
				sello.equals("RECICL") ||
				sello.equals("");
	}
	
	private String nombreSelloToCodigo(String nombre){		
		return codigos.get(nombre);
	}
	
	private List<CaracteristicaProductor> getSellosProductor(String codigos){
		List<CaracteristicaProductor> res = new ArrayList<CaracteristicaProductor>();
		
		for(String c: cellContentToSellos(codigos)){
			res.add(caracteristicasProductor.get(c));
		}
			
		return res;
	}

	
	private List<Caracteristica> getSellosProducto(String codigos){
		List<Caracteristica> res = new ArrayList<Caracteristica>();
		
		for(String c: cellContentToSellos(codigos)){
			res.add(caracteristicasProducto.get(c));
		}
			
		return res;
	}

	
}