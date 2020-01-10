package chasqui.view.composer;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.cxf.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.zkforge.ckez.CKeditor;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.dao.FabricanteDAO;
import chasqui.dao.ProductoDAO;
import chasqui.model.Caracteristica;
import chasqui.model.Categoria;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.RefreshListener;
import chasqui.view.genericEvents.Refresher;
import chasqui.view.renders.ImagenesRender;
import chasqui.view.renders.VarianteItemRenderer;

@SuppressWarnings({"serial","deprecation"})
public class ABMProductoComposer extends GenericForwardComposer<Component> implements Refresher{

	public AnnotateDataBinder binder;
	private Textbox nombreProducto;
	private Combobox comboCategorias;
	private Combobox comboFabricantes;
	private Combobox comboCaracteristicas;
	private Toolbarbutton botonAgregarFabricante;
	private Toolbarbutton botonAgregarCategoria;
	private Listbox listboxCaracteristicas;
	private Toolbarbutton botonGuardar;
	private Toolbarbutton botonCancelar;
	private Textbox agregarCaractTextbox;
	private Popup popUpCaracteristica;
	private Tab tabdetalles;
	private Tab tabdescsellos;
	private Listitem listitemincentivo;
	private Producto model;
	private List<Caracteristica> caracteristicas;
	private Categoria categoriaSeleccionada;
	private Caracteristica caracteristicaSeleccionada;
	private Fabricante productorSeleccionado; 
	private List<Variante>varianteRollback; 
	private List<Caracteristica>caracteristicasProducto;
	private Caracteristica caracteristicaProductoSeleccionada;
	private FabricanteDAO fabricantedao;
	private Vendedor usuario;
	private boolean modoEdicion;
	private Doublebox incentivo;
	private Doublebox totalPrecio;
	
	private Auxheader auxheaderproducto;
	private Popup cantidadCaracteres;
	private Label mensaje;
	
	
	private UsuarioService usuarioService;
	private CaracteristicaService caracteristicaService;
	
	//Seccion variante
	
	private FileSaver fileSaver; 
	private Variante modelv;
	private List<Imagen> imagenes;	
	private Doublebox doubleboxPrecio; 
	private Intbox intboxStock;
	private Textbox textboxCodigo; 
	private Textbox ckEditor; 
	private Fileupload uploadImagen; 
	private Listbox listImagenes;	
	private ImagenesRender imgRender;
	private ProductoDAO productodao;
	
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		imgRender = new ImagenesRender(comp,true,this);
		model = (Producto) Executions.getCurrent().getArg().get("producto");
		Integer accion = (Integer) Executions.getCurrent().getArg().get("accion");
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		caracteristicaService = (CaracteristicaService) SpringUtil.getBean("caracteristicaService");
		fabricantedao = (FabricanteDAO) SpringUtil.getBean("fabricanteDAO");
		productodao = (ProductoDAO)  SpringUtil.getBean("productoDAO");
		caracteristicasProducto = caracteristicaService.buscarCaracteristicasProducto();
		comp.addEventListener(Events.ON_RENDER, new RefreshListener<ABMProductoComposer>(this));
		comp.addEventListener(Events.ON_CLICK, new BorrarImagenEventListener(this));
		comp.addEventListener(Events.ON_USER, new DescargarImagenEventListener(this));
		imagenes = new ArrayList<Imagen>();
		
