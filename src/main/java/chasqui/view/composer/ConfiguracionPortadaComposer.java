package chasqui.view.composer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import chasqui.model.DataMultimedia;
import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.renders.ImagenRenderPortada;
import chasqui.view.renders.ImagenesRender;

@SuppressWarnings("serial")
public class ConfiguracionPortadaComposer extends GenericForwardComposer<Component>{
	private Vendedor vendedorLogueado;
	private List<Imagen> imagenesBanner;
	private List<Imagen> imagenesPortada;
	private Listbox listImagenesPortada;
	private Listbox listImagenesBanner;
	private Image imagenLogo;
	private Imagen referenciaImagen;
	private FileSaver fileSaver;
	private AnnotateDataBinder binder;
	private DataMultimedia dataMultimedia;
	private UsuarioService usuarioService;
	private Fileupload uploadImagenLogo;
	private Fileupload uploadImagenBanner;
	private Fileupload uploadImagenPortada;
	private Textbox txtPortada;
	private static final String BANNER = "banner";
	private static final String IMAGEN_PORTADA = "imagenPortada";
	private static final String LOGO = "logo";
	private static final String DEFAULT_LOGO = "/imagenes/chasqui_logo.png";
	private String folder= "/imagenes/portada/";
	private String relativePath = "/imagenes/portada/usuarios/";
	private Component window;
	private ImagenRenderPortada imgRender;
	private Toolbarbutton buttonGuardarTexto;
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		imgRender = new ImagenRenderPortada(comp,this);
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		binder = new AnnotateDataBinder(comp);
		window = (Window) comp;
		dataMultimedia = this.obtenerDataMultimedia(vendedorLogueado);
		txtPortada.setValue(dataMultimedia.getDataPortada().getTextoBienvenida());
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");
		this.llenarDatosDeImagenesDeVendedor(dataMultimedia);
		comp.addEventListener(Events.ON_CLICK, new BorrarImagenPortadaEventListener(this));
		comp.addEventListener(Events.ON_USER, new DescargarImagenPortadaEventListener(this));
		listImagenesBanner.setItemRenderer(imgRender);
		listImagenesPortada.setItemRenderer(imgRender);
		binder.loadAll();
	}
	
	private void llenarDatosDeImagenesDeVendedor(DataMultimedia data) {
		if(data != null) {
			imagenesBanner = data.getDataPortada().getImagenesDeBanner();
			imagenesPortada= data.getDataPortada().getImagenesDePortada();
			if(data.getDataPortada().getLogo() != null) {
				imagenLogo.setSrc(data.getDataPortada().getLogo().getPath());
			}else {
				imagenLogo.setSrc(DEFAULT_LOGO);
			}
		}else {
			imagenesBanner = new ArrayList<Imagen>();
			imagenesPortada= new ArrayList<Imagen>();
			imagenLogo.setSrc(DEFAULT_LOGO);
		}
		
	}

	private DataMultimedia obtenerDataMultimedia(Vendedor vendedor) {
		DataMultimedia vData;
		if(vendedor.getDataMultimedia() == null) {
			vendedor.setDataMultimedia(new DataMultimedia(vendedor.getId()));
			vData = vendedor.getDataMultimedia();
		}else {
			vData = vendedor.getDataMultimedia();
		}
		return vData;
	}
	
	public void onClick$buttonGuardarTexto() {
		this.dataMultimedia.getDataPortada().setTextoBienvenida(txtPortada.getValue());
		this.vendedorLogueado.setDataMultimedia(dataMultimedia);
		usuarioService.guardarUsuario(vendedorLogueado);
		Clients.showNotification("El texto se guardo correctamente", "info", window, "middle_center", 3000,true);
	}

	public void onUpload$uploadImagenLogo(UploadEvent evt) {
		Integer alto = 500;
		Integer ancho = 500;
		Integer kb = 1024;
		List<String> formats = new ArrayList<String>();
		formats.add("png");
		formats.add("jpg");
		formats.add("jpeg");
		if(this.validateSizeOfImageAt(alto,ancho,evt) && validateFormatAndWeigthOfImage(evt, formats,kb)) {
			this.actualizarImagen(evt,LOGO);
		}else {
			Clients.showNotification("La imagen debe tener: " +alto+"px x " +ancho+"px, debe ser formato png, jpg o jpeg y no debe pesar mas de "+ kb/1024 +"MB", "warning", window, "middle_center", 5000, true);
		}	
	}
	
	public void onUpload$uploadImagenBanner(UploadEvent evt) {
		Integer alto = 272;
		Integer ancho = 1280;
		Integer kb = 2048;
		List<String> formats = new ArrayList<String>();
		formats.add("jpg");
		formats.add("jpeg");
		formats.add("png");
		formats.add("bmp");
		if(dataMultimedia.getDataPortada().getImagenesDeBanner().size()<1) {
			if(this.validateSizeOfImageAt(alto,ancho,evt) && validateFormatAndWeigthOfImage(evt,formats,kb)) {
				this.actualizarImagen(evt,BANNER);
			}else {
				Clients.showNotification("La imagen debe tener: " +alto+"px x " +ancho+"px, debe ser de formato jpg, jpeg, png o bmp y no debe pesar mas de "+ kb /1024 +"MB", "warning", window, "middle_center", 5000, true);
			}
		}else {
			Clients.showNotification("Por favor borre la imagen primero antes de agregar una nueva", "info", window, "middle_center", 3000, true);
		}
	}
	
	public void onUpload$uploadImagenPortada(UploadEvent evt) {
		List<String> formats = new ArrayList<String>();
		Integer alto = 500;
		Integer ancho = 500;
		Integer kb = 2048;
		formats.add("jpg");
		formats.add("jpeg");
		formats.add("png");
		formats.add("bmp");
		if(dataMultimedia.getDataPortada().getImagenesDePortada().size()<1){
			if(this.validateSizeOfImageAt(alto,ancho,evt) && validateFormatAndWeigthOfImage(evt,formats,kb)) {
				this.actualizarImagen(evt,IMAGEN_PORTADA);
			}else {
				Clients.showNotification("La imagen debe tener: " +alto+"px x " +ancho+"px, debe ser de formato jpg, jpeg, png o bmp y no debe pesar mas de "+ kb/1024 +"MB", "warning", window, "middle_center", 5000, true);
			}
		}else {
			Clients.showNotification("Por favor borre la imagen primero antes de agregar una nueva", "info", window, "middle_center", 3000, true);
		}		
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

	private boolean validateSizeOfImageAt(int h, int w, UploadEvent evt) {
		boolean ret = false;
        org.zkoss.util.media.Media media = evt.getMedia();
        if (media instanceof org.zkoss.image.Image) {
            org.zkoss.image.Image img = (org.zkoss.image.Image) media;
            if (img.getHeight() <= h && img.getWidth() <= w){
            	ret = true;
            }
        }
		return ret;
	}

	
	public void actualizarImagen(UploadEvent evt,String tipo){
		
		try{
			Media media = evt.getMedia();
			Image image = new Image();
			if (media instanceof org.zkoss.image.Image) {
				image.setContent((org.zkoss.image.Image) media);
			} else {
				Messagebox.show("El archivo no es una imagen o es demasiado grande","Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			
			if(!nombreDeImagenRepetida(image.getContent().getName())) {
				ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
				String path = context.getRealPath(folder);
				referenciaImagen = fileSaver.guardarImagenConPathRelativeDinamico(path +"/",relativePath,vendedorLogueado.getUsername(),image.getContent().getName(),image.getContent().getByteData());
				guardarImagenEnVendedorDe(tipo);
			}else {
				Clients.showNotification("El nombre "+ image.getContent().getName() +" de la imagen ya existe", "info", window, "middle_center", 5000, true);
				return;
			}
		}catch(Exception e){
			Messagebox.show("Ha ocurrido un error al subir la imagen","Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}finally{
			Clients.clearBusy();
			binder.loadAll();
		}
	}

	private boolean nombreDeImagenRepetida(String name) {
		boolean ret = false;
		for(Imagen imagenBanner: imagenesBanner) {
			if(!ret) {
				ret = name.equals(imagenBanner.getNombre());
			}
		}
		for(Imagen imagenHeader: imagenesPortada) {
			if(!ret) {
				ret = name.equals(imagenHeader.getNombre());
			}
		}
		if(!ret) {
			ret = name.equals(vendedorLogueado.getDataMultimedia().getDataPortada().getLogo().getNombre());
		}
		return ret;
	}

	private void guardarImagenEnVendedorDe(String tipo) {
		Imagen oldLogo = null;
		switch(tipo) {
			case LOGO:
				oldLogo = this.dataMultimedia.getDataPortada().getLogo();
				this.dataMultimedia.getDataPortada().setLogo(referenciaImagen);
				imagenLogo.setSrc(dataMultimedia.getDataPortada().getLogo().getPath());
				break;
			case BANNER:
				this.dataMultimedia.getDataPortada().getImagenesDeBanner().add(referenciaImagen);
			   	break;
			case IMAGEN_PORTADA:
				this.dataMultimedia.getDataPortada().getImagenesDePortada().add(referenciaImagen);
				break;
			default: break;
		}		
		this.vendedorLogueado.setDataMultimedia(dataMultimedia);
		usuarioService.guardarUsuario(vendedorLogueado);
		if(oldLogo != null) {
			fileSaver.borrarImagenEnCarpeta(oldLogo.getAbsolutePath());
		}
		this.referenciaImagen = null;
		Clients.showNotification("La imagen fue guardada correctamente", "info", window, "middle_center", 3000,true);
	}

	public Vendedor getVendedorLogueado() {
		return vendedorLogueado;
	}

	public void setVendedorLogueado(Vendedor vendedorLogueado) {
		this.vendedorLogueado = vendedorLogueado;
	}

	public List<Imagen> getImagenesBanner() {
		return imagenesBanner;
	}

	public void setImagenesBanner(List<Imagen> imagenesBanner) {
		this.imagenesBanner = imagenesBanner;
	}

	public List<Imagen> getImagenesPortada() {
		return imagenesPortada;
	}

	public void setImagenesPortada(List<Imagen> imagenesPortada) {
		this.imagenesPortada = imagenesPortada;
	}

	public Image getImagenLogo() {
		return imagenLogo;
	}

	public void setImagenLogo(Image imagenLogo) {
		this.imagenLogo = imagenLogo;
	}

	public Fileupload getUploadImagenLogo() {
		return uploadImagenLogo;
	}

	public void setUploadImagenLogo(Fileupload uploadImagenLogo) {
		this.uploadImagenLogo = uploadImagenLogo;
	}

	public Fileupload getUploadImagenBanner() {
		return uploadImagenBanner;
	}

	public void setUploadImagenBanner(Fileupload uploadImagenBanner) {
		this.uploadImagenBanner = uploadImagenBanner;
	}

	public Fileupload getUploadImagenPortada() {
		return uploadImagenPortada;
	}

	public void setUploadImagenPortada(Fileupload uploadImagenPortada) {
		this.uploadImagenPortada = uploadImagenPortada;
	}

	public Textbox getTxtPortada() {
		return txtPortada;
	}

	public void setTxtPortada(Textbox txtPortada) {
		this.txtPortada = txtPortada;
	}
	
	public void eliminarImagen(final Imagen img) {
		
		if(img!=null) {
			Messagebox.show(
					"¿Esta seguro que desea eliminar la imagen" + img.getNombre() + " ?",
					"Pregunta",
		    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
		    		new String[] {"Aceptar","Cancelar"},
		    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

				public void onEvent(ClickEvent event) throws Exception {
					String edata= event.getData().toString();
					switch (edata){
					case "YES":
						try {
							imagenesBanner.remove(img);
							imagenesPortada.remove(img);
							usuarioService.guardarUsuario(vendedorLogueado);
							fileSaver.borrarImagenEnCarpeta(img.getAbsolutePath());
							Clients.showNotification("La imagen "+ img.getNombre() + " se eliminó correctamente", "info", window, "middle_center", 3000,true);
							refresh();
						} catch (Exception e) {
							Clients.showNotification("Ocurrio un error desconocido", "error", window, "middle_center", 3000, true);
							e.printStackTrace();						
						}
						break;
					case "ABORT":
					}
				}
				});
			refresh();
		}
	}
	public void descargarImagen(Imagen img) throws FileNotFoundException {
		Filedownload.save(img.getPath(), null);
	}
	
	public void refresh() {
		this.binder.loadAll();
		
	}

}

class BorrarImagenPortadaEventListener implements EventListener<Event>{
	
	ConfiguracionPortadaComposer composer;
	public BorrarImagenPortadaEventListener(ConfiguracionPortadaComposer configuracionPortadaComposer){
		this.composer = configuracionPortadaComposer;
	}
	
	public void onEvent(Event event) throws Exception {
		Imagen img = (Imagen) event.getData();
		composer.eliminarImagen(img);
	}
	
}


class DescargarImagenPortadaEventListener implements EventListener<Event>{
	ConfiguracionPortadaComposer composer;
	public DescargarImagenPortadaEventListener(ConfiguracionPortadaComposer configuracionPortadaComposer){
		this.composer = configuracionPortadaComposer;
	}
	public void onEvent(Event event) throws Exception {
		Imagen img = (Imagen) event.getData();
		composer.descargarImagen(img);
		
	}
}
