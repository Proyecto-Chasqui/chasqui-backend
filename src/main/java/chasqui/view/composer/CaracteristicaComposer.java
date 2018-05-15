package chasqui.view.composer;

import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Imagen;
import chasqui.model.Vendedor;
import chasqui.services.impl.CaracteristicaServiceImpl;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.ICaracteristica;
import chasqui.view.renders.CaracteristicaProductorRenderer;
import chasqui.view.renders.CaracteristicaRenderer;

@SuppressWarnings({ "deprecation", "serial" })
public class CaracteristicaComposer extends GenericForwardComposer<Component> {

	private AnnotateDataBinder binder;
	private Image imgIcon;
	private Listbox ltbCaracteristicas;
	private Textbox txtbCaracteristica;

	private FileSaver fileSaver;
	private Vendedor usuario;
	private Imagen imagen;
	private List<Caracteristica> caracteristicas;

	// private Button agregarProductor;
	private Listbox ltbCaracteristicasProductor;
	private List<CaracteristicaProductor> caracteristicasProductor;
	// private Fileupload uploadImagenProductor;
	private Image imgIconProductor;
	private Imagen imagenProductor;
	private Textbox txtbCaracteristicaProductor;

	private Tab tabProducto;
	private Tab tabProductor;
	Boolean ventanaProductor;
	Boolean ventanaProducto;

	CKeditor ckEditor;
	CKeditor ckEditorProductor;

	ICaracteristica caracteristicaAEditar;
	private Component window;

	private CaracteristicaService service;

	public void doAfterCompose(Component c) throws Exception {
		super.doAfterCompose(c);
		this.window = c;
		

		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		binder = new AnnotateDataBinder(c);
		service = (CaracteristicaService) SpringUtil.getBean("caracteristicaService");

		ventanaProductor = (Boolean) Executions.getCurrent().getArg().get(Constantes.VENTANA_PRODUCTOR);
		ventanaProducto = (Boolean) Executions.getCurrent().getArg().get(Constantes.VENTANA_PRODUCTO);

		if (ventanaProductor != null) {
			tabProducto.setDisabled(true);
			tabProductor.setFocus(true);
		}

		if (ventanaProducto != null) {
			tabProductor.setDisabled(true);
			tabProducto.setFocus(true);
		}

		caracteristicas = service.buscarCaracteristicasProducto();
		caracteristicasProductor = service.buscarCaracteristicasProductor();
		ltbCaracteristicas.setItemRenderer(new CaracteristicaRenderer((Window) c, false));
		ltbCaracteristicasProductor.setItemRenderer(new CaracteristicaProductorRenderer((Window) c, false));
		fileSaver = (FileSaver) SpringUtil.getBean("fileSaver");

		c.addEventListener(Events.ON_NOTIFY, new ArchivoListener(this));
		c.addEventListener(Events.ON_UPLOAD, new ArchivoListener(this));
		c.addEventListener(Events.ON_USER, new ArchivoListener(this));
		c.addEventListener(Events.ON_OK, new ArchivoListener(this));
		this.binder.loadAll();
	}

	/*
	 * Evento disparado por el boton Agregar (Producto)
	 */
	public void onClick$agregarCaracteristicaProducto() {
		String nombreCarac = txtbCaracteristica.getValue();
		String descripcion = ckEditor.getValue();
		if (caracteristicaAEditar != null) {
			caracteristicaAEditar.setDescripcion(descripcion);
			caracteristicaAEditar.setNombre(nombreCarac);
			caracteristicaAEditar.setPathImagen(imagen.getPath());
		} else {
			validarNombreDeCaracteristicaProductor(nombreCarac);
			validarCaracteristica(nombreCarac, descripcion, imagen);
			Caracteristica c = new Caracteristica();
			c.setNombre(nombreCarac);
			c.setEliminada(false);
			c.setPathImagen(imagen.getPath());
			c.setDescripcion(descripcion);
			caracteristicas.add(c);
		}
		Clients.showNotification("Agregada la caracterisitica, debe guardarla " , "info", window, "middle_center",
				3000, true);
		txtbCaracteristica.setValue(null);
		imagen = null;
		imgIcon.setSrc(null);
		ckEditor.setValue(null);
		caracteristicaAEditar = null;
		this.binder.loadAll();
	}

