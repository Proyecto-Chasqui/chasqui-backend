package chasqui.view.composer;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.zkforge.ckez.CKeditor;
import org.zkoss.spring.SpringUtil;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.RefreshListener;
import chasqui.view.genericEvents.Refresher;

@SuppressWarnings({"serial","deprecation"})
public class ABMProductorComposer extends GenericForwardComposer<Component> implements Refresher{

	
	private Textbox textboxNombreProductor;
	private Textbox txtDireccion;
	private Textbox txtLocalidad;
	private Textbox txtProvincia;
	private Textbox txtPais;
	private Textbox descCorta;
	private Textbox descLarga;
	private Intbox altura;
	private Combobox comboCaracteristica;
	private CaracteristicaProductor caracteristicaSeleccionada;
	private AnnotateDataBinder binder;
	private Toolbarbutton buttonGuardar;
	private Image imagenProductor;
	private Fileupload uploadImagen;
	private ProductorService productorService;
	private Listbox listboxCaracteristicas;
	private CaracteristicaProductor caracteristicaProductorSeleccionada;
	
	private Vendedor usuario;
	private Fabricante model;
	private UsuarioService usuarioService;
	private CaracteristicaService service;
	private FileSaver fileSaver;
	boolean existe = false;
	private List<CaracteristicaProductor>caracteristicas = new ArrayList<CaracteristicaProductor>();
	private CaracteristicaProductor caracteristicaSinDefinir = new CaracteristicaProductor();
	private boolean mostrarBorrarCaract = true;
	private List<CaracteristicaProductor> caracteristicasProductor = new ArrayList<CaracteristicaProductor>();
	
	private Tab tabdetalles;
	private Tab tabdesc;
	private Tab tabimgsellos;
	
	private Popup cantidadCaracteresCorta;
	private Label mensajedesccorta;
	
