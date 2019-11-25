package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Messagebox.ClickEvent;

import chasqui.model.Tag;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.TagService;
import chasqui.services.interfaces.UsuarioService;

public class ABMTagsComposer extends GenericForwardComposer<Component>{
		//Variables generales
		Vendedor usuario;
		
		UsuarioService usuarioService;
		private AnnotateDataBinder binder;
		private Textbox textNombre;
		private Textbox textDescripcion;
		private Div crearTag;
		private Div configuracionTags;
		private TagService tagService;
		private Component component;
		//Ssecciónde  variables de tag Tipo productos
		private List<TagTipoProducto> tagsTipoProductos;
		private TagTipoProducto tagTipoProductoSeleccionado;
		private Listbox listboxTagTipoProductoSeleccionado;
		private Button btnGuardarTagProducto;
		//sección variables de tag Zona de cobertura
		private List<TagZonaDeCobertura> tagsZonaCobertura;
		private TagZonaDeCobertura tagTagZonaCoberturaSeleccionado;
		private Listbox listboxTagZonaCobertura;
		private Button btnGuardarTagZona;
		//sección variables de tag Tipo de organización
		private List<TagTipoOrganizacion> tagsTipoOrganizacion;
		private TagTipoOrganizacion tagTipoOrganizacionSeleccionado;
		private Listbox listboxTagTipoOrganizacion;
		private Button btnGuardarTagOrganizacion;
		
		public void doAfterCompose(Component comp) throws Exception{
			super.doAfterCompose(comp);
			binder = new AnnotateDataBinder(comp);
			component = comp;
			tagService = (TagService) SpringUtil.getBean("tagService");
			tagsTipoProductos = tagService.obtenerTagsTipoProducto();
			tagsZonaCobertura = tagService.obtenerTagZonaDeCobertura();
			tagsTipoOrganizacion = tagService.obtenerTagTipoOrganizacion();
			binder.loadAll();
		}
		//[reload] metodos de gestion de visibilidad y carga de datos
		private void hideAllSaveButtons() {
			btnGuardarTagProducto.setVisible(false);
			btnGuardarTagZona.setVisible(false);
			btnGuardarTagOrganizacion.setVisible(false);
		}
		
		private void limpiarCampos() {
			textNombre.setValue("");
			textDescripcion.setValue("");
			flushSelected();
		}
		
		private void flushSelected() {
			tagTipoProductoSeleccionado = null;
			tagTagZonaCoberturaSeleccionado = null;
			tagTipoOrganizacionSeleccionado = null;
		}
		
		private void switchVisible() {
			crearTag.setVisible(!crearTag.isVisible());
			configuracionTags.setVisible(!configuracionTags.isVisible());
		}
		
		public void fillDataTag(Tag tag) {
			textNombre.setValue(tag.getNombre());
			textDescripcion.setValue(tag.getDescripcion());
		}
		
		private void reload() {
			tagsTipoProductos = tagService.obtenerTagsTipoProducto();
			tagsZonaCobertura = tagService.obtenerTagZonaDeCobertura();
			tagsTipoOrganizacion = tagService.obtenerTagTipoOrganizacion();
			binder.loadAll();
		}
		//[Borrado] metodos de borrado de sellos
		public void onEliminarTagZonaDeCobertura() {
			if(tagTagZonaCoberturaSeleccionado != null) {
				advertenciaDeEliminar(tagTagZonaCoberturaSeleccionado.getNombre(),"zona");
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}
		}
		
		public void eliminarTagZonaDeCobertura(){
			tagService.eliminarTag(tagTagZonaCoberturaSeleccionado);
			Clients.showNotification("El tag se eliminó correctamente", "info", component, "middle_center", 2000);
			limpiarCampos();
			reload();
		}
		
		
		public void onEliminarTagTipoProducto() {
			if(tagTipoProductoSeleccionado != null) {
				advertenciaDeEliminar(tagTipoProductoSeleccionado.getNombre(),"prod");
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}
		}
		
		public void eliminarTagTipoProducto() {
			tagService.eliminarTag(tagTipoProductoSeleccionado);
			Clients.showNotification("El tag se eliminó correctamente", "info", component, "middle_center", 2000);
			limpiarCampos();
			reload();
		}
		
		public void onEliminarTagTipoOrganizacion() {
			if(tagTipoOrganizacionSeleccionado != null) {
				advertenciaDeEliminar(tagTipoOrganizacionSeleccionado.getNombre(),"orga");
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}
		}
		
		public void eliminarTagTipoOrganizacion() {
				tagService.eliminarTag(tagTipoOrganizacionSeleccionado);
				Clients.showNotification("El tag se eliminó correctamente", "info", component, "middle_center", 2000);
				limpiarCampos();
				reload();
		}
		