	private void validarNombreDeCaracteristicaProducto(String nombre) {
		if(this.existeUnaCaracteristicaProductoConElMismoNombre(nombre)){
			throw new WrongValueException(txtbCaracteristica, "Ya existe una caracteristica con el nombre ingresado");
		}		
	}

	private boolean existeUnaCaracteristicaProductoConElMismoNombre(String nombre) {
		return service.existeCaracteristicaProductoConNombre(nombre);
	}

	/*
	 * Evento disparado por el boton Agregar (Productor)
	 */
	public void onClick$agregarCaracteristicaProductor() {
		String nombreCarac = txtbCaracteristicaProductor.getValue();
		String descripcion = ckEditorProductor.getValue();
		if (caracteristicaAEditar != null) {
			caracteristicaAEditar.setDescripcion(descripcion);
			caracteristicaAEditar.setNombre(nombreCarac);
			caracteristicaAEditar.setPathImagen(imagenProductor.getPath());
		} else {
			validarNombreDeCaracteristicaProductor(nombreCarac);
			validarCaracteristica(nombreCarac, descripcion, imagenProductor);
			CaracteristicaProductor c = new CaracteristicaProductor();
			c.setNombre(nombreCarac);
			c.setEliminada(false);
			c.setPathImagen(imagenProductor.getPath());
			c.setDescripcion(descripcion);
			caracteristicasProductor.add(c);
		}
		Clients.showNotification("Agregada la caracterisitica, debe guardarla " , "info", window, "middle_center",
				3000, true);
		txtbCaracteristicaProductor.setValue(null);
		ckEditorProductor.setValue(null);
		imagenProductor = null;
		imgIconProductor.setSrc(null);
		caracteristicaAEditar = null;
		this.binder.loadAll();
	}

	/*
	 * Valida que los campos Nombre y Descripcion sean correctos
	 */
	private void validarCaracteristica(String nombre, String descripcion, Imagen img) {
		if (StringUtils.isEmpty(nombre)) {
			throw new WrongValueException(txtbCaracteristicaProductor,
					"El nombre de la caracteristica no debe ser vacia");
		}
		if (img == null) {
			throw new WrongValueException(imgIconProductor, "Debe asociar un icono a la caracteristica");
		}

		if (StringUtils.isEmpty(descripcion)) {
			throw new WrongValueException(ckEditorProductor, "Debe Agregar una descripcion");
		}
		if (descripcion.length() > 4096) {
			throw new WrongValueException(ckEditorProductor, "La Descripción es demasiado larga");
		}
	}
	
	private void validarNombreDeCaracteristicaProductor(String nombre){
		if(this.existeUnaCaracteristicaProductorConElMismoNombre(nombre)){
			throw new WrongValueException(txtbCaracteristicaProductor, "Ya existe una caracteristica con el nombre ingresado");
		}
	}

	private boolean existeUnaCaracteristicaProductorConElMismoNombre(String nombre) {
		return service.existeCaracteristicaProductorConNombre(nombre);
	}

	/*
	 * Evento disparado por la selección de una imagen de una caracteristica del
	 * Productor
	 */
	private void validarCaracteristica() {
		String c = txtbCaracteristica.getValue();
		String descrpcion = ckEditor.getValue();

		if (StringUtils.isEmpty(c)) {
			throw new WrongValueException(txtbCaracteristica, "La caracteristica del productor no debe ser vacia");
		}
		if (imagen == null) {
			throw new WrongValueException(imgIcon, "Debe asociar un icono a la caracteristica");
		}

		if (StringUtils.isEmpty(descrpcion)) {
			throw new WrongValueException(ckEditor, "Debe Agregar una descripcion");
		}

		if (descrpcion.length() > 4096) {
			throw new WrongValueException(ckEditor, "La descripción es demasiado larga");
		}
	}

	public void onUpload$uploadImagenProductor(UploadEvent evt) {
		Clients.showBusy("Procesando...");
		Events.echoEvent(Events.ON_UPLOAD, this.self, evt);
	}

	/*
	 * Evento disparado por la selección de una imagen de una caracteristica del
	 * Producto
	 */
	public void onUpload$uploadImagen(UploadEvent evt) {
		Clients.showBusy("Procesando...");
		Events.echoEvent(Events.ON_NOTIFY, this.self, evt);
	}

