package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Vendedor;
import chasqui.view.composer.Constantes;

public class UsuarioRenderer implements ListitemRenderer<Vendedor>{

	Window usuariosActualesWindow;
	public UsuarioRenderer(Window c){
		usuariosActualesWindow = c;
	}
	
	public void render(Listitem item, Vendedor u, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(u.getUsername());
		Listcell c2 = new Listcell();
		Hbox hbox = new Hbox();
		Vendedor usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);

		
		// Boton editar datos de vendedor
		
		Toolbarbutton editar = new Toolbarbutton();
		editar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		editar.setImage("/imagenes/editar.png");
		
		Map<String,Object> mapEditar = new HashMap<String,Object>();
		mapEditar.put("accion", "editar");
		mapEditar.put("usuario", u);
		editar.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY, mapEditar);
		
		editar.setParent(hbox);
		
		
		// Boton editar estrategias
		
		Toolbarbutton editarEstrategias = new Toolbarbutton();
		editarEstrategias.setTooltiptext("Editar estrategias");
		editarEstrategias.setImage("/imagenes/bookedit.png");
		
		Map<String,Object> mapEditarEstrategias = new HashMap<String,Object>();
		mapEditarEstrategias.put("accion", "editarEstrategias");
		mapEditarEstrategias.put("usuario", u);
		editarEstrategias.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY, mapEditarEstrategias);

		editarEstrategias.setParent(hbox);
		
		
		// Boton cargar startup
		
		Toolbarbutton cargarStartUp = new Toolbarbutton();
		cargarStartUp.setTooltiptext("Cargar startup");
		cargarStartUp.setImage("/imagenes/bookedit.png");		
				
		Map<String,Object> mapCargarStartUp= new HashMap<String,Object>();
		mapCargarStartUp.put("accion", "cargarStartUp");
		mapCargarStartUp.put("usuario", u);
		cargarStartUp.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY, mapCargarStartUp); 

		cargarStartUp.setParent(hbox);
		
		
		// Boton eliminar vendedor
		
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("accion", "eliminar");
		map2.put("usuario", u);
		
		c1.setParent(item);
		if(!usuarioLogueado.getUsername().equalsIgnoreCase(u.getUsername())){
			Toolbarbutton eliminar = new Toolbarbutton();
			eliminar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.eliminar"));
			eliminar.setImage("/imagenes/detach.png");
			eliminar.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY,map2);
			eliminar.setParent(hbox);			
		}
		hbox.setParent(c2);
		c2.setParent(item);
		
	}

	

	
		
		
	

}
