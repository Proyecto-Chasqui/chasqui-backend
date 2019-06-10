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
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;

import chasqui.model.Categoria;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.Refresher;

@SuppressWarnings({"serial","deprecation"})
public class CargaStartUpComposer extends GenericForwardComposer<Component> implements Refresher{
	
	public AnnotateDataBinder binder;
	private Vendedor vendedor;	
	// Carga de excels
	//private Fileupload uploadStartUp;
	private Textbox productosYaCargados; 
	private FileSaver fileSaver;
	@Autowired
	private UsuarioService usuarioService;
	private ProductorService productorService;
	
	// Configuracion de las hojas del import
	
	private int productor_sheet = 0;
	private int producto_sheet = 1;
	
	// Configuracion de las columnas del import

	private int productor_nombre = 0;
	private int productor_descripcionCorta = 1;
	private int productor_descripcionLarga = 2;
	
	private int producto_nombre = 0;
	private int producto_precio = 1;
	private int producto_productor = 2;
	private int producto_categoria = 3;
	private int producto_stock = 4;
	private int producto_codigo = 5;
	private int producto_descripcion = 6;
	
		
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		// obtenerVendedorPorID
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		productorService = (ProductorService) SpringUtil.getBean("productorService");
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");

		vendedor = (Vendedor) Executions.getCurrent().getArg().get("vendedor");
		vendedor = usuarioService.obtenerVendedorPorID(vendedor.getId());
		usuarioService.inicializarListasDe(vendedor);
		productosYaCargados.setValue("Hay los que se me cantan productos cargados");
		
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}
	
	public void refresh() {
		this.binder.loadAll();	
	}
	
	public void onUpload$uploadStartUp(UploadEvent evt){
			
		Media media = evt.getMedia();
		InputStream fin = (InputStream) media.getStreamData();
        		
		try {
			readExcel(fin);
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		binder.loadAll();
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readExcel(InputStream fin) throws IOException, EncryptedDocumentException, InvalidFormatException{
		Hibernate.initialize(vendedor.getCategorias());
		Hibernate.initialize(vendedor.getFabricantes());
		
		Workbook wb = WorkbookFactory.create(fin);

        Sheet sheetProductores = wb.getSheetAt(productor_sheet);
        Sheet sheetProductos = wb.getSheetAt(producto_sheet);
        
        List<Fabricante> nuevosProductores = getNuevosProductoresFromSheet(sheetProductores);
        Map<String, List> otrosDatos = getNuevosProductosFromSheet(sheetProductos, nuevosProductores);
        List<Producto> nuevosProductos = (List<Producto>)otrosDatos.get("productos");
        List<Categoria> nuevasCategorias = (List<Categoria>)otrosDatos.get("categorias");
    
		usuarioService.guardarUsuario(vendedor);
		
	}
	
	private List<Fabricante> getNuevosProductoresFromSheet(Sheet sheet){
		List<Fabricante> res = new ArrayList<Fabricante>();
		int cantidadFabricantes = sheet.getLastRowNum();
		for(int i = 1; i<=cantidadFabricantes; i++){
			Row row = sheet.getRow(i);
			Fabricante nuevo = new Fabricante(row.getCell(productor_nombre).toString());
			nuevo.setDescripcionCorta(row.getCell(productor_descripcionCorta).toString());
			nuevo.setDescripcionLarga(row.getCell(productor_descripcionLarga).toString());
			productorService.guardar(nuevo);
			vendedor.agregarProductor(nuevo);
			usuarioService.guardarUsuario(vendedor);
			res.add(nuevo);
		}
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	private Map<String, List> getNuevosProductosFromSheet(Sheet sheet, List<Fabricante> productores) throws IOException{
		List<Producto> productos = new ArrayList<Producto>();
		List<Categoria> categorias = new ArrayList<Categoria>();
		Map<String,List> res = new HashMap<String,List>();
		
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
		res.put("productos", productos);
		res.put("categorias", categorias);
		return res;
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
}

	
