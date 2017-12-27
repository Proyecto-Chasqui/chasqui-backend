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

import chasqui.model.CaracteristicaProductor;

public class CaracteristicaProductorRenderer implements ListitemRenderer<CaracteristicaProductor>{
	
	//window abmproductor o producto
	Window window;
	boolean lectura;
	final String CTE_PRODUCTOR = "productor";
	
	public CaracteristicaProductorRenderer(Window c,boolean lectura){
		window = c;
		this.lectura = lectura;
	}
	
	public void render(Listitem item, CaracteristicaProductor c, int arg2) throws Exception {
		Listcell c1 = new Listcell();
		Listcell c2 = new Listcell();
		Listcell c3 = new Listcell();
		Toolbarbutton botonEliminar = new Toolbarbutton();
		
		botonEliminar.setImage("/imagenes/detach.png");
		botonEliminar.setDisabled(lectura);

		Map<String,Object> eliminarActionMap = new HashMap<String,Object>();
		
		eliminarActionMap.put("accion", "eliminar");
		eliminarActionMap.put("caracteristica", c);
		eliminarActionMap.put("tipo", CTE_PRODUCTOR); 
		botonEliminar.addForward(Events.ON_CLICK,window ,Events.ON_USER , eliminarActionMap);
		c1.setLabel(c.getNombre());
		Image img = new Image(c.getPathImagen());
		img.setWidth("16px");
		img.setHeight("16px");
		Hbox hbox = new Hbox();
		img.setParent(hbox);
		hbox.setParent(c2);
		botonEliminar.setParent(c3);
		

		Toolbarbutton botoneditar = new Toolbarbutton();
		botoneditar.setImage("/imagenes/editar.png");
		botoneditar.setDisabled(lectura);
		
		Map<String,Object> editarActionMap = new HashMap<String,Object>();		
		editarActionMap.put("accion", "editar");
		editarActionMap.put("caracteristica", c);
		editarActionMap.put("tipo", CTE_PRODUCTOR); 
		botoneditar.addForward(Events.ON_CLICK,window ,Events.ON_USER , editarActionMap);
		
		botoneditar.setParent(c3);
		
		c1.setParent(item);
		c2.setParent(item);
		c3.setParent(item);
		
	}

}
