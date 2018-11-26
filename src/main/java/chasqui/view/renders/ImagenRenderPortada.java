package chasqui.view.renders;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Imagen;
import chasqui.view.composer.ConfiguracionPortadaComposer;

public class ImagenRenderPortada implements ListitemRenderer<Imagen>{
	
	Window window;
	boolean lectura;
	ConfiguracionPortadaComposer c;
	
	public ImagenRenderPortada(Component comp,ConfiguracionPortadaComposer composer){
		window = (Window) comp;
		c = composer;
	}
	
	public void render(Listitem item, Imagen img, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(img.getNombre());
		Listcell c2 = new Listcell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("5px");
		
		Toolbarbutton trashbutton = new Toolbarbutton();
			
		trashbutton.setImage("/imagenes/trash.png");
		trashbutton.addForward(Events.ON_CLICK, window, Events.ON_CLICK, img);
		
		Toolbarbutton downloadbutton = new Toolbarbutton();
		downloadbutton.setImage("/imagenes/download.png");
		downloadbutton.addForward(Events.ON_CLICK, window, Events.ON_USER, img);
		
		space.setParent(hbox);
		downloadbutton.setParent(hbox);
		trashbutton.setParent(hbox);	
		hbox.setParent(c2);
		c1.setParent(item);
		c2.setParent(item);
	}

}
