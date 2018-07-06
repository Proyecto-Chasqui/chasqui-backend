package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.cxf.common.util.StringUtils;
import org.joda.time.DateTime;
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
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import chasqui.model.Imagen;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.ZonaService;
import chasqui.utils.TokenGenerator;

@SuppressWarnings({"deprecation","unused"})
public class ZonasComposer extends GenericForwardComposer<Component> {

	
	private Image imgMapa;
	private AnnotateDataBinder binder;
	private Textbox nombreZona;
	private Button btnAgregar;
	private Button btnGuardar;
	private Button btnLimpiar;
	private Button guardar;
	private Button cancelar;
	private Fileupload uploadImagen;
	private Datebox fechaCierrePedidos;
	private Textbox txtDescripcion;
	private Zona zonacreada;
	
	private Zona zonaSeleccionada;
	private List<Zona> zonas;
	private Vendedor usuario;
	private FileSaver fileSaver;
	private ZonaService zonaService;
	private UsuarioService usuarioService;
	private Iframe mapFrame;
	private TokenGenerator tokenGenerator;
	
	
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
		zonaService = (ZonaService) SpringUtil.getBean("zonaService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		c.addEventListener(Events.ON_NOTIFY,new SubirImagenListener(this));
		zonas = zonaService.buscarZonasBy(usuario.getId());
		if(usuario.getMapaZonas() != null){
			imgMapa.setSrc(usuario.getMapaZonas());			
		}
		tokenGenerator = (TokenGenerator) SpringUtil.getBean("tokenGenerator");
		String selfurl = "http://localhost:8080/map";//"http://" + Executions.getCurrent().getServerName() + ":" + Executions.getCurrent().getServerPort()+"/map/";
		mapFrame.setSrc(selfurl+"?token="+tokenGenerator.generarTokenParaVendedor(usuario.getId()));
		binder = new AnnotateDataBinder(c);
		binder.loadAll();
	}

	public void onEliminarZona(){
		Messagebox.show("Está seguro que desea eliminar la zona seleccionada?","Pregunta",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					zonas.remove(zonaSeleccionada);
					usuario.eliminarZona(zonaSeleccionada);
					zonaSeleccionada = null;
					usuarioService.guardarUsuario(usuario);
					binder.loadAll();
				case Messagebox.NO:
				}
			}

			
			});
		this.binder.loadAll();
	}
	
	
	public void onClick$btnLimpiar(){
		limpiarCampos();
		this.binder.loadAll();
	}
	
	private void limpiarCampos(){
		txtDescripcion.setValue(null);
		fechaCierrePedidos.setValue(null);
		nombreZona.setValue(null);
	}

	public void onClick$btnAgregar(){
		String zona = nombreZona.getValue();
		Date fechaCierre = fechaCierrePedidos.getValue();
		String msg = txtDescripcion.getValue();
		validarZona(zona,fechaCierre,msg);
		crearZona();
		if(zonaSeleccionada == null){
			Messagebox.show("¿Está seguro que desea agregar la zona " +zona+" ?","Pregunta",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
					new EventListener<Event>(){

				public void onEvent(Event event) throws Exception {
					switch (((Integer) event.getData()).intValue()){
					case Messagebox.YES:									
						usuario.agregarZona(zonacreada);
						usuarioService.guardarUsuario(usuario);
						zonacreada = null;
						zonas = zonaService.buscarZonasBy(usuario.getId());
						binder.loadAll();
					case Messagebox.NO:
					}				
				}

				});
			
			

		}else{
			zonaSeleccionada.editar(msg,fechaCierre,zona);
			zonaService.guardar(zonaSeleccionada);
			btnAgregar.setLabel("Agregar");
			btnLimpiar.setVisible(true);
			alert("Se ha editado correctamente la zona");
		}
		limpiarCampos();
		zonaSeleccionada = null;
		this.binder.loadAll();
		
	}
	
	public void crearZona(){
		String zona = nombreZona.getValue();
		Date fechaCierre = fechaCierrePedidos.getValue();
		String msg = txtDescripcion.getValue();
		DateTime fecha = new DateTime(fechaCierre);
		zonacreada = new Zona(zona,fecha,msg);
		zonacreada.setNombre(zona);
		zonacreada.setIdVendedor(usuario.getId());
		zonacreada.setFechaCierrePedidos(fecha);
		zonacreada.setDescripcion(msg);

	}
	
	
	public void onEditarZona(){
		btnAgregar.setLabel("Guardar Cambios");
		btnLimpiar.setVisible(false);
		txtDescripcion.setValue(zonaSeleccionada.getDescripcion());
		fechaCierrePedidos.setValue(zonaSeleccionada.getFechaCierrePedidos().toDate());
		nombreZona.setValue(zonaSeleccionada.getNombre());
		this.binder.loadAll();
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
			Imagen imagen = fileSaver.guardarImagen(path +"/",usuario.getUsername(),image.getContent().getName(),image.getContent().getByteData());
			imgMapa.setSrc(imagen.getPath());
			usuario.setMapaZonas(imagen.getPath());
		}catch(Exception e){
			Messagebox.show("Ha ocurrido un error al subir la imagen","Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}finally{
			Clients.clearBusy();
			binder.loadAll();
		}
	}
	
	
	public void onUpload$uploadImagen(UploadEvent evt){
		Clients.showBusy("Procesando...");
		Events.echoEvent(Events.ON_NOTIFY,this.self,evt);
	}
	
	
	private void validarZona(String zona, Date t,String msg){
		if(StringUtils.isEmpty(zona)){
			throw new WrongValueException(nombreZona,"La zona no debe ser vacia");
		}
		if(estaEnLista(zona)){
			throw new WrongValueException(nombreZona,"La zona: '"+zona+ "' ya se encuentra en la lista" );
		}
		if(t == null){
			throw new WrongValueException(fechaCierrePedidos, "La fecha de cierre de pedidos no debe ser vacia!");
		}
		DateTime hoy = new DateTime();
		if(hoy.isAfter(t.getTime())){
			throw new WrongValueException(fechaCierrePedidos, "La fecha de cierre de pedidos debe ser posterior a la fecha actual");
		}
		if(StringUtils.isEmpty(msg)){
			txtDescripcion.setFocus(true);
			throw new WrongValueException(txtDescripcion,"El mensaje no debe ser vacío");
		}
	}

	
	private boolean estaEditando(Integer id){
		return zonaSeleccionada != null && zonaSeleccionada.getId() == id;
	}
	
	private boolean estaEnLista(String zona){
		for(Zona z : zonas){
			if(z.getNombre().equalsIgnoreCase(zona) && ! estaEditando(z.getId())){
				return true;
			}
		}
		return false;
	}
	
	
	public Zona getZonaSeleccionada() {
		return zonaSeleccionada;
	}

	public void setZonaSeleccionada(Zona zonaSeleccionada) {
		this.zonaSeleccionada = zonaSeleccionada;
	}

	public List<Zona> getZonas() {
		return zonas;
	}

	public void setZonas(List<Zona> zonas) {
		this.zonas = zonas;
	}

	public Iframe getMapFrame() {
		return mapFrame;
	}

	public void setMapFrame(Iframe mapFrame) {
		this.mapFrame = mapFrame;
	}
	
	
	
	
	
}

class SubirImagenListener implements EventListener<Event>{

	ZonasComposer composer;
	public SubirImagenListener(ZonasComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		composer.actualizarImagen((UploadEvent)event.getData());
		
	}
	
}
