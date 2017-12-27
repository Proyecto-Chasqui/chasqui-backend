package chasqui.view.renders;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;

import chasqui.model.Variante;
import chasqui.view.composer.ABMProductoComposer;

public class VarianteItemRenderer implements ListitemRenderer<Variante>{

	
	ABMProductoComposer composer;
	public VarianteItemRenderer(ABMProductoComposer composer){
		this.composer = composer;
	}
	
	
	@Override
	public void render(Listitem item, Variante v, int arg2) throws Exception {
		
		Listcell nombre = new Listcell(v.getNombre());
		
		Listcell precio = new Listcell();
		
		Hbox hbox = new Hbox();
		Image img = new Image("/imagenes/price.png");
		img.setTooltip(Labels.getLabel("'zk.listheader.label.administracion.producto.precio'"));
		Space space = new Space();
		space.setSpacing("5px");;
		Label l = new Label(v.getPrecio().toString());
		
		Listcell stock = new Listcell(v.getStock().toString());
		
		Listcell acciones = new Listcell();
		
		Toolbarbutton destacado = new Toolbarbutton();
		if(!v.getDestacado()){
			destacado.setImage("/imagenes/destacado_off.png");			
		}else{
			destacado.setImage("/imagenes/destacado_on.png");
		}
		destacado.setTooltiptext("Producto Destacado");
		destacado.addEventListener(Events.ON_CLICK, new DestacadoListener(destacado,composer,v));
		destacado.setVisible(composer.isModoEdicion());
		
		Space sp = new Space();
		sp.setSpacing("10px");
		
		Toolbarbutton visualizar = new Toolbarbutton();
		visualizar.setImage("/imagenes/eye.png");
		visualizar.setTooltip(Labels.getLabel("'zk.toolbarbutton.administracion.tooltip.visualizar'"));
		visualizar.addEventListener(Events.ON_CLICK, new VisualizarListener(visualizar,composer,v));
		
		Space spps = new Space();
		spps.setSpacing("10px");
		
		Toolbarbutton editar = new Toolbarbutton();
		editar.setImage("/imagenes/editar.png");
		editar.setTooltip(Labels.getLabel("Editar"));
		editar.addEventListener(Events.ON_CLICK, new EditarListener(visualizar,composer,v));
		
		
		Space spp = new Space();
		spp.setSpacing("10px");
		
		
		Toolbarbutton borrar = new Toolbarbutton();
		borrar.setImage("/imagenes/trash.png");
		borrar.setTooltip(Labels.getLabel("'zk.toolbarbutton.administracion.tooltip.eliminar'"));
		borrar.setVisible(composer.isModoEdicion());
		borrar.addEventListener(Events.ON_CLICK, new BorrarListener(borrar,composer,v));
		
		img.setParent(hbox);
		space.setParent(hbox);
		l.setParent(hbox);
		hbox.setParent(precio);
		
		
		destacado.setParent(acciones);
		sp.setParent(acciones);
		visualizar.setParent(acciones);
		spps.setParent(acciones);
		editar.setParent(acciones);
		spp.setParent(acciones);
		borrar.setParent(acciones);
		
		nombre.setParent(item);
		precio.setParent(item);
		stock.setParent(item);
		acciones.setParent(item);
		
	}

}

class DestacadoListener implements EventListener<Event>{

	Toolbarbutton desc;
	ABMProductoComposer c;
	Variante model;
	
	public DestacadoListener(Toolbarbutton destacado, ABMProductoComposer composer,Variante v) {
		desc = destacado;
		c = composer;
		model = v;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		c.onDestacarVariante(model);

	}
	
}


class VisualizarListener implements EventListener<Event>{

	Toolbarbutton desc;
	ABMProductoComposer c;
	Variante model;
	
	public VisualizarListener(Toolbarbutton destacado, ABMProductoComposer composer,Variante v) {
		desc = destacado;
		c = composer;
		model = v;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		c.onVerVariante(model);		
	}
	
}


class EditarListener implements EventListener<Event>{

	Toolbarbutton desc;
	ABMProductoComposer c;
	Variante model;
	
	public EditarListener(Toolbarbutton destacado, ABMProductoComposer composer,Variante v) {
		desc = destacado;
		c = composer;
		model = v;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		c.onEditarVariante(model);		
	}
	
}


class BorrarListener implements EventListener<Event>{

	Toolbarbutton desc;
	ABMProductoComposer c;
	Variante model;
	
	public BorrarListener(Toolbarbutton destacado, ABMProductoComposer composer,Variante v) {
		desc = destacado;
		c = composer;
		model = v;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		c.onEliminarVariante(model);		
	}
	
}
