package chasqui.view.composer;

import org.apache.cxf.common.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import chasqui.model.Categoria;
import chasqui.model.Vendedor;
import chasqui.view.genericEvents.CreatedListener;
import chasqui.view.genericEvents.ICreatedCallback;
import chasqui.services.interfaces.UsuarioService;

@SuppressWarnings({"serial","deprecation"})
public class ABMCategoriaComposer extends GenericForwardComposer<Component> {

	private Categoria model;
	private Toolbarbutton buttonGuardar;
	private Textbox textboxNombreCategoria;
	private AnnotateDataBinder binder;
	private Vendedor usuario;
	private UsuarioService usuarioService;
	private Boolean esEdicion;
		
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		model = (Categoria) Executions.getCurrent().getArg().get("categoria");
		esEdicion = (Boolean) Executions.getCurrent().getArg().get("esEdicion");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(model != null){
			iniciarModoEdicion();
		}
		binder.loadAll();
		
	}
	
	
	private void iniciarModoEdicion(){
		textboxNombreCategoria.setValue(model.getNombre());
	}

	private void notifyCreated (Categoria newCategoria) {
		Object data = CreatedListener.createData("categoria", newCategoria);
		Events.sendEvent(CreatedListener.ON_CREATED ,this.self.getParent(), data);
	}
	
	
	public void onClick$buttonGuardar(){
		if(StringUtils.isEmpty(textboxNombreCategoria.getValue())){
			throw new WrongValueException("El nombre no debe ser vacio!");
		}
		if(usuario.contieneCategoria(textboxNombreCategoria.getValue()) && !esEdicion){
			throw new WrongValueException("El usuario:" + usuario.getUsername() + " ya contiene la categoria: "+ textboxNombreCategoria.getValue());
		}
		
		// guardar y cerrar
		if(model != null){
			model.setNombre(textboxNombreCategoria.getValue());
			usuarioService.guardarUsuario(usuario);
			Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		}else{
			model = new Categoria();
			model.setNombre(textboxNombreCategoria.getValue());
			model.setVendedor(usuario);
			usuario.agregarCategoria(model);
//			model.setProductos(null);
			usuarioService.guardarUsuario(usuario);
			notifyCreated(model);
			Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		}
		// GUARDAR EN DB
		this.self.detach();
	}
}
