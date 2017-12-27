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
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.ZonaService;

@SuppressWarnings({"deprecation","unused"})
public class ZonasComposer extends GenericForwardComposer<Component> {

	
	private Image imgMapa;
	private AnnotateDataBinder binder;
	private Textbox nombreZona;
	private Button btnGuardar;
	private Button btnLimpiar;
	private Button guardar;
	private Button cancelar;
	private Fileupload uploadImagen;
	private Datebox fechaCierrePedidos;
	private Textbox txtDescripcion;
	
	private Zona zonaSeleccionada;
	private List<Zona> zonas;
	private Vendedor usuario;
	private FileSaver fileSaver;
	private ZonaService zonaService;
	private UsuarioService usuarioService;
	
	
	
	
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
		binder = new AnnotateDataBinder(c);
		binder.loadAll();
	}

	public void onEliminarZona(){
		Messagebox.show("Está seguro que desea eliminar la zona seleccionada?","Pregunta",Messagebox.YES,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					zonas.remove(zonaSeleccionada);
					zonaService.borrar(zonaSeleccionada);
					zonaSeleccionada = null;
					binder.loadAll();
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
		if(zonaSeleccionada == null){
			validarZona(zona,fechaCierre,msg);
			DateTime fecha = new DateTime();
			Zona z = new Zona(zona,fecha,msg);
			z.setNombre(zona);
			z.setIdVendedor(usuario.getId());
			z.setFechaCierrePedidos(fecha);
			z.setDescripcion(msg);		
			zonas.add(z);			
		}else{
			zonaSeleccionada.editar(msg,fechaCierre,zona);
		}
		limpiarCampos();
		zonaSeleccionada = null;
		this.binder.loadAll();
		
	}
	
	
	public void onEditarZona(){
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
	
	
	private boolean estaEnLista(String zona){
		for(Zona z : zonas){
			if(z.getNombre().equalsIgnoreCase(zona)){
				return true;
			}
		}
		return false;
	}
	
	
	public void onClick$guardar(){
		for(Zona z :zonas){
			zonaService.guardar(z);
		}
		usuarioService.guardarUsuario(usuario);
		alert("Se ha guardado correctamente");
		this.self.detach();
	}
	
	public void onClick$cancelar(){
		this.self.detach();
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