	public void onClick$guardarCaracteristicaProductor() {
		Clients.showBusy("Guardando...");
		Events.echoEvent(Events.ON_OK, this.self, "guardarCaracteristicaProductor");
	}

	public void guardarCaracteristicaProductor() {
		service.guardarCaracteristicaProductor(caracteristicasProductor);
		if (ventanaProductor != null) {
			Events.sendEvent(Events.ON_NOTIFY, this.self.getParent(), null);
			this.self.detach();
		}
		Clients.clearBusy();
		Messagebox.show("Las caracteristicas de los productores se han guardado correctamente", "Información",
				Messagebox.OK, Messagebox.INFORMATION);

	}

	/*
	 * Evento disparado por el boton Guardar (Producto)
	 */
	public void onClick$guardarCaracteristica() {
		Clients.showBusy("Guardando...");
		Events.echoEvent(Events.ON_OK, this.self, "guardar");
	}

	public void guardarCaracteristica() {
		service.guardaCaracteristicasProducto(caracteristicas);
		if (ventanaProducto != null) {
			Events.sendEvent(Events.ON_NOTIFY, this.self.getParent(), null);
			this.self.detach();
		}
		Clients.clearBusy();
		Messagebox.show("Las caracteristicas de los productos se han guardado correctamente", "Información",
				Messagebox.OK, Messagebox.INFORMATION);
	}

	// ---------------------------------Metodos para guardar imagenes y
	// recuperarlas del Filesystem
	private String getAbsolutePath() {
		ServletContext context = Sessions.getCurrent().getWebApp().getServletContext();
		String path = context.getRealPath("/imagenes");
		return path + "/";
	}

	public Image cargarImagen(UploadEvent evt) {
		Media media = evt.getMedia();

		if (media instanceof org.zkoss.image.Image) {
			Image image = new Image();
			image.setContent((org.zkoss.image.Image) media);
			return image;
		} else {
			Messagebox.show("El archivo no es una imagen o es demasiado grande", "Error", Messagebox.OK,
					Messagebox.ERROR);
			return null;
		}
	}

