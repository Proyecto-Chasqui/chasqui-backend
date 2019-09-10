package chasqui.misc.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Filedownload;

import chasqui.dtos.ProductoDTO;
import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.services.interfaces.ProductoService;

public class RootDataVendorsXlsExport {
	
	private String nombreVendedor = "export";
	private Workbook wb = new HSSFWorkbook();
	private Map<String, CellStyle> styles = createStyles(wb);

	private Sheet sheet;
	
	private String[] titles = null;
	private Map<String,String> codigos= new HashMap<String,String>();
	
	public RootDataVendorsXlsExport() {
		
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
	}
	
	public void resetSheets() {
		wb = new HSSFWorkbook();
		styles = createStyles(wb);
		sheet=null;
		titles = null;
	}

	
	public void exportarDatosProductores(List<Fabricante> productores, String nombreDeVendedor) throws IOException {
		formarTablaProductores(productores);
		nombreVendedor = nombreDeVendedor;
		doFile();
	}

	private void formarTablaProductores(List<Fabricante> productores) {
		titles = new String[] {"Nombre","Sellos","Descripcion Corta","Descripcion Larga"};
		sheet = wb.createSheet("Productores");
		doColumnNames();
		for(int i = 0 ; i<productores.size(); i ++) {
			loadInfoInTable(productores.get(i),i +1);
		}
		doDetails(3);
	}

	public void exportarDatosProductos(List<ProductoDTO> productos, String nombreDeVendedor) throws IOException{
		nombreVendedor = nombreDeVendedor;
		formarTablaProductos(productos);
		doFile();
	}

	private void formarTablaProductos(List<ProductoDTO> productos) {
		titles = new String[] {"Nombre", "Precio", "Sello", "Productor", "Categoria", "Stock", "Código", "Descripción"};
		sheet = wb.createSheet("Productos");
		doColumnNames();
		for(int i = 0 ; i<productos.size();i ++) {
			loadInfoInTable(productos.get(i),i+1);
		}
		doDetails(3);
		
	}

	public void exportarDatosSellos(List<Caracteristica> sellos, List<CaracteristicaProductor> sellosProductor,String nombreDeVendedor) throws IOException {
		nombreVendedor = nombreDeVendedor;
		formarTablaSellos(sellos,sellosProductor);
		doFile();
	}

	private void formarTablaSellos(List<Caracteristica> sellos, List<CaracteristicaProductor> sellosProductor) {
		titles = new String[] {"ID","Nombre","Descripcion"};
		sheet = wb.createSheet("Sellos Producto");
		doColumnNames();
		int i = 1;
		for(Caracteristica c : sellos) {
			loadInfoInTable(c,i);
			i = i +1;
		}
		doDetails(5);
		sheet = wb.createSheet("Sellos Productor");
		doColumnNames();
		i = 1;
		for(CaracteristicaProductor cp: sellosProductor) {
			loadInfoInTable(cp,i);
			i = i +1;
		}
		doDetails(5);
	}

	//<Tipo,List<Tipo>>
	@SuppressWarnings("unchecked")
	public void exportarTodos(Map<String,List<?>> listas, String nombreDeVendedor) throws IOException {
		nombreVendedor = nombreDeVendedor;
		formarTablaProductores((List<Fabricante>) listas.get("Productores"));
		formarTablaProductos((List<ProductoDTO>) listas.get("Productos"));
		doFile();
	}
	
	
	private void doColumnNames() {
		// Arma la cabecera con los titulos de columnas
		Row headerRow = sheet.createRow(0);
		headerRow.setHeightInPoints(20);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		Cell headerCell;
		for (int i = 0; i < titles.length; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titles[i]);
			headerCell.setCellStyle(styles.get("header"));
		}	

	}
	//{"Nombre","Sellos","Descripcion Corta","Descripcion Larga"};
	private void loadInfoInTable(Fabricante f, Integer posicion) {
		sheet.createRow(posicion);
		Row row = sheet.getRow(posicion);
		createCellsfor(row,4);
		row.getCell(0).setCellValue(f.getNombre());
		String text = "";
		if(f.getCaracteristicas()!= null) {
			for(CaracteristicaProductor sello: f.getCaracteristicas()) {
				text = text + codigos.get(sello.getNombre()) +",";
			}
			text = text.replaceFirst(".$","");
		}
		row.getCell(1).setCellValue(text);
		row.getCell(2).setCellValue(f.getDescripcionCorta());
		row.getCell(3).setCellValue(Jsoup.parse(f.getDescripcionLarga()).text());

		//row.getCell(4).setCellValue(f.getIdVendedor());
	}
