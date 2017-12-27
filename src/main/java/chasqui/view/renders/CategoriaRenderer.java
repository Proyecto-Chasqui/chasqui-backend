package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Categoria;

public class CategoriaRenderer implements ListitemRenderer<Categoria>{

	Window admWindow;
	
	public CategoriaRenderer(Component w){
		admWindow = (Window) w;
	}
	
	public void render(Listitem item, Categoria c, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(c.getNombre());
		Listcell c2 = new Listcell();
		Hbox hbox = new Hbox();
		
		Toolbarbutton editar = new Toolbarbutton();
		Toolbarbutton eliminar = new Toolbarbutton();
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Map<String,Object>params2 = new HashMap<String,Object>();
		
		params1.put("accion", "editar");
		params1.put("categoria",c);
		editar.setImage("/imagenes/editar.png");
		editar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		editar.addForward(Events.ON_CLICK, admWindow, Events.ON_USER, params1);
		
		params2.put("accion", "eliminar");
		params2.put("categoria", c);
		eliminar.setImage("/imagenes/detach.png");
		eliminar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.eliminar"));
		eliminar.addForward(Events.ON_CLICK, admWindow, Events.ON_USER, params2);
		
		c1.setParent(item);
		editar.setParent(hbox);
		eliminar.setParent(hbox);
		hbox.setParent(c2);
		c2.setParent(item);
		
		
	}

}
