package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.cxf.common.util.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.UsuarioService;

@SuppressWarnings({"serial","deprecation"})
public class ConfiguracionComposer extends GenericForwardComposer<Component>{
	
	private Vendedor vendedorLogueado;
	private Window configuracionWindow;
	private AnnotateDataBinder binder;
	private Toolbarbutton buttonGuardar;
	private Fileupload uploadImagen;
	private Combobox comboCantidadDeKilometros;
	private Checkbox checkUtilizarMismaFecha;
	private Button zonaButton;
	private Button puntoDeRetiroButton;
	private Button configuracionDePortadaButton;
	private Listcell puntoderetiro;
	private Listcell puntoderetiroOptions;
	private Encrypter encrypter ;
	private static final String ANCHO = "ancho";
	private static final String ALTO = "alto";
//	private Datebox dateProximaEntrega;
	private Textbox textboxClaveActual;
	private Textbox textboxNuevaClaveRepita;
	private Textbox textboxNuevaClave;
	private Intbox intboxMontoMinimo;
	private List<Integer>kilometros = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15);
	private Integer kilometroSeleccionado;
	private FileSaver fileSaver;
	private UsuarioService usuarioService;
	private Imagen imagen;
	private Listitem cuestionarioitem;
	private Component component;
	
	public Listitem getCuestionarioitem() {
		return cuestionarioitem;
	}

	public void setCuestionarioitem(Listitem cuestionarioitem) {
		this.cuestionarioitem = cuestionarioitem;
	}

	public void doAfterCompose(Component comp) throws Exception{
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(vendedorLogueado != null){
			super.doAfterCompose(comp);
			imagen = new Imagen();
			component = comp;
			if(vendedorLogueado.getImagenPerfil() != null || vendedorLogueado.getImagenPerfil().equals("/imagenes/usuarios/ROOT/perfil.jpg")){
				imagen.setPath(vendedorLogueado.getImagenPerfil());				
			}else{
				imagen.setPath("/imagenes/subirImagen.png");
			}
			fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
			usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
			encrypter = (Encrypter) SpringUtil.getBean("encrypter");
			binder = new AnnotateDataBinder(comp);
			kilometroSeleccionado = vendedorLogueado.getDistanciaCompraColectiva();
			puntoderetiro.setVisible(false);
			puntoderetiroOptions.setVisible(false);
//			DateTime d = new DateTime(vendedorLogueado.getFechaCierrePedido());
//			DateTime hoy = new DateTime();
//			if(hoy.isBefore(d)){
//				d.plusMonths(1);
//				vendedorLogueado.setFechaCierrePedido(new DateTime(d.getMillis()));
//				dateProximaEntrega.setValue(new Date(d.getMillis()));
//			}else if(vendedorLogueado.getFechaCierrePedido() != null){
//				dateProximaEntrega.setValue(new Date (vendedorLogueado.getFechaCierrePedido().getMillis()));
//			}
			this.mostrarPreguntasSiTieneEstrategiaDeVentas(vendedorLogueado.getEstrategiasUtilizadas());
			intboxMontoMinimo.setValue(vendedorLogueado.getMontoMinimoPedido());
			comp.addEventListener(Events.ON_NOTIFY, new SubirArchivoListener(this));
			binder.loadAll();			
		}
	}
	
	private void mostrarPreguntasSiTieneEstrategiaDeVentas(EstrategiasDeComercializacion estrategiasDeComercializacion) {
		if(estrategiasDeComercializacion.isCompraIndividual() || estrategiasDeComercializacion.isGcc() || estrategiasDeComercializacion.isNodos()) {
			cuestionarioitem.setVisible(true);
		}else {
			cuestionarioitem.setVisible(false);
		}		
		
	}

	public void onUpload$uploadImagen(UploadEvent evt){
		Clients.showBusy("Procesando...");
		Events.echoEvent(Events.ON_NOTIFY,this.self,evt);
	}
	
	private boolean validateSizeOfImageAt(Double h, Double w, Double margenalto, String statico,UploadEvent evt) {
		boolean ret = false;
		Double baseAspectRatio = w / h;
		org.zkoss.util.media.Media media = evt.getMedia();		
        if (media instanceof org.zkoss.image.Image) {
            org.zkoss.image.Image img = (org.zkoss.image.Image) media;
    		Double imageHeight = Double.valueOf(img.getHeight());
    		Double imageWidth = Double.valueOf(img.getWidth());
            if(statico.equals(ANCHO)) {
            	if(imageHeight >= h && imageWidth <= (w+margenalto) && (imageWidth >= w)){
            		ret = true;
            	}
            }
            if(statico.equals(ALTO)) {
            	if(imageHeight <= (h+margenalto) && imageHeight>= h && imageWidth >= w){
            		ret = true;
            	}
            }
            if(baseAspectRatio != imageWidth/imageHeight) {
            	ret = false;
            }
        }
		return ret;
	}
	
	private boolean validateFormatAndWeigthOfImage(UploadEvent evt,List<String> formats, Integer imageSizeInKB) {
		boolean ret = false;
        org.zkoss.util.media.Media media = evt.getMedia();
        if (media instanceof org.zkoss.image.Image && media.getByteData().length < imageSizeInKB * 1024 && hasAValidFormat(media,formats)) {
           ret = true;
        }
		return ret;
	}
	
	private boolean hasAValidFormat(Media media, List<String> formats) {
		boolean ret = false;
		for(String format: formats) {
			if(!ret) {
				ret = media.getFormat().equals(format);
			}
		}
		return ret;
	}
	
	public void actualizarImagen(UploadEvent evt){
		try{
			Media media = evt.getMedia();
			Image image = new Image();
			Double alto = 180.0;
			Double ancho = 280.0;
			Integer kb = 512;
			Double margenalto = 180.0;
			Double margenancho = 280.0;
			List<String> formats = new ArrayList<String>();
			formats.add("jpg");
			formats.add("jpeg");
			formats.add("png");
			if (media instanceof org.zkoss.image.Image) {
				if(this.validateSizeOfImageAt(alto,ancho,margenalto,ALTO,evt) && this.validateSizeOfImageAt(alto,ancho,margenancho,ANCHO,evt) && validateFormatAndWeigthOfImage(evt,formats,kb)) {
					image.setContent((org.zkoss.image.Image) media);
				}else {
					String mensaje = "La imagen debe tener una dimensión de " +ancho.intValue()+"px x " +alto.intValue()+" px, hasta "+ (ancho.intValue()+margenancho.intValue()) +" px x "+(alto.intValue()+margenalto.intValue())+" px, debe tener propocion 14:9 y ser de formato jpg, jpeg o png y no debe pesar mas de "+ kb +"KB";
					Clients.showNotification(mensaje, "warning", component, "middle_center", 10000, true);
					return;
				}
			} else {
				Clients.showNotification("El archivo no se pudo procesar correctamente o no se trata de una imagen, reintente subirla, si el problema persiste consulte con el administrador.", "error", component, "middle_center", 10000, true);
			}
			ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
			String path = context.getRealPath("/imagenes/");
			imagen = fileSaver.guardarImagen(path +"/",vendedorLogueado.getUsername(),image.getContent().getName(),image.getContent().getByteData());
			vendedorLogueado.setImagenPerfil(imagen.getPath());
			usuarioService.guardarUsuario(vendedorLogueado);
			Clients.showNotification("La imagen se guardó correctamente", "info", component, "middle_center", 3000,true);
		}catch(Exception e){
			Clients.showNotification("Ocurrió un error inesperado al tratar de agregar la imagen", "error", component, "middle_center", 3000,true);
			e.printStackTrace();
		}finally{
			Clients.clearBusy();
			binder.loadAll();
		}
	}
	
	
	public void validarPassword() throws Exception{
		String claveActual = textboxClaveActual.getValue();
		String nuevaClave = textboxNuevaClave.getValue();
		String nuevaClaveRepita = textboxNuevaClaveRepita.getText();
		boolean clavesNuevasNoEmpty = (!StringUtils.isEmpty(nuevaClave) || !StringUtils.isEmpty(nuevaClaveRepita));
		if(StringUtils.isEmpty(claveActual) && clavesNuevasNoEmpty){
			throw new WrongValueException(textboxClaveActual,"Por favor introduzca su contraseña actual, si desea actualizarla");
		}
		
		if(!StringUtils.isEmpty(nuevaClave) && !StringUtils.isEmpty(nuevaClaveRepita) && !nuevaClave.equals(nuevaClaveRepita)){
			WrongValueException e1 = new WrongValueException(textboxNuevaClave,"Las contraseñas no coinciden!");
			WrongValueException e2 = new WrongValueException(textboxNuevaClaveRepita,"Las contraseñas no coinciden!");
			throw new WrongValuesException(new WrongValueException[] {e1,e2});
		}
		if( !StringUtils.isEmpty(claveActual) && !claveActual.equals(encrypter.decrypt(vendedorLogueado.getPassword()))){
			throw new WrongValueException(textboxClaveActual,"La contraseña actual es incorrecta!");			
		}
		
		if(!StringUtils.isEmpty(nuevaClave) && (!nuevaClave.matches("^[a-zA-Z0-9]*$")|| nuevaClave.length() < 8)){
		  throw new WrongValueException(textboxNuevaClave,"La nueva contraseña no cumple con los requisitos!");
		}
		
		if(clavesNuevasNoEmpty){
			vendedorLogueado.setPassword(encrypter.encrypt(nuevaClave));
		}
		
		
	}
	
	public void validacionesDeCompra(){
		//Date date = dateProximaEntrega.getValue();
		Integer monto = intboxMontoMinimo.getValue();

//		if(date==null){
//			throw new WrongValueException(dateProximaEntrega,"La fecha de proxima entrega no puede estar vacía!");
//		}
		if(monto == null || monto < 0){
			throw new WrongValueException(intboxMontoMinimo,"El monto no debe ser menor a 0!");
		}
	//		if(date.before(new Date())){
	//			throw new WrongValueException(dateProximaEntrega,"La fecha de proxima entrega debe ser posterior a la fecha actual!");
	//		}
		
	}
	
	
	public void onClick$zonaButton(){
		Window w = (Window) Executions.createComponents("/zona.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$puntoDeRetiroButton(){
		Window w = (Window) Executions.createComponents("/puntoDeRetiro.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$cuestionarioButton(){
		Window w = (Window) Executions.createComponents("/abmCuestionario.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$configuracionDePortadaButton(){
		Window w = (Window) Executions.createComponents("/configuracionPortada.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$configPropsButton(){
		Window w = (Window) Executions.createComponents("/configuracionPropiedadesVendedor.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$configVentasButton(){
		Window w = (Window) Executions.createComponents("/configuracionDeVentas.zul", this.self, null);
		w.doModal();
	}
	
	public void onClick$buttonGuardarMontoMinimo() {
		validacionesDeCompra();
		try {
			vendedorLogueado.setMontoMinimoPedido(intboxMontoMinimo.getValue());
			usuarioService.guardarUsuario(vendedorLogueado);
			Clients.showNotification("El monto mínimo se guardó correctamente", "info", component, "middle_center", 3000,true);		
		}catch (Exception e) {
			Clients.showNotification("Ocurrio un error inesperado al tratar de guardar el monto mínimo", "error", component, "middle_center", 3000,true);
		}
	}
	
	public void sincWithBD() throws VendedorInexistenteException {
		Vendedor user =(Vendedor) usuarioService.obtenerUsuarioPorID(vendedorLogueado.getId());
		usuarioService.inicializarListasDe(user);
		Executions.getCurrent().getSession().setAttribute(Constantes.SESSION_USERNAME, user);
		vendedorLogueado = user;
	}
	
	public void onClick$buttonGuardar() throws Exception{
		validarPassword();
		sincWithBD();
		//Date d = dateProximaEntrega.getValue();
		//vendedorLogueado.setDistanciaCompraColectiva(kilometroSeleccionado);
		//vendedorLogueado.setFechaCierrePedido(new DateTime(d.getTime()));
		//vendedorLogueado.setImagenPerfil(imagen.getPath());
		usuarioService.guardarUsuario(vendedorLogueado);
		textboxClaveActual.setValue(null);
		textboxNuevaClave.setValue(null);
		textboxNuevaClaveRepita.setValue(null);
		Messagebox.show("Los cambios se ha guardado correctamente","Información",Messagebox.OK,Messagebox.INFORMATION);
		this.binder.loadAll();
	}
	

	public Vendedor getUsuarioLogueado() {
		return vendedorLogueado;
	}

	public void setUsuarioLogueado(Vendedor usuarioLogueado) {
		this.vendedorLogueado = usuarioLogueado;
	}
	public Window getConfiguracionWindow() {
		return configuracionWindow;
	}
	public void setConfiguracionWindow(Window configuracionWindow) {
		this.configuracionWindow = configuracionWindow;
	}
	public AnnotateDataBinder getBinder() {
		return binder;
	}
	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public List<Integer> getKilometros() {
		return kilometros;
	}

	public void setKilometros(List<Integer> kilometros) {
		this.kilometros = kilometros;
	}

	public Integer getKilometroSeleccionado() {
		return kilometroSeleccionado;
	}

	public void setKilometroSeleccionado(Integer kilometroSeleccionado) {
		this.kilometroSeleccionado = kilometroSeleccionado;
	}

	public Toolbarbutton getButtonGuardar() {
		return buttonGuardar;
	}
	
	public Fileupload getUploadImagen() {
		return uploadImagen;
	}

	public void setUploadImagen(Fileupload uploadImagen) {
		this.uploadImagen = uploadImagen;
	}

	public void setButtonGuardar(Toolbarbutton buttonGuardar) {
		this.buttonGuardar = buttonGuardar;
	}

	public Combobox getComboCantidadDeKilometros() {
		return comboCantidadDeKilometros;
	}
	public void setComboCantidadDeKilometros(Combobox comboCantidadDeKilometros) {
		this.comboCantidadDeKilometros = comboCantidadDeKilometros;
	}
	public Checkbox getCheckUtilizarMismaFecha() {
		return checkUtilizarMismaFecha;
	}
	public void setCheckUtilizarMismaFecha(Checkbox checkUtilizarMismaFecha) {
		this.checkUtilizarMismaFecha = checkUtilizarMismaFecha;
	}
//	public Datebox getDateProximaEntrega() {
//		return dateProximaEntrega;
//	}
//	public void setDateProximaEntrega(Datebox dateProximaEntrega) {
//		this.dateProximaEntrega = dateProximaEntrega;
//	}
	public Textbox getTextboxClaveActual() {
		return textboxClaveActual;
	}
	public void setTextboxClaveActual(Textbox textboxClaveActual) {
		this.textboxClaveActual = textboxClaveActual;
	}
	public Textbox getTextboxNuevaClaveRepita() {
		return textboxNuevaClaveRepita;
	}
	public void setTextboxNuevaClaveRepita(Textbox textboxNuevaClaveRepita) {
		this.textboxNuevaClaveRepita = textboxNuevaClaveRepita;
	}
	public Textbox getTextboxNuevaClave() {
		return textboxNuevaClave;
	}
	public void setTextboxNuevaClave(Textbox textboxNuevaClave) {
		this.textboxNuevaClave = textboxNuevaClave;
	}
	public Intbox getIntboxMontoMinimo() {
		return intboxMontoMinimo;
	}
	public void setIntboxMontoMinimo(Intbox intboxMontoMinimo) {
		this.intboxMontoMinimo = intboxMontoMinimo;
	}

	public FileSaver getFileSaver() {
		return fileSaver;
	}

	public void setFileSaver(FileSaver fileSaver) {
		this.fileSaver = fileSaver;
	}

	public Imagen getImagen() {
		return imagen;
	}

	public void setImagen(Imagen imagen) {
		this.imagen = imagen;
	}


	
	
	
	

}

class SubirArchivoListener implements EventListener<Event>{
	
	ConfiguracionComposer composer;
	public SubirArchivoListener(ConfiguracionComposer c){
		this.composer = c;
	}
	public void onEvent(Event event) throws Exception {
		composer.actualizarImagen((UploadEvent)event.getData());
		
	}
}
