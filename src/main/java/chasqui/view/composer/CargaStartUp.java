package chasqui.view.composer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Textbox;

import chasqui.model.Categoria;
import chasqui.model.Fabricante;
import chasqui.model.Producto;
import chasqui.model.Vendedor;
import chasqui.view.genericEvents.Refresher;

@SuppressWarnings({"serial","deprecation"})
public class CargaStartUp extends GenericForwardComposer<Component> implements Refresher{
	
	public AnnotateDataBinder binder;
	private Vendedor vendedor;	
	// Carga de excels
	private Fileupload uploadStartUp;
	private Textbox productosYaCargados; 
	
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		this.vendedor = (Vendedor) Executions.getCurrent().getArg().get("vendedor");
		productosYaCargados.setValue("Hay 86 productos cargados");
		
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}
	
	public void refresh() {
		this.binder.loadAll();	
	}
	
	public void onUpload$uploadImagen(UploadEvent evt){
			
		Media media = evt.getMedia();
		InputStream fin = (InputStream) media.getStreamData();
        		
		try {
			Workbook wb = WorkbookFactory.create(fin);

	        Sheet sheetProductores = wb.getSheetAt(0);
	        Sheet sheetProductos = wb.getSheetAt(1);
	        
	        List<Fabricante> nuevosProductores = getNuevosProductoresFromSheet(sheetProductores);
	        List<Producto> nuevosProductos = getNuevosProductosFromSheet(sheetProductos, nuevosProductores);
	        
            
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
	
	private List<Fabricante> getNuevosProductoresFromSheet(Sheet sheet){
		List<Fabricante> res = new ArrayList<Fabricante>();
		int cantidadFabricantes = sheet.getLastRowNum();
		for(int i = 1; i<=cantidadFabricantes; i++){
			Row row = sheet.getRow(i);
			Fabricante nuevo = new Fabricante(row.getCell(0).toString());
			nuevo.setDescripcionCorta(row.getCell(1).toString());
			nuevo.setDescripcionLarga(row.getCell(2).toString());
			res.add(nuevo);
		}
		return res;
	}
	
	private List<Producto> getNuevosProductosFromSheet(Sheet sheet, List<Fabricante> productores){
		List<Producto> productos = new ArrayList<Producto>();
		List<Categoria> categorias = new ArrayList<Categoria>();
		int cantidadProductos = sheet.getLastRowNum();
		for(int i = 1; i<=cantidadProductos; i++){
			Row row = sheet.getRow(i);
			Categoria nuevaCategoria = new Categoria(this.vendedor, row.getCell(3).toString());
			if(categorias.lastIndexOf(nuevaCategoria) > 0){
				nuevaCategoria = categorias.get(categorias.lastIndexOf(nuevaCategoria));
			} else {
				categorias.add(nuevaCategoria);
			}

			Fabricante mock = new Fabricante(row.getCell(2).toString());
			
			Fabricante productorDelProducto = productores.get(productores.lastIndexOf(mock));
			
			Producto nuevoProducto = new Producto(row.getCell(0).toString(), nuevaCategoria, productorDelProducto);

			productos.add(nuevoProducto);
		}
		return productos;
	}
}
