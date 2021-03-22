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
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Window;

import chasqui.model.Producto;

public class ProductoRenderer implements ListitemRenderer<Producto>{

	Window administracionWindow;
	
	public ProductoRenderer(Component w){
		this.administracionWindow = (Window) w;
	}
	
	public void render(Listitem item, Producto p, int arg2) throws Exception {
		Menubar menubar;
		Menu menu;
		Listcell c1 = new Listcell(p.getNombre());
		Listcell c2 = new Listcell(p.getFabricante().getNombre());
		Listcell c5 = new Listcell(p.getCategoria().getNombre());
		Listcell c6 = new Listcell();
		Listcell c7 = new Listcell();
		Listcell c8 = new Listcell();
		Listcell c9 = new Listcell(p.getStock().toString());
		Listcell cReserva = new Listcell(p.getCantidadReservada().toString());
		Listcell cCodigoProducto = new Listcell(p.getCodigo());
		Listcell cPesoGramos = new Listcell(p.getPesoConUnidad());
		Hbox hboxImagenes = new Hbox();
		hboxImagenes.setAlign("center");
		
		if(p.getVariantes().get(0).getDestacado()) {
			c7.setLabel("Destacado");
			c7.setStyle("color:green;font-family:Arial Black;");
		}else {
			c7.setLabel("No Destacado");
			c7.setStyle("color:red;font-family:Arial Black;");
		}
		
		if(p.isOcultado()) {
			c8.setLabel("Oculto");
			c8.setStyle("color:red;font-family:Arial Black;");
		}else {
			c8.setLabel("Visible");
			c8.setStyle("color:green;font-family:Arial Black;");
		}
		
		if(p.getVariantes().get(0).getStock() == 0) {
			c9.setStyle("color:red;font-family:Arial Black;");
		}
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Map<String,Object>params2 = new HashMap<String,Object>();
		Map<String,Object>params3 = new HashMap<String,Object>();
		Map<String,Object>params4 = new HashMap<String,Object>();
		Map<String,Object>params5 = new HashMap<String,Object>();
		
		menubar = new Menubar();
		menu = new Menu("Ver Acciones");
		menu.setParent(menubar);
		Menupopup menupop = new Menupopup();
		menupop.setParent(menu);
		menubar.setAutodrop(false);
		
		//Menuitem ver detalle
		params1.put("accion", "visualizar");
		params1.put("producto", p);
		Menuitem menuitemdetalle = new Menuitem("Ver detalle");
		menuitemdetalle.setTooltip(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.visualizar"));
		menuitemdetalle.setImage("/imagenes/eye.png");
		menuitemdetalle.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params1);
		
		//Menuitem ver visualizar
		
		params2.put("accion", "editar");
		params2.put("producto", p);
		Menuitem menuitemedit = new Menuitem("Editar");
		menuitemedit.setTooltip(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		menuitemedit.setImage("/imagenes/editar.png");
		menuitemedit.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params2);	
		
		//Menu eliminar
		params3.put("accion", "eliminar");
		params3.put("producto", p);
		Menuitem menuitemeliminar = new Menuitem("Eliminar");
		menuitemeliminar.setTooltip(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.visualizar"));
		menuitemeliminar.setImage("/imagenes/detach.png");
		menuitemeliminar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params3);
		
		//Menu destacar
		Menuitem menuitemedestacar = new Menuitem("Destacar");
		menuitemedestacar.setTooltip(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.destacar"));
		menuitemedestacar.setImage("/imagenes/editar.png");
		params4.put("accion", "destacar");
		params4.put("producto", p);
		if(!p.getVariantes().isEmpty()){
			if(!p.getVariantes().get(0).getDestacado()){
				menuitemedestacar.setImage("/imagenes/destacado_off.png");			
			}else{
				menuitemedestacar.setImage("/imagenes/destacado_on.png");
			}
		}else{
			menuitemedestacar.setImage("/imagenes/destacado_off.png");
		}
		menuitemedestacar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params4);
		
		//menuitemOcultar
		
		Menuitem menuitemeocultar = new Menuitem(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.ocultar"));
		menuitemeocultar.setTooltip(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.ocultar"));
		menuitemeocultar.setImage("/imagenes/editar.png");
		params5.put("accion", "ocultar");
		params5.put("producto", p);
		if(!p.isOcultado()){
			menuitemeocultar.setImage("/imagenes/if_toggle-right.png");			
		}else{
			menuitemeocultar.setImage("/imagenes/if_toggle-left.png");
		}
		menuitemeocultar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params5);
		
		menubar.setSclass("selectorproductos");
		
		cCodigoProducto.setParent(item);
		c1.setParent(item); // Producto
		c2.setParent(item); // Productor
		c5.setParent(item); // Categoria
		c9.setParent(item);	// Stock
		cReserva.setParent(item); // cantidad reservada)
		cPesoGramos.setParent(item); // cantidad reservada)
		menuitemdetalle.setParent(menupop);
		menuitemedit.setParent(menupop);
		menuitemedestacar.setParent(menupop);
		menuitemeocultar.setParent(menupop);
		menuitemeliminar.setParent(menupop);
		menubar.setParent(hboxImagenes);
		hboxImagenes.setParent(c6);
		c6.setParent(item);
		c7.setParent(item);
		c8.setParent(item);		
	}
	
}
