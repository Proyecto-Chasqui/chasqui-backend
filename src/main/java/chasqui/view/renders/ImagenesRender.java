package chasqui.view.renders;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Imagen;
import chasqui.view.composer.ABMProductoComposer;

public class ImagenesRender implements ListitemRenderer<Imagen>{
	
	Window varianteWindow;
	boolean lectura;
	ABMProductoComposer c;
	
	public ImagenesRender(Component comp,boolean lectura,ABMProductoComposer composer){
		varianteWindow = (Window) comp;
		this.lectura = lectura;
		c = composer;
	}
	
	public void setLectura(Boolean lectura){
		this.lectura=lectura;
	}
	public void render(Listitem item, Imagen img, int arg2) throws Exception {
		
		Listcell c1 = new Listcell(img.getNombre());
		Listcell c2 = new Listcell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("5px");

		Toolbarbutton preview = new Toolbarbutton();
		Toolbarbutton trashbutton = new Toolbarbutton();
		if(lectura){
			if(img.getPreview()){
				preview.setImage("/imagenes/destacado_on.png");				
			}else{
				preview.setImage("/imagenes/destacado_off.png");
			}
			preview.addEventListener(Events.ON_CLICK, new PreviewListener(img,c));
			preview.setTooltiptext("Eligir como imagen de previsualización");
			
			trashbutton.setImage("/imagenes/trash.png");
			trashbutton.addForward(Events.ON_CLICK, varianteWindow, Events.ON_CLICK, img);
		}
		
		Toolbarbutton downloadbutton = new Toolbarbutton();
		downloadbutton.setImage("/imagenes/download.png");
		downloadbutton.addForward(Events.ON_CLICK, varianteWindow, Events.ON_USER, img);
		
		space.setParent(hbox);
		preview.setParent(hbox); 
		downloadbutton.setParent(hbox);
		trashbutton.setParent(hbox);	
		hbox.setParent(c2);
		c1.setParent(item);
		c2.setParent(item);
	}

}

final class PreviewListener implements EventListener<Event>{
	
	Imagen model;
	ABMProductoComposer composer;
	public PreviewListener(Imagen img,ABMProductoComposer c) {
		model = img;
		composer = c;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if(model.getPreview()){
			model.setPreview(false);
		}else{
			model.setPreview(true);				
		}
		composer.refresh();
	}
	
}