	private Popup cantidadCaracteresLarga;
	private Label mensajedesclarga;
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		//hotfix (Ver de limitar desde panel);
		//textboxNombreProductor.setMaxlength(64);
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		comp.addEventListener(Events.ON_NOTIFY, new SubirArchivo(this));
		model = (Fabricante) Executions.getCurrent().getArg().get("productor");
		Integer edicion = (Integer) Executions.getCurrent().getArg().get("accion");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		service = (CaracteristicaService) SpringUtil.getBean("caracteristicaService");
		productorService = (ProductorService) SpringUtil.getBean("productorService");
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
		comp.addEventListener(Events.ON_NOTIFY, new RefreshListener<Refresher>(this));
		caracteristicas = service.buscarCaracteristicasProductor();
		if(model != null && Constantes.VENTANA_MODO_LECTURA.equals(edicion)){
			inicializarModoLectura();
		}else if(model != null && Constantes.VENTANA_MODO_EDICION.equals(edicion)){
			llenarCampos();
			existe =true;
		}
		
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}
	
	public void llenarCampos(){
		textboxNombreProductor.setValue(model.getNombre());
		txtPais.setValue(model.getPais());
		txtProvincia.setValue(model.getProvincia());
		txtLocalidad.setValue(model.getLocalidad());
		txtDireccion.setValue(model.getCalle());
		altura.setValue(model.getAltura());
		descCorta.setValue(model.getDescripcionCorta());
		descLarga.setValue(Jsoup.parse(model.getDescripcionLarga()).wholeText());
		caracteristicaSeleccionada = model.getCaracteristica();
		if(caracteristicaSeleccionada != null){
			comboCaracteristica.setValue(caracteristicaSeleccionada.getNombre());			
		}else{
			comboCaracteristica.setValue(caracteristicaSinDefinir.getNombre());
		}
		caracteristicasProductor.addAll(model.getCaracteristicas());
		imagenProductor.setSrc(model.getPathImagen());
	}
	
	public void inicializarModoLectura(){
		llenarCampos();
		setMostrarBorrarCaract(false);
		comboCaracteristica.setDisabled(true);
		altura.setDisabled(true);
		descLarga.setDisabled(true);
		descCorta.setDisabled(true);
		textboxNombreProductor.setDisabled(true);
		txtDireccion.setDisabled(true);
		txtPais.setDisabled(true);
		txtProvincia.setDisabled(true);
		txtLocalidad.setDisabled(true);
		buttonGuardar.setDisabled(true);
		uploadImagen.setDisabled(true);
	}
	
	public void onEliminarCaracteristica(){
		if(!listboxCaracteristicas.isDisabled()){			
			caracteristicasProductor.remove(caracteristicaSeleccionada);
		}
		refresh();
	}
	
	public void onSelect$comboCaracteristica(SelectEvent evt) {
		this.onClick$botonAgregarCaracteristica();
	}
	
	public void onClick$botonAgregarCaracteristica(){
		
		if(caracteristicaProductorSeleccionada == null){
			throw new WrongValueException(comboCaracteristica,"Debe seleccionar una caracteristica.");
		}
		if(caracteristicasProductor.contains(caracteristicaProductorSeleccionada)){
			throw new WrongValueException("El productor ya posee la caracteristica que desea agregar");			
		}
		caracteristicasProductor.add(caracteristicaProductorSeleccionada);
		Clients.showNotification("Agregada la caracterisitica " + caracteristicaProductorSeleccionada.getNombre(),
								"info", listboxCaracteristicas, "top_center",
								3000);
		comboCaracteristica.setValue(null);
		caracteristicaProductorSeleccionada = null;
		refresh();
		
	}
	
	public void onCalcularTotalCaracteresCorta() {
		Integer total = Jsoup.parse(descCorta.getValue()).text().length();
		if(total > 300){
			String error = "La Descripción larga no debe superar los "+300+" carácteres (actualmente tiene "+total+" carácteres)";
			Clients.showNotification(error, "error", descLarga, "middle_center", 20000, true);
		}
	}
	
	public void onCalcularTotalCaracteres() {
		Integer total = Jsoup.parse(descLarga.getValue()).text().length();
		if(total > Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR){
			String error = "La Descripción larga no debe superar los "+Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR+" carácteres (actualmente tiene "+total+" carácteres)";
			Clients.showNotification(error, "error", descLarga, "middle_center", 20000, true);
		}
	}
	
	public void onChanging$descCorta(InputEvent evt) {
		Integer total = Jsoup.parse(evt.getValue()).wholeText().length();
		mensajedesccorta.setValue("Cant. carácteres: "+total+"/300");
		cantidadCaracteresCorta.open(descCorta,"after_end");
	}
	
	
	public void onChanging$descLarga(InputEvent evt) {
		Integer total = Jsoup.parse(evt.getValue()).wholeText().length();
		mensajedesclarga.setValue("Cant. carácteres: "+total+"/8200");
		cantidadCaracteresLarga.open(descLarga,"after_end");
	}
	
	public void onClick$buttonGuardar(){
		String productor = textboxNombreProductor.getValue();
		Integer alt = altura.getValue();
		String calle = txtDireccion.getValue();
		String pais = txtPais.getValue();
		String descorta = descCorta.getValue();
		String deslarga = Jsoup.parse(descLarga.getValue()).wholeText();
		String provincia = txtProvincia.getValue();
		String localidad = txtLocalidad.getValue();
		validar(productor,alt,calle,pais,provincia,localidad,descorta,deslarga);
		if(!existe){
			model  = new Fabricante();			
		}
		model.setNombre(productor);
		if(caracteristicaSeleccionada != null){
			if(caracteristicaSeleccionada.getNombre() != "Sin Definir"){
				model.setCaracteristica(caracteristicaSeleccionada);
			}else{
				model.setCaracteristica(null);
			}
		}else{
			model.setCaracteristica(null);
		}
		model.setCalle(calle);
		model.setDescripcionCorta(descorta);
		model.setDescripcionLarga(deslarga);
		model.setAltura(alt);
		model.setPais(pais);
		model.setProvincia(provincia);
		model.setPathImagen(imagenProductor.getSrc());
		model.setLocalidad(localidad);
		model.setIdVendedor(usuario.getId());
		model.setCaracteristicas(caracteristicasProductor);
		if(!existe){
			usuario.agregarProductor(model);
			usuarioService.guardarUsuario(usuario);
		}else {
			productorService.guardar(model);
			actualizarUsuarioEnMemoria();
		}
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		this.self.detach();
	}
	
	
	private void actualizarUsuarioEnMemoria() {
		Vendedor user =(Vendedor) usuarioService.obtenerUsuarioPorID(usuario.getId());
		usuarioService.inicializarListasDe(user);
		Executions.getCurrent().getSession().setAttribute(Constantes.SESSION_USERNAME, user);
		usuario = user;
	}

	public void onUpload$uploadImagen(UploadEvent evt){
		Clients.showBusy("Procesando...");
		Events.echoEvent(Events.ON_NOTIFY,this.self,evt);
	}
	
	public void actualizarImagen(UploadEvent evt){
		try{
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
			imagenProductor.setSrc(imagen.getPath());
			binder.loadAll();			
		}catch(Exception e){
			Messagebox.show("Ha ocurrido un error al subir la imagen","Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}finally{
			Clients.clearBusy();
			binder.loadAll();
		}
	}
	
	
	
	
	
	
	public void onClick$botonCancelar(){
		this.self.detach();
	}
	public List<CaracteristicaProductor> getCaracteristicas() {
		return caracteristicas;
	}
	public void setCaracteristicas(List<CaracteristicaProductor> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	public CaracteristicaProductor getCaracteristicaSeleccionada() {
		return caracteristicaSeleccionada;
	}
	public void setCaracteristicaSeleccionada(CaracteristicaProductor caracteristicaSeleccionada) {
		this.caracteristicaSeleccionada = caracteristicaSeleccionada;
	}
	
	private void validar(String productor,Integer alt, String calle,String pais, String provincia, String localidad,String descCorta, String descLarga) {
		if(StringUtils.isEmpty(productor)){
			throw new WrongValueException(tabdetalles,"El productor no debe ser vacio!");
		}
		
		if(model == null && usuario.contieneProductor(productor)){
			throw new WrongValueException(tabdetalles,"El usuario: " + usuario.getUsername() + " ya tiene el productor: " + productor );
		}
		
		if(model != null && !model.getNombre().equals(productor) && usuario.contieneProductor(productor)){
			throw new WrongValueException("El usuario: " + usuario.getUsername() + " ya tiene el productor: " + productor );
		}
		
//		if(productor.matches(".*[0-9].*")){
//			throw new WrongValueException(textboxNombreProductor,"El productor debe ser un nombre sin numeros");
//		}
//		
//		if(StringUtils.isEmpty(pais)){
//			throw new WrongValueException(txtPais,"El pais no debe ser vacio!");
//		}
//		
//		if(StringUtils.isEmpty(provincia)){
//			throw new WrongValueException(txtProvincia,"La provincia no debe ser vacia!");
//		}
//		
//		if(StringUtils.isEmpty(localidad)){
//			throw new WrongValueException(txtLocalidad,"La localidad no debe ser vacia!");
//		}
//		
//		if(StringUtils.isEmpty(calle)){
//			throw new WrongValueException(txtDireccion,"La calle no debe ser vacía");
//		}
//		if(alt == null){
//			throw new WrongValueException(altura,"La altura no debe ser vacía");
//		}

		if(StringUtils.isEmpty(descLarga)){
			throw new WrongValueException("La descripción breve no debe ser vacía");
		}
		
		
		if(StringUtils.isEmpty(descLarga)){
			throw new WrongValueException("La descripción larga no debe ser vacía");
		}
		
		if(descLarga!=null &&  descLarga.length() > Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR){
			throw new WrongValueException("La Descripción larga no debe superar los "+Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR+" carácteres (actualmente tiene "+descLarga.length()+" carácteres");
		}
		
//		if(caracteristicaSeleccionada == null){
//			throw new WrongValueException(comboCaracteristica,"Debe seleccionar una caracteristica");
//		}
		
	}
	public void refresh() {
		this.binder.loadAll();
		
	}

	public Listbox getListboxCaracteristicas() {
		return listboxCaracteristicas;
	}

	public void setListboxCaracteristicas(Listbox listboxCaracteristicas) {
		this.listboxCaracteristicas = listboxCaracteristicas;
	}

	public CaracteristicaProductor getCaracteristicaProductorSeleccionada() {
		return caracteristicaProductorSeleccionada;
	}

	public void setCaracteristicaProductorSeleccionada(CaracteristicaProductor caracteristicaProductorSeleccionada) {
		this.caracteristicaProductorSeleccionada = caracteristicaProductorSeleccionada;
	}

	public List<CaracteristicaProductor> getCaracteristicasProductor() {
		return caracteristicasProductor;
	}

	public void setCaracteristicasProductor(List<CaracteristicaProductor> caracteristicasProductor) {
		this.caracteristicasProductor = caracteristicasProductor;
	}

	public boolean isMostrarBorrarCaract() {
		return mostrarBorrarCaract;
	}

	public void setMostrarBorrarCaract(boolean mostrarBorrarCaract) {
		this.mostrarBorrarCaract = mostrarBorrarCaract;
	}

	public Tab getTabdetalles() {
		return tabdetalles;
	}

	public void setTabdetalles(Tab tabdetalles) {
		this.tabdetalles = tabdetalles;
	}

	public Tab getTabdesc() {
		return tabdesc;
	}

	public void setTabdesc(Tab tabdesc) {
		this.tabdesc = tabdesc;
	}

	public Tab getTabimgsellos() {
		return tabimgsellos;
	}

	public void setTabimgsellos(Tab tabimgsellos) {
		this.tabimgsellos = tabimgsellos;
	}

	public Popup getCantidadCaracteresCorta() {
		return cantidadCaracteresCorta;
	}

	public void setCantidadCaracteresCorta(Popup cantidadCaracteresCorta) {
		this.cantidadCaracteresCorta = cantidadCaracteresCorta;
	}

	public Label getMensajedesccorta() {
		return mensajedesccorta;
	}

	public void setMensajedesccorta(Label mensajedesccorta) {
		this.mensajedesccorta = mensajedesccorta;
	}

	public Popup getCantidadCaracteresLarga() {
		return cantidadCaracteresLarga;
	}

	public void setCantidadCaracteresLarga(Popup cantidadCaracteresLarga) {
		this.cantidadCaracteresLarga = cantidadCaracteresLarga;
	}

	public Label getMensajedesclarga() {
		return mensajedesclarga;
	}

	public void setMensajedesclarga(Label mensajedesclarga) {
		this.mensajedesclarga = mensajedesclarga;
	}


}

class SubirArchivo implements EventListener<Event>{
	
	ABMProductorComposer composer;
	public SubirArchivo(ABMProductorComposer c){
		this.composer = c;
	}
	public void onEvent(Event event) throws Exception {
		composer.actualizarImagen((UploadEvent)event.getData());
		
	}
}
