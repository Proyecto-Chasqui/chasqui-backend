package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import chasqui.model.Tag;
import chasqui.model.TagTipoProducto;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.TagService;
import chasqui.services.interfaces.UsuarioService;

public class ABMTagsComposer extends GenericForwardComposer<Component>{
	//Variables generales
		Vendedor usuario;
		UsuarioService usuarioService;
		private AnnotateDataBinder binder;
		//Variables de selección de preguntas
		private List<TagTipoProducto> tagsTipoProductos;
		private TagTipoProducto tagTipoProductoSeleccionado;
		//variables de edición de preguntas
		private Listbox listboxTagTipoProductoSeleccionado;
		private TagService tagService;
		
		private Div crearTag;
		private Div configuracionTags;
		private Textbox textNombre;
		private Textbox textDescripcion;
		
		public void doAfterCompose(Component comp) throws Exception{
			super.doAfterCompose(comp);
			binder = new AnnotateDataBinder(comp);
			tagService = (TagService) SpringUtil.getBean("tagService");
			tagsTipoProductos = tagService.obtenerTagsTipoProducto();
			binder.loadAll();
		}
		
		private void limpiarCampos() {
			textNombre.setValue("");
			textDescripcion.setValue("");
			flushSelected();
		}
		
		private void flushSelected() {
			tagTipoProductoSeleccionado = null;
		}
		
		private void switchVisible() {
			crearTag.setVisible(!crearTag.isVisible());
			configuracionTags.setVisible(!configuracionTags.isVisible());
		}
		
		public void fillDataTag(Tag tag) {
			textNombre.setValue(tag.getNombre());
			textDescripcion.setValue(tag.getDescripcion());
		}
		
		public void onEliminarTagTipoProducto() {
			if(tagTipoProductoSeleccionado != null) {
				tagService.eliminarTag(tagTipoProductoSeleccionado);
				limpiarCampos();
				reload();
			}
		}
		
		private void reload() {
			tagsTipoProductos = tagService.obtenerTagsTipoProducto();
			binder.loadAll();
		}
		
		public void onEditarTagTipoProducto() {
			fillDataTag(tagTipoProductoSeleccionado);
			switchVisible();
		}
		
		public void onClick$btnCancelarEdicionCreacionTag() {
			switchVisible();
			limpiarCampos();
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
			this.limpiarCampos();
			switchVisible();
			reload();
		}
		
		private void validar() {
			if(textNombre.getValue() == null || textNombre.getValue().equals("")) {
				throw new WrongValueException(textNombre,"el nombre no debe ser vacio");
			}
		}
		
		public void onClick$btnNuevoTagTipoProducto() {
			limpiarCampos();
			switchVisible();
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


}