		listImagenes.setItemRenderer(imgRender);
		inicializarVentana(accion);		
		
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}

	public void inicializarVentana(Integer accion){
	
		if(model != null && Constantes.VENTANA_MODO_EDICION.equals(accion)){
			inicializarModoEdicion();
		}
		
		if(Constantes.VENTANA_MODO_EDICION.equals(accion) && model == null){
			model = Producto.crearProductoEmpty();
			modelv = new Variante();
			inicializarModoEdicion();
		}
		
		if(Constantes.VENTANA_MODO_LECTURA.equals(accion)){
			modelv = model.getVariantes().get(0);
			varianteRollback = new ArrayList<Variante>( model.getVariantes());
			inicializarModoLectura();
		}
		
	}

	public void inicializarModoEdicion(){
		modoEdicion= true;
		imgRender.setLectura(true);
		listImagenes.setDisabled(false);
		listitemincentivo.setVisible(usuario.getEstrategiasUtilizadas().isUtilizaIncentivos());
		if(model.getCategoria() != null && model.getFabricante() != null){
			categoriaSeleccionada = model.getCategoria();
			productorSeleccionado = model.getFabricante();
			comboFabricantes.setValue(productorSeleccionado.toString());
			comboCategorias.setValue(categoriaSeleccionada.toString());
			
		}
		if(!model.getVariantes().isEmpty()){
			modelv = model.getVariantes().get(0);
			doubleboxPrecio.setValue(modelv.getPrecio());
			incentivo.setValue(modelv.getIncentivo());
			totalPrecio.setReadonly(true);
			totalPrecio.setValue(modelv.getIncentivo() + modelv.getPrecio());
			intboxStock.setValue(modelv.getStock());
			textboxCodigo.setValue(modelv.getCodigo());
			ckEditor.setValue(Jsoup.parse(modelv.getDescripcion()).wholeText());
			imagenes.addAll(modelv.getImagenes());
			
		}
		caracteristicas = model.getCaracteristicas();
		nombreProducto.setValue(model.getNombre());
		varianteRollback = new ArrayList<Variante>( model.getVariantes());
	}
	
	public void onSelect$comboCaracteristicas(SelectEvent evt) {
		this.onClick$botonAgregarCaracteristica();
	}	

	
	public void onDestacarVariante(Variante v){
		if(v.getDestacado()){
			v.setDestacado(false);
		}else{
			v.setDestacado(true);
		}
		binder.loadAll();
	}
	
	public void onClick$botonAgregarCategoria(){
		Window w = (Window) Executions.createComponents("/abmCategoria.zul", this.self, null);
		w.doModal();		
	}
	
	public void onClick$botonGuardar() throws IOException{
		validaciones();
		ejecutarValidaciones();
		guardarImagenNoDisponible();
		boolean productoNuevo = false;
		model.setNombre(nombreProducto.getValue());
		model.setCaracteristicas(caracteristicas);
		if(model.getId() == null){
			productoNuevo = true;
			categoriaSeleccionada.agregarProducto(model);							
		}
		model.setCategoria(categoriaSeleccionada);
		//Sync fix
		if(model.getFabricante() != null){
			model.getFabricante().eliminarProducto(model);
		}
		model.setFabricante(productorSeleccionado);
		productorSeleccionado.agregarProducto(model);
		
		//variante section
		
		modelv.setCodigo(textboxCodigo.getValue());
		modelv.setDescripcion(ckEditor.getValue());
		modelv.setImagenes(imagenes);
		modelv.setStock(intboxStock.getValue());
		modelv.setPrecio(doubleboxPrecio.getValue());
		if(usuario.getEstrategiasUtilizadas().isUtilizaIncentivos()) {
			modelv.setIncentivo(incentivo.getValue());
		}
		modelv.setCantidadReservada(0);
		modelv.setProducto(model);
		
		if(model == null){	
			modelv.setDestacado(false);
			model.getVariantes().add(modelv);			
		}else{
			destacarSiNoLoEsta(modelv);
			List<Variante> variantes= new ArrayList<Variante>();
			variantes.add(modelv);
			model.setVariantes(variantes);
		}
		usuarioService.guardarUsuario(usuario);
		if(productoNuevo) {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("accion", "productoGuardado");		
			Events.sendEvent(Events.ON_NOTIFY, this.self.getParent(), params);
		}else {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("accion", "productoEditado");		
			Events.sendEvent(Events.ON_NOTIFY, this.self.getParent(), params);

		}
		this.self.detach();
	}
	
	private void guardarImagenNoDisponible() throws IOException {
		if(imagenes.isEmpty()){
			ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
			String path = context.getRealPath("/imagenes/");
			String sourcePath = context.getRealPath("/imagenes/imagennodisponible.jpg");
			BufferedImage originalImage = ImageIO.read(new File(sourcePath));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, "jpg", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			Imagen imagen = fileSaver.guardarImagen(path ,usuario.getUsername(),nombreProducto.getValue()+"ND",imageInByte);
			imagen.setNombre(nombreProducto.getValue() + "_imagenNoDisponible");
			imagen.setPreview(false);
			imagenes.add(imagen);
		}
	}
	
	private void destacarSiNoLoEsta(Variante modelv2) {
		if(modelv2.getDestacado() == null){
			modelv2.setDestacado(false);
		}
		
	}

	private void validaciones(){
		String nombre = nombreProducto.getValue();
		if(StringUtils.isEmpty(nombre)){
			throw new WrongValueException(tabdetalles,"El nombre no debe ser vacio!");
		}
		if(categoriaSeleccionada == null){
			throw new WrongValueException(tabdetalles,"Se debe seleccionar una categoria");
		}
		if(productorSeleccionado == null){
			throw new WrongValueException(tabdetalles,"Se debe seleccionar un productor");
		}
		if(textboxCodigo == null || textboxCodigo.getValue().equals("")){
			throw new WrongValueException(tabdetalles,"Se debe escribir un codigo de producto	");
		}
		if(existeCodigo(textboxCodigo.getValue())) {
			throw new WrongValueException(tabdetalles,"El código del producto ya existe");
		}
	}
	
	private boolean existeCodigo(String codigo) {
		List<Producto> variante = usuario.getProductosConCodigo(codigo);
		if(modoEdicion) {
			if(model.getId()==null) {			
				return variante.size()>=1;
			}else {
				if(model.getVariantes().get(0).getCodigo().equals(codigo)) {
					return variante.size()>1;
				} else {
					return variante.size()>=1;
				}
			}
		}
		return false;
	}

	public void onEliminarVariante(Variante v){
		model.getVariantes().remove(v);
		this.binder.loadAll();
	}
	
	public void onClick$botonCancelar(){
		rollbackProducto();
		this.self.detach();
	}
	
	public void onClose$productosWindow(){
		rollbackProducto();
		this.self.detach();
	}
	
	private void rollbackProducto(){
		model.setVariantes(varianteRollback);
	}
	
	
	
	public void onVerVariante(Variante v){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("producto",model);
		params.put("variante", v);
		params.put("lectura", true);
		Window w = (Window) Executions.createComponents("/abmVariante.zul", this.self, params);
		w.doModal();
	}
	
	
	public void onEditarVariante(Variante v) {
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("producto",model);
		params.put("variante", v);
		params.put("lectura", false);
		Window w = (Window) Executions.createComponents("/abmVariante.zul", this.self, params);
		w.doModal();
	}
	
	public void onAltaVariante(){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("producto",model);
		params.put("lectura", false);
		Window w = (Window) Executions.createComponents("/abmVariante.zul", this.self, params);
		w.doModal();
	}
	
	public void onClick$botonAgregarFabricante(){
		Window w = (Window)Executions.createComponents("/abmProductor.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$botonAgregarCaracteristica(){
		
		if(caracteristicaProductoSeleccionada == null){
			throw new WrongValueException(comboCaracteristicas,"Debe seleccionar una caracteristica.");
		}
		if(caracteristicas.contains(caracteristicaProductoSeleccionada)){
			throw new WrongValueException("El producto ya posee la caracteristica que desea agregar");			
		}
		caracteristicas.add(caracteristicaProductoSeleccionada);
		Clients.showNotification("Agregada la caracterisitica " + caracteristicaProductoSeleccionada.getNombre(),
								"info", listboxCaracteristicas, "top_center",
								3000);
		comboCaracteristicas.setValue(null);
		caracteristicaProductoSeleccionada = null;
		refresh();
		
	}
	
	
	public void onEliminarCaracteristica(){
		if(!listboxCaracteristicas.isDisabled()){			
			caracteristicas.remove(caracteristicaSeleccionada);
		}
		refresh();
	}
	
	
	
	
	public Producto getModel() {
		return model;
	}
	public void setModel(Producto model) {
		this.model = model;
	}

	public Categoria getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public Caracteristica getCaracteristicaSeleccionada() {
		return caracteristicaSeleccionada;
	}

	public void setCaracteristicaSeleccionada(Caracteristica caracteristicaSeleccionada) {
		this.caracteristicaSeleccionada = caracteristicaSeleccionada;
	}

	





	public List<Caracteristica> getCaracteristicasProducto() {
		return caracteristicasProducto;
	}



	public void setCaracteristicasProducto(List<Caracteristica> caracteristicasProducto) {
		this.caracteristicasProducto = caracteristicasProducto;
	}



	public Caracteristica getCaracteristicaProductoSeleccionada() {
		return caracteristicaProductoSeleccionada;
	}



	public void setCaracteristicaProductoSeleccionada(Caracteristica caracteristicaProductoSeleccionada) {
		this.caracteristicaProductoSeleccionada = caracteristicaProductoSeleccionada;
	}



	public Fabricante getProductorSeleccionado() {
		return productorSeleccionado;
	}



	public void setProductorSeleccionado(Fabricante productorSeleccionado) {
		this.productorSeleccionado = productorSeleccionado;
	}



	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}
	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	public Vendedor getUsuario() {
		return usuario;
	}

	public void setUsuario(Vendedor usuario) {
		this.usuario = usuario;
	}

	public boolean isModoEdicion() {
		return modoEdicion;
	}

	public void setModoEdicion(boolean modoEdicion) {
		this.modoEdicion = modoEdicion;
	}

	
	//variante section
	
	
	public void llenarCampos(){
		nombreProducto.setValue(model.getNombre());
		textboxCodigo.setValue(modelv.getCodigo());
		imagenes.addAll(modelv.getImagenes());
		doubleboxPrecio.setValue(modelv.getPrecio());
		intboxStock.setValue(modelv.getStock());
		categoriaSeleccionada = model.getCategoria();
		productorSeleccionado = model.getFabricante();
		comboFabricantes.setValue(productorSeleccionado.toString());
		comboCategorias.setValue(categoriaSeleccionada.toString());
		//textboxNombre.setValue(modelv.getNombre());
		ckEditor.setValue(Jsoup.parse(modelv.getDescripcion()).text());
		onCalcularTotalCaracteres();
		
	}
	
	public void inicializarModoLectura(){
		llenarCampos();
		incentivo.setValue(modelv.getIncentivo());
		totalPrecio.setValue(modelv.getIncentivo() + modelv.getPrecio());
		modoEdicion = false;
		textboxCodigo.setDisabled(true);
		comboCaracteristicas.setDisabled(true);
		botonAgregarFabricante.setDisabled(true);
		botonAgregarCategoria.setDisabled(true);
		botonGuardar.setDisabled(true);
		listboxCaracteristicas.setDisabled(true);
		nombreProducto.setDisabled(true);
		comboCaracteristicas.setDisabled(true);
		comboFabricantes.setDisabled(true);
		comboCategorias.setDisabled(true);
		imgRender.setLectura(false);
		listImagenes.setDisabled(true);
		doubleboxPrecio.setDisabled(true);
		intboxStock.setDisabled(true);
		uploadImagen.setDisabled(true);
		listitemincentivo.setVisible(usuario.getEstrategiasUtilizadas().isUtilizaIncentivos());
		listitemincentivo.setDisabled(true);
		incentivo.setReadonly(true);
		totalPrecio.setReadonly(true);
		ckEditor.setDisabled(true);
		
	}
	
	public void onUpload$uploadImagen(UploadEvent evt){
			
		if(imagenes.size() == Constantes.CANT_MAX_IMAGENES_VARIEDAD){
			throw new WrongValueException(listImagenes,"No se pueden agregar mas de 3 imágenes por producto");
		}else {
			Media media = evt.getMedia();
			Image image = new Image();
			if (media instanceof org.zkoss.image.Image) {
				image.setContent((org.zkoss.image.Image) media);
			} else {
				Messagebox.show("El archivo no es una imagen o es demasiado grande","Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			
			ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
			String path = context.getRealPath("/imagenes/");
			Imagen imagen = fileSaver.guardarImagen(path ,usuario.getUsername(),image.getContent().getName(),image.getContent().getByteData());
			imagen.setNombre(image.getContent().getName());
			imagen.setPreview(false);
			imagenes.add(imagen);
			binder.loadAll();
		}
	}
	
	public void onChanging$ckEditor(InputEvent evt) {
		Integer total = Jsoup.parse(evt.getValue()).wholeText().length();
		mensaje.setValue("Cant. carácteres: "+total+"/355");
		cantidadCaracteres.open(ckEditor,"after_end");
	}
	
	public void onCalcularTotalCaracteres() {
		Integer total = Jsoup.parse(ckEditor.getValue()).wholeText().length();
		mensaje.setValue("Cant. carácteres: "+total+"/355");
		cantidadCaracteres.open(ckEditor,"after_end");
	}
	public void onCalcularTotal() {
		if(incentivo.getValue() != null) {
			totalPrecio.setValue(incentivo.getValue() + doubleboxPrecio.getValue());
		}else {
			totalPrecio.setValue(doubleboxPrecio.getValue());
		}
	}
	
	
	public void refresh() {
		this.binder.loadAll();
		
	}
	//Hacer que elimine la imagen de disco, para evitar basura.
	public void eliminarImagen(Imagen img){
		imagenes.remove(img);
		refresh();
	}
	
	
	public void descargarImagen(Imagen img) throws IOException{
		Filedownload.save(img.getPath(), null);
	}


	
	private void ejecutarValidaciones() throws IOException{
		Double precio = doubleboxPrecio.getValue();
		Double vincentivo = incentivo.getValue();
		Integer stock = intboxStock.getValue();
		String descripcion = Jsoup.parse(ckEditor.getValue()).text();

		if(precio == null || precio < 0){
			throw new WrongValueException(tabdetalles,"El precio no debe ser menor a 0");
		}
		
		if(vincentivo == null || vincentivo < 0){
			throw new WrongValueException(tabdetalles,"El Incentivo no debe ser negativo");
		}
		
		if(vincentivo + precio < 0){
			throw new WrongValueException(tabdetalles,"El el precio total no debe ser menor a 0");
		}
		
		if(stock == null || stock < 0){
			throw new WrongValueException(tabdetalles,"El Stock debe ser mayor a 0");
		}
		if(StringUtils.isEmpty(descripcion)){
			throw new WrongValueException(tabdescsellos,"La descripción no debe ser vacia");
		}
		
		if(descripcion.length() > 355){
			throw new WrongValueException(tabdescsellos,"La descripción es demasiado larga");
		}
		int previews = 0;
		for(Imagen i : imagenes){
			if(i.getPreview()){
				previews++;
			}
		}
		if(previews > 1){
			throw new WrongValueException(tabdetalles,"No se puede elegir mas de una imagen de previsualización");
		}
		
		
	}

	public List<Imagen> getImagenes() {
		return imagenes;
	}


	public void setImagenes(List<Imagen> imagenes) {
		this.imagenes = imagenes;
	}

	public Tab getTabdetalles() {
		return tabdetalles;
	}

	public void setTabdetalles(Tab tabdetalles) {
		this.tabdetalles = tabdetalles;
	}

	public Tab getTabdescsellos() {
		return tabdescsellos;
	}

	public void setTabdescsellos(Tab tabdescsellos) {
		this.tabdescsellos = tabdescsellos;
	}

	public Listitem getListitemincentivo() {
		return listitemincentivo;
	}

	public void setListitemincentivo(Listitem listitemincentivo) {
		this.listitemincentivo = listitemincentivo;
	}

	public Doublebox getIncentivo() {
		return incentivo;
	}

	public void setIncentivo(Doublebox incentivo) {
		this.incentivo = incentivo;
	}

	public Doublebox getTotalPrecio() {
		return totalPrecio;
	}

	public void setTotalPrecio(Doublebox totalPrecio) {
		this.totalPrecio = totalPrecio;
	}

	public Label getMensaje() {
		return mensaje;
	}

	public void setMensaje(Label mensaje) {
		this.mensaje = mensaje;
	}
	
}

class BorrarImagenEventListener implements EventListener<Event>{
	
	ABMProductoComposer composer;
	public BorrarImagenEventListener(ABMProductoComposer abmProductoComposer){
		this.composer = abmProductoComposer;
	}
	
	public void onEvent(Event event) throws Exception {
		Imagen img = (Imagen) event.getData();
		composer.eliminarImagen(img);
	}
	
}


class DescargarImagenEventListener implements EventListener<Event>{
	ABMProductoComposer composer;
	public DescargarImagenEventListener(ABMProductoComposer abmProductoComposer){
		this.composer = abmProductoComposer;
	}
	public void onEvent(Event event) throws Exception {
		Imagen img = (Imagen) event.getData();
		composer.descargarImagen(img);
		
	}
}

	