	public void actualizarImagenProducto(UploadEvent evt) {
		try {
			Image image = cargarImagen(evt);
			imagen = fileSaver.guardarImagen(getAbsolutePath(), usuario.getUsername(), image.getContent().getName(),
					image.getContent().getByteData());
			imgIcon.setSrc(imagen.getPath());
			this.binder.loadAll();
		} catch (Exception e) {
			Messagebox.show("Ha ocurrido un error al subir la imagen", "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		} finally {
			Clients.clearBusy();
			binder.loadAll();
		}
	}

	public void actualizarImagenProductor(UploadEvent evt) {
		try {
			Image image = cargarImagen(evt);
			imagenProductor = fileSaver.guardarImagen(getAbsolutePath(), usuario.getUsername(),
					image.getContent().getName(), image.getContent().getByteData());
			imgIconProductor.setSrc(imagenProductor.getPath());
			this.binder.loadAll();
		} catch (Exception e) {
			Messagebox.show("Ha ocurrido un error al subir la imagen", "Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		} finally {
			Clients.clearBusy();
			binder.loadAll();
		}
	}

	/*
	 * Eliminar caracteristica
	 */
	public void eliminarCaracteristica(ICaracteristica c, String tipo) {
		Messagebox.show("Está seguro de eliminar la caracteristica seleccionada??", "Advertencia",
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new DialogoBorrar(tipo, c, this));
	}

	/*
	 * Determina el tipo de caracteristica (PRODUCTO/PRODUCTOR)
	 */
	public void editarCaracteristica(ICaracteristica c, String tipo) {

		if (tipo.equals(CaracteristicaRenderer.CTE_PRODUCTO)) {

			if (imagen != null) {
				throw new WrongValueException(imgIcon, "Hay cambios pendientes");
			}

			this.cargarCaracteristicaProducto((Caracteristica) c);
			caracteristicaAEditar = (Caracteristica) c;
		} else {

			if (imagenProductor != null) {
				throw new WrongValueException(imgIconProductor, "Hay cambios pendientes");
			}

			this.cargarCaracteristicaProductor((CaracteristicaProductor) c);
			caracteristicaAEditar = (CaracteristicaProductor) c;
		}
	}

	/*
	 * Carga los datos de la caracteristica del PRODUCTOR que fue seleccionada
	 * para editarse
	 */
	private void cargarCaracteristicaProductor(CaracteristicaProductor caracteristica) {
		this.ckEditorProductor.setValue(caracteristica.getDescripcion());
		this.txtbCaracteristicaProductor.setValue(caracteristica.getNombre());
		this.imgIconProductor.setSrc(caracteristica.getPathImagen());
		String fileName = caracteristica.getPathImagen().substring(caracteristica.getPathImagen().lastIndexOf("/"));
		this.imagenProductor = fileSaver.recuperarImagen(getAbsolutePath(), usuario.getUsername(), fileName);

	}

	/*
	 * Carga los datos de la caracteristica del PRODUCTO que fue seleccionada
	 * para editarse
	 */
	private void cargarCaracteristicaProducto(Caracteristica caracteristica) {
		this.ckEditor.setValue(caracteristica.getDescripcion());
		this.txtbCaracteristica.setValue(caracteristica.getNombre());
		this.imgIcon.setSrc(caracteristica.getPathImagen());
		String fileName = caracteristica.getPathImagen().substring(caracteristica.getPathImagen().lastIndexOf("/"));
		this.imagen = fileSaver.recuperarImagen(getAbsolutePath(), usuario.getUsername(), fileName);
	}

	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public List<CaracteristicaProductor> getCaracteristicasProductor() {
		return caracteristicasProductor;
	}

	public void setCaracteristicasProductor(List<CaracteristicaProductor> caracteristicasProductor) {
		this.caracteristicasProductor = caracteristicasProductor;
	}

	public void eliminarCaracteristicaProducto(ICaracteristica caracteristica) {
		service.actualizarCaracteristica(caracteristica);
		caracteristicas.remove(caracteristica);
		binder.loadAll();
	}

	public void eliminarCaracteristicaProductor(ICaracteristica caracteristica) {
		service.actualizarCaracteristicaProductor(caracteristica);
		caracteristicasProductor.remove(caracteristica);
		binder.loadAll();
	}
}

class DialogoBorrar implements EventListener<Event> {

	String tipo;
	ICaracteristica caracteristica;
	private CaracteristicaComposer composer;

	public DialogoBorrar(String t, ICaracteristica c, CaracteristicaComposer composer) {
		this.tipo = t;
		this.caracteristica = c;
		this.composer = composer;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		switch ((Integer) (event.getData())) {
		case Messagebox.YES:
			caracteristica.setEliminada(true);
			if (tipo.equals(CaracteristicaRenderer.CTE_PRODUCTO)) {
				composer.eliminarCaracteristicaProducto(caracteristica);
			} else {
				composer.eliminarCaracteristicaProductor(caracteristica);
			}
			Messagebox.show("Las caracteristica se ha borrado correctamente", "Información", Messagebox.OK,
					Messagebox.INFORMATION);
		case Messagebox.NO:
			break;
		}

	}

}

final class ArchivoListener implements EventListener<Event> {

	CaracteristicaComposer composer;

	public ArchivoListener(CaracteristicaComposer c) {
		this.composer = c;
	}

	public void onEvent(Event event) throws Exception {

		if (event.getName().equals(Events.ON_NOTIFY)) {
			composer.actualizarImagenProducto((UploadEvent) event.getData());
		}
		if (event.getName().equals(Events.ON_UPLOAD)) {
			composer.actualizarImagenProductor((UploadEvent) event.getData());
		}
		if (event.getName().equals(Events.ON_OK)) {
			String ev = (String) event.getData();
			if ("guardar".equals(ev)) {
				composer.guardarCaracteristica();
			} else {
				composer.guardarCaracteristicaProductor();
			}
		}

		if (event.getName().equals(Events.ON_USER)) {
			Map<String, Object> param = (Map<String, Object>) event.getData();
			Object c = param.get("caracteristica");
			String accion = (String) param.get("accion");
			String tipo = (String) param.get("tipo");

			if (accion.equals("editar")) {
				composer.editarCaracteristica((ICaracteristica) c, tipo);
			}
			if (accion.equals("eliminar")) {
				composer.eliminarCaracteristica((ICaracteristica) c, tipo);

			}

		}
	}
}