//{"Nombre", "Precio", "Sello", "Productor", "Categoria", "Stock", "Código", "Descripción"};
	private void loadInfoInTable(ProductoDTO p, Integer posicion) {
		sheet.createRow(posicion);
		Row row = sheet.getRow(posicion);
		createCellsfor(row,8);
		row.getCell(0).setCellValue(p.getNombre());
		row.getCell(1).setCellValue(p.getPrecio());
		row.getCell(2).setCellValue(convertirNombreSelloAListaCodigos(p.getSellos()));
		row.getCell(3).setCellValue(p.getNombreProductor());
		row.getCell(4).setCellValue(p.getCategoria());
		row.getCell(5).setCellValue(p.getStock());
		row.getCell(6).setCellValue(p.getCodigoInterno());
		row.getCell(7).setCellValue(Jsoup.parse(p.getDescripcion()).text());
	}
	
	
	private String convertirNombreSelloAListaCodigos(List<String> sellos) {
		String texto = "";
		for(String nombresello : sellos) {
			texto = texto + codigos.get(nombresello) + ",";
		}
		texto = texto.replaceFirst(".$","");
		return texto;
	}

	//{"ID","Nombre","Tipo","Descripcion"}
	private void loadInfoInTable(Caracteristica c, Integer posicion) {
		sheet.createRow(posicion);
		Row row = sheet.getRow(posicion);
		createCellsfor(row,3);
		row.getCell(0).setCellValue(c.getId());
		row.getCell(1).setCellValue(c.getNombre());
		row.getCell(2).setCellValue(c.getDescripcion());
	}
	
	private void loadInfoInTable(CaracteristicaProductor c, int posicion) {
		sheet.createRow(posicion);
		Row row = sheet.getRow(posicion);
		createCellsfor(row,3);
		row.getCell(0).setCellValue(c.getId());
		row.getCell(1).setCellValue(c.getNombre());
		row.getCell(2).setCellValue(c.getDescripcion());
		
	}
	
	private void showDownload() throws IOException {
		File tempFile = File.createTempFile("DataChasqui", nombreVendedor);
		String outputFile = tempFile.getAbsolutePath();
		FileOutputStream out = new FileOutputStream(outputFile);
		wb.write(out);
		Filedownload.save(tempFile, "xls");
		tempFile.deleteOnExit();
		out.close();
	}
	
	private void clean() throws IOException {
		titles = null;
		wb.close();
		wb = new HSSFWorkbook();
		styles = createStyles(wb);
		sheet = null;
		nombreVendedor = "export";
	}
	
	private void doFile() throws IOException {
		showDownload();
		clean();		
	}
	
	private void doDetails(Integer multiplier) {
		// Se ajustan los anchos de las celdas, el ancho esta medido en unidades de 1/256
		// del ancho de un caracter
		for(int i = 0; i<titles.length; i ++) {
			if(titles[i].length() < 7) {
				sheet.setColumnWidth(i, titles[i].length() * (multiplier+2) * 256);
			}else{
			sheet.setColumnWidth(i, titles[i].length() * multiplier * 256);
			}
		}
	}
	
	private void createCellsfor(Row row,Integer cant) {
		for(int i=0;i<cant;i++) {
			row.createCell(i);
			row.getCell(i).setCellStyle(styles.get("cell"));
		}		
	}
	
	/**
	 * Seccion para las librerias de stilo
	 */
 	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<>();
		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 18);
		titleFont.setBold(true);
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(titleFont);
		styles.put("title", style);

		Font monthFont = wb.createFont();
		monthFont.setFontHeightInPoints((short) 11);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(monthFont);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("header", style);
		
		Font contactData = wb.createFont();
		contactData.setFontHeightInPoints((short) 11);
		contactData.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(contactData);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("header2", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(style.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("formula", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula_2", style);

		return styles;
	}
}
