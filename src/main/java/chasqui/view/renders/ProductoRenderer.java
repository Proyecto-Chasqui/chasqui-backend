package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Producto;

public class ProductoRenderer implements ListitemRenderer<Producto>{

	Window administracionWindow;
	
	public ProductoRenderer(Component w){
		this.administracionWindow = (Window) w;
	}
	
	public void render(Listitem item, Producto p, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(p.getNombre());
		Listcell c2 = new Listcell(p.getFabricante().getNombre());
		Listcell c5 = new Listcell();
		Listcell c6 = new Listcell();
		Hbox hboxImagenes = new Hbox();		
		Combobox combo = new Combobox();
		
		combo.setValue(p.getCategoria().getNombre());
		combo.setDisabled(true);
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Map<String,Object>params2 = new HashMap<String,Object>();
		Map<String,Object>params3 = new HashMap<String,Object>();
		Map<String,Object>params4 = new HashMap<String,Object>();
		Map<String,Object>params5 = new HashMap<String,Object>();
		
		
		params1.put("accion", "visualizar");
		params1.put("producto", p);
		Toolbarbutton botonVisualizar = new Toolbarbutton();
		botonVisualizar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.visualizar"));
		botonVisualizar.setImage("/imagenes/eye.png");
		botonVisualizar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params1);		
		params2.put("accion", "editar");
		params2.put("producto", p);
		Toolbarbutton botonEditar = new Toolbarbutton();
		botonEditar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.editar"));
		botonEditar.setImage("/imagenes/editar.png");
		botonEditar.addForward(Events.ON_CLICK, administracionWindow, Events.ON_NOTIFY, params2);		
		
		params3.put("accion", "eliminar");
		params3.put("producto", p);
		Toolbarbutton botonEliminar = new Toolbarbutton();
		botonEliminar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.eliminar"));
		botonEliminar.setImage("/imagenes/detach.png");
		botonEliminar.addForward(Events.ON_CLICK,administracionWindow, Events.ON_NOTIFY, params3);
		
		Toolbarbutton botonDestacar = new Toolbarbutton();
		botonDestacar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.destacar"));
		params4.put("accion", "destacar");
		params4.put("producto", p);
		params4.put("boton", botonDestacar);
		if(!p.getVariantes().isEmpty()){
			if(!p.getVariantes().get(0).getDestacado()){
				botonDestacar.setImage("/imagenes/destacado_off.png");			
			}else{
				botonDestacar.setImage("/imagenes/destacado_on.png");
			}
		}else{
			botonDestacar.setImage("/imagenes/destacado_off.png");
		}
		botonDestacar.addForward(Events.ON_CLICK,administracionWindow, Events.ON_NOTIFY, params4);
		
		Toolbarbutton botonOcultar = new Toolbarbutton();
		botonOcultar.setTooltiptext(Labels.getLabel("zk.toolbarbutton.administracion.tooltip.ocultar"));
		params5.put("accion", "ocultar");
		params5.put("producto", p);
		params5.put("boton", botonOcultar);
		if(!p.isOcultado()){
			botonOcultar.setImage("/imagenes/if_toggle-right.png");			
		}else{
			botonOcultar.setImage("/imagenes/if_toggle-left.png");
		}
		botonOcultar.addForward(Events.ON_CLICK,administracionWindow, Events.ON_NOTIFY, params5);
		
		c1.setParent(item);
		c2.setParent(item);
		combo.setParent(c5);
		c5.setParent(item);
		botonVisualizar.setParent(hboxImagenes);
		botonEditar.setParent(hboxImagenes);
		botonDestacar.setParent(hboxImagenes);
		botonOcultar.setParent(hboxImagenes);
		botonEliminar.setParent(hboxImagenes);

		hboxImagenes.setParent(c6);
		c6.setParent(item);
	}
	
}
