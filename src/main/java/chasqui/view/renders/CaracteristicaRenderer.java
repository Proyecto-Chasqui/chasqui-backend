package chasqui.view.renders;


import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Caracteristica;

public class CaracteristicaRenderer implements ListitemRenderer<Caracteristica>{

	
	Window window;
	boolean lectura;
	public static final String CTE_PRODUCTO = "producto";
	
	public CaracteristicaRenderer(Window c,boolean lectura){
		window = c;
		this.lectura = lectura;
	}
	
	public void render(Listitem item, Caracteristica c, int arg2) throws Exception {
		Listcell columnaNombre = new Listcell();
		Listcell columnaImagen = new Listcell();
		Listcell columnaAcciones = new Listcell();
		
		
		Toolbarbutton botonEliminar = new Toolbarbutton();
		botonEliminar.setImage("/imagenes/detach.png");
		botonEliminar.setDisabled(lectura);
		Map<String,Object> eliminarActionMap = new HashMap<String,Object>();
		eliminarActionMap.put("accion", "eliminar");
		eliminarActionMap.put("caracteristica", c);
		eliminarActionMap.put("tipo", CTE_PRODUCTO); 
		botonEliminar.addForward(Events.ON_CLICK,window ,Events.ON_USER , eliminarActionMap);
		botonEliminar.setParent(columnaAcciones);
				
		
		Toolbarbutton botoneditar = new Toolbarbutton();
		botoneditar.setImage("/imagenes/editar.png");
		botoneditar.setDisabled(lectura);
		Map<String,Object> editarActionMap = new HashMap<String,Object>();
		editarActionMap.put("accion", "editar");
		editarActionMap.put("caracteristica", c);
		editarActionMap.put("tipo", CTE_PRODUCTO); 
		botoneditar.addForward(Events.ON_CLICK,window ,Events.ON_USER , editarActionMap);
		botoneditar.setParent(columnaAcciones);
		
		Image img = new Image(c.getPathImagen());
		img.setWidth("16px");
		img.setHeight("16px");
		Hbox hbox = new Hbox();
		img.setParent(hbox);
		hbox.setParent(columnaImagen);
		
		columnaNombre.setLabel(c.getNombre());
		
		columnaNombre.setParent(item);
		columnaImagen.setParent(item);
		columnaAcciones.setParent(item);
		
	}
	
}