		private void advertenciaDeEliminar(String tag,final String code) {
			Messagebox.show(
					"¿Esta seguro que desea eliminar el Tag " + tag + "?",
					"Pregunta",
		    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
		    		new String[] {"Aceptar","Cancelar"},
		    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

				public void onEvent(ClickEvent event) throws Exception {
					String edata= event.getData().toString();
					switch (edata){
					case "YES":
						try {
							if(code.equals("zona")) {
								eliminarTagZonaDeCobertura();
							}
							if(code.equals("orga")) {
								eliminarTagTipoOrganizacion();
							}
							if(code.equals("prod")) {
								eliminarTagTipoProducto();
							}
							
						} catch (DataIntegrityViolationException e) {
							Clients.showNotification("No puede eliminar el tag por que uno o mas vendedores lo estan usando.", "error", component, "middle_center", 3000);
							e.printStackTrace();						
						} catch (Exception e) {
							Clients.showNotification("Ocurrio un error desconocido", "error", component, "middle_center", 3000);
							e.printStackTrace();						
						}
						break;
					case "ABORT":
					}
				}
				});
		}
		//[Edicion] metodos de edición de sellos
		public void onEditarTagZonaDeCobertura() {
			if(tagTagZonaCoberturaSeleccionado != null) {
				fillDataTag(tagTagZonaCoberturaSeleccionado);
				hideAllSaveButtons();
				btnGuardarTagZona.setVisible(true);
				switchVisible();
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}
		}
		
		public void onEditarTagTipoProducto() {
			if(tagTipoProductoSeleccionado != null) {
				fillDataTag(tagTipoProductoSeleccionado);
				hideAllSaveButtons();
				btnGuardarTagProducto.setVisible(true);
				switchVisible();
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}

		}
		
		public void onEditarTagTipoOrganizacion() {
			if(tagTipoOrganizacionSeleccionado != null) {
				fillDataTag(tagTipoOrganizacionSeleccionado);
				hideAllSaveButtons();
				btnGuardarTagOrganizacion.setVisible(true);
				switchVisible();
			}else {
				Clients.showNotification("No hay un tag seleccionado", "error", component, "middle_center", 3000);
			}
		}
		
		public void onClick$btnCancelarEdicionCreacionTag() {
			switchVisible();
			limpiarCampos();
		}
		//[Guardar] metodos de Guardado de sellos
		public void onClick$btnGuardarTagZona() {
			validar();
			if(tagTagZonaCoberturaSeleccionado == null) {
				TagZonaDeCobertura tag = new TagZonaDeCobertura(textNombre.getValue(), textDescripcion.getValue());
				tagService.guardarTagZonaDeCobertura(tag);
			}else {
				tagTagZonaCoberturaSeleccionado.setNombre(textNombre.getValue());
				tagTagZonaCoberturaSeleccionado.setDescripcion(textDescripcion.getValue());
				tagTagZonaCoberturaSeleccionado.setFechaModificacion(new DateTime());
				tagService.guardarTagZonaDeCobertura(tagTagZonaCoberturaSeleccionado);
			}
			Clients.showNotification("El tag se guardó correctamente", "info", component, "middle_center", 2000);
			this.limpiarCampos();
			switchVisible();
			reload();
		}
		
		public void onClick$btnGuardarTagProducto() {
			validar();
			if(tagTipoProductoSeleccionado == null) {
				TagTipoProducto tag = new TagTipoProducto(textNombre.getValue(), textDescripcion.getValue());
				tagService.guardarTagTipoProducto(tag);
			}else {
				tagTipoProductoSeleccionado.setNombre(textNombre.getValue());
				tagTipoProductoSeleccionado.setDescripcion(textDescripcion.getValue());
				tagTipoProductoSeleccionado.setFechaModificacion(new DateTime());
				tagService.guardarTagTipoProducto(tagTipoProductoSeleccionado);
			}
			Clients.showNotification("El tag se guardó correctamente", "info", component, "middle_center", 2000);
			this.limpiarCampos();
			switchVisible();
			reload();
		}
		
		public void onClick$btnGuardarTagOrganizacion() {
			validar();
			if(tagTipoOrganizacionSeleccionado == null) {
				TagTipoOrganizacion tag = new TagTipoOrganizacion(textNombre.getValue(), textDescripcion.getValue());
				tagService.guardarTagTipoOrganizacion(tag);
			}else {
				tagTipoOrganizacionSeleccionado.setNombre(textNombre.getValue());
				tagTipoOrganizacionSeleccionado.setDescripcion(textDescripcion.getValue());
				tagTipoOrganizacionSeleccionado.setFechaModificacion(new DateTime());
				tagService.guardarTagTipoOrganizacion(tagTipoOrganizacionSeleccionado);
			}
			Clients.showNotification("El tag se guardó correctamente", "info", component, "middle_center", 2000);
			this.limpiarCampos();
			switchVisible();
			reload();
		}
		
		public void onClick$btnNuevoTagTipoProducto() {
			hideAllSaveButtons();
			btnGuardarTagProducto.setVisible(true);
			limpiarCampos();
			switchVisible();
		}
		
		public void onClick$btnNuevoTagZonaCobertura(){
			hideAllSaveButtons();
			btnGuardarTagZona.setVisible(true);
			limpiarCampos();
			switchVisible();
		}
		
		public void onClick$btnNuevoTagTipoOrganizacion(){
			hideAllSaveButtons();
			btnGuardarTagOrganizacion.setVisible(true);
			limpiarCampos();
			switchVisible();
		}
		
		//[validaciones] seccion de validaciones
		private void validar() {
			if(textNombre.getValue() == null || textNombre.getValue().equals("")) {
				throw new WrongValueException(textNombre,"el nombre no debe ser vacio");
			}
		}

		public List<TagTipoProducto> getTagsTipoProductos() {
			return tagsTipoProductos;
		}

		public TagTipoProducto getTagTipoProductoSeleccionado() {
			return tagTipoProductoSeleccionado;
		}

		public Listbox getListboxTagTipoProductoSeleccionado() {
			return listboxTagTipoProductoSeleccionado;
		}

		public void setTagsTipoProductos(List<TagTipoProducto> tagsTipoProductos) {
			this.tagsTipoProductos = tagsTipoProductos;
		}

		public void setTagTipoProductoSeleccionado(TagTipoProducto tagTipoProductoSeleccionado) {
			this.tagTipoProductoSeleccionado = tagTipoProductoSeleccionado;
		}

		public void setListboxTagTipoProductoSeleccionado(Listbox listboxTagTipoProductoSeleccionado) {
			this.listboxTagTipoProductoSeleccionado = listboxTagTipoProductoSeleccionado;
		}

		public Div getCrearTag() {
			return crearTag;
		}

		public Textbox getTextNombre() {
			return textNombre;
		}


		public void setCrearTag(Div crearTag) {
			this.crearTag = crearTag;
		}

		public void setTextNombre(Textbox textNombre) {
			this.textNombre = textNombre;
		}

		public Div getConfiguracionTags() {
			return configuracionTags;
		}

		public void setConfiguracionTags(Div configuracionTags) {
			this.configuracionTags = configuracionTags;
		}

		public List<TagZonaDeCobertura> getTagsZonaCobertura() {
			return tagsZonaCobertura;
		}

		public void setTagsZonaCobertura(List<TagZonaDeCobertura> tagsZonaCobertura) {
			this.tagsZonaCobertura = tagsZonaCobertura;
		}

		public Listbox getListboxTagZonaCobertura() {
			return listboxTagZonaCobertura;
		}

		public void setListboxTagZonaCobertura(Listbox listboxTagZonaCobertura) {
			this.listboxTagZonaCobertura = listboxTagZonaCobertura;
		}

		public Button getBtnGuardarTagProducto() {
			return btnGuardarTagProducto;
		}

		public void setBtnGuardarTagProducto(Button btnGuardarTagProducto) {
			this.btnGuardarTagProducto = btnGuardarTagProducto;
		}

		public Button getBtnGuardarTagZona() {
			return btnGuardarTagZona;
		}

		public void setBtnGuardarTagZona(Button btnGuardarTagZona) {
			this.btnGuardarTagZona = btnGuardarTagZona;
		}

		public TagZonaDeCobertura getTagTagZonaCoberturaSeleccionado() {
			return tagTagZonaCoberturaSeleccionado;
		}

		public void setTagTagZonaCoberturaSeleccionado(TagZonaDeCobertura tagTagZonaCoberturaSeleccionado) {
			this.tagTagZonaCoberturaSeleccionado = tagTagZonaCoberturaSeleccionado;
		}

		public List<TagTipoOrganizacion> getTagsTipoOrganizacion() {
			return tagsTipoOrganizacion;
		}

		public void setTagsTipoOrganizacion(List<TagTipoOrganizacion> tagsTipoOrganizacion) {
			this.tagsTipoOrganizacion = tagsTipoOrganizacion;
		}

		public TagTipoOrganizacion getTagTipoOrganizacionSeleccionado() {
			return tagTipoOrganizacionSeleccionado;
		}

		public void setTagTipoOrganizacionSeleccionado(TagTipoOrganizacion tagTipoOrganizacionSeleccionado) {
			this.tagTipoOrganizacionSeleccionado = tagTipoOrganizacionSeleccionado;
		}

		public Listbox getListboxTagTipoOrganizacion() {
			return listboxTagTipoOrganizacion;
		}

		public void setListboxTagTipoOrganizacion(Listbox listboxTagTipoOrganizacion) {
			this.listboxTagTipoOrganizacion = listboxTagTipoOrganizacion;
		}

		public Button getBtnGuardarTagOrganizacion() {
			return btnGuardarTagOrganizacion;
		}

		public void setBtnGuardarTagOrganizacion(Button btnGuardarTagOrganizacion) {
			this.btnGuardarTagOrganizacion = btnGuardarTagOrganizacion;
		}



}
