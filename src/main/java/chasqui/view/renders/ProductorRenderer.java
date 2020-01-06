package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;


import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;

public class ProductorRenderer implements ListitemRenderer<Fabricante>{

	Window administracionWindow;
	
	public ProductorRenderer(Component w){
		this.administracionWindow = (Window) w;
	}
	
	public void render(Listitem item, Fabricante f, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(f.getNombre());
		Listcell c2 = new Listcell(f.getDescripcionCorta());
		Listcell c3;
		if(f.getCaracteristicas().isEmpty()) {
			c3 = new Listcell(Labels.getLabel("zk.label.administracion.caracteristicanull"));
		}else {
			c3 = new Listcell();
			for(CaracteristicaProductor c : f.getCaracteristicas()) {
				Image caracteristica = new Image();
				caracteristica.setSrc(c.getPathImagen());
				caracteristica.setHeight("24px");
				caracteristica.setWidth("24px");
			}
		}
		Listcell c4 = new Listcell();
		Hbox hbox = new Hbox();
		
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Map<String,Object>params2 = new HashMap<String,Object>();
		Map<String,Object>params3 = new HashMap<String,Object>();
		
		params1.put("accion", "visualizar");
		params1.put("productor", f);
		Toolbarbutton ver = new Toolbarbutton();
		ver.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.visualizar"));
		ver.setImage("/imagenes/eye.png");
		ver.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params1);
		
		params3.put("accion", "edicion");
		params3.put("productor", f);
		Toolbarbutton edicion = new Toolbarbutton();
		edicion.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		edicion.setImage("/imagenes/editar.png");
		edicion.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params3);
		
		params2.put("accion", "eliminar");
		params2.put("productor", f);
		Toolbarbutton eliminar = new Toolbarbutton();
		eliminar.setImage("/imagenes/detach.png");
		eliminar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.eliminar"));
		eliminar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params2);
		
		ver.setParent(hbox);
		edicion.setParent(hbox);
		eliminar.setParent(hbox);
		hbox.setParent(c4);

		c1.setParent(item);
		c2.setParent(item);
		c3.setParent(item);
		c4.setParent(item);
	}

}
