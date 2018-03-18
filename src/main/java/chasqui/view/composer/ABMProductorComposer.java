package chasqui.view.composer;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;
import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.CaracteristicaService;
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
	private CKeditor descLarga;
	private Intbox altura;
	private Combobox comboCaracteristica;
	private CaracteristicaProductor caracteristicaSeleccionada;
	private AnnotateDataBinder binder;
	private Toolbarbutton buttonGuardar;
	private Image imagenProductor;
	private Fileupload uploadImagen;
	
	
	
	private Vendedor usuario;
	private Fabricante model;
	private UsuarioService usuarioService;
	private CaracteristicaService service;
	private FileSaver fileSaver;
	boolean existe = false;
	private List<CaracteristicaProductor>caracteristicas = new ArrayList<CaracteristicaProductor>();
	private CaracteristicaProductor caracteristicaSinDefinir = new CaracteristicaProductor();
	
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		comp.addEventListener(Events.ON_NOTIFY, new SubirArchivo(this));
		model = (Fabricante) Executions.getCurrent().getArg().get("productor");
		Integer edicion = (Integer) Executions.getCurrent().getArg().get("accion");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		service = (CaracteristicaService) SpringUtil.getBean("caracteristicaService");
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
		comp.addEventListener(Events.ON_NOTIFY, new RefreshListener<Refresher>(this));
		caracteristicas = service.buscarCaracteristicasProductor();
		caracteristicaSinDefinir.setNombre("Sin Definir");
		caracteristicas.add(caracteristicaSinDefinir);
		if(model != null && Constantes.VENTANA_MODO_LECTURA.equals(edicion)){
			inicializarModoLectura();
		}else if(model != null && Constantes.VENTANA_MODO_EDICION.equals(edicion)){
			llenarCampos();
			existe =true;
		}
		
		binder = new AnnotateDataBinder(comp);
	}
	
	public void llenarCampos(){
		textboxNombreProductor.setValue(model.getNombre());
		txtPais.setValue(model.getPais());
		txtProvincia.setValue(model.getProvincia());
		txtLocalidad.setValue(model.getLocalidad());
		txtDireccion.setValue(model.getCalle());
		altura.setValue(model.getAltura());
		descCorta.setValue(model.getDescripcionCorta());
		descLarga.setValue(model.getDescripcionLarga());
		caracteristicaSeleccionada = model.getCaracteristica();
		if(caracteristicaSeleccionada != null){
			comboCaracteristica.setValue(caracteristicaSeleccionada.getNombre());			
		}else{
			comboCaracteristica.setValue(caracteristicaSinDefinir.getNombre());
		}
		imagenProductor.setSrc(model.getPathImagen());
	}
	
	public void inicializarModoLectura(){
		llenarCampos();
		comboCaracteristica.setDisabled(true);
		altura.setDisabled(true);
		descLarga.setCustomConfigurationsPath("/js/ckEditorReadOnly.js");
		descCorta.setDisabled(true);
		textboxNombreProductor.setDisabled(true);
		txtDireccion.setDisabled(true);
		txtPais.setDisabled(true);
		txtProvincia.setDisabled(true);
		txtLocalidad.setDisabled(true);
		buttonGuardar.setDisabled(true);
		uploadImagen.setDisabled(true);
	}
	
	public void onClick$buttonGuardar(){
		String productor = textboxNombreProductor.getValue();
		Integer alt = altura.getValue();
		String calle = txtDireccion.getValue();
		String pais = txtPais.getValue();
		String descorta = descCorta.getValue();
		String deslarga = descLarga.getValue();
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
		if(!existe){
			usuario.agregarProductor(model);
		}
		usuarioService.guardarUsuario(usuario);
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		this.self.detach();
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
			throw new WrongValueException(textboxNombreProductor,"El productor no debe ser vacio!");
		}
		
		if(model == null && usuario.contieneProductor(productor)){
			throw new WrongValueException(textboxNombreProductor,"El usuario: " + usuario.getUsername() + " ya tiene el productor: " + productor );
		}
		
		if(model != null && !model.getNombre().equals(productor) && usuario.contieneProductor(productor)){
			throw new WrongValueException("El usuario: " + usuario.getUsername() + " ya tiene el productor: " + productor );
		}
		
		if(productor.matches(".*[0-9].*")){
			throw new WrongValueException(textboxNombreProductor,"El productor debe ser un nombre sin numeros");
		}
		
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

		if(StringUtils.isEmpty(descCorta)){
			throw new WrongValueException("La descripción breve no debe ser vacía");
		}
		
		
		if(StringUtils.isEmpty(descLarga)){
			throw new WrongValueException("La descripción larga no debe ser vacía");
		}
		
		if(descLarga!=null && descLarga.length() > Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR){
			throw new WrongValueException("La Descripción LARGA no debe superar los "+Constantes.MAX_SIZE_DESC_LARGA_PRODUCTOR+" caracteres (actualmente tiene "+descLarga.length()+" caracteres");
		}
		
//		if(caracteristicaSeleccionada == null){
//			throw new WrongValueException(comboCaracteristica,"Debe seleccionar una caracteristica");
//		}
		
	}
	public void refresh() {
		this.binder.loadAll();
		
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
