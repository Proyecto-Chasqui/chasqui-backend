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
		Toolbarbutton editar = new Toolbarbutton();
		Toolbarbutton editarEstrategias = new Toolbarbutton();
		Vendedor usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		editar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		editar.setImage("/imagenes/editar.png");
		editarEstrategias.setTooltiptext("Editar estrategias");
		editarEstrategias.setImage("/imagenes/editar.png");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("accion", "editar");
		map.put("usuario", u);
		editar.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY, map);
		
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("accion", "eliminar");
		map2.put("usuario", u);
		
		Map<String,Object> map3 = new HashMap<String,Object>();
		map3.put("accion", "editarEstrategias");
		map3.put("usuario", u);
		editarEstrategias.addForward(Events.ON_CLICK, usuariosActualesWindow, Events.ON_NOTIFY, map3); 
		
		c1.setParent(item);
		editar.setParent(hbox);
		editarEstrategias.setParent(hbox);
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
