package chasqui.view.renders;

import java.util.HashMap;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import chasqui.model.Nodo;

public class SolicitudRenderer implements ListitemRenderer<Nodo>{

	private Window solicitudNodoWindow;
	public SolicitudRenderer(Window w){
		solicitudNodoWindow = w;
	}
	
	public void render(Listitem item, final Nodo nodo, int arg2) throws Exception {
		/*
		 * Aca se define tanto lo que se va a mostrar como su orden.
		 * En este caso, la informacion de los nodos
		**/
		String nombre;
		String apellido;
		String telefono;
		String celular;
			
		if(nodo.getAdministrador() != null){
			apellido = nodo.getAdministrador().getApellido(); 
			nombre	= nodo.getAdministrador().getNombre();
			telefono = nodo.getAdministrador().getTelefonoFijo();
			celular = nodo.getAdministrador().getTelefonoMovil();
		}else{
			nombre = "Sin";
			apellido = "Usuario Registrado";
			telefono = "";
			celular = "";
		}
		
		final Checkbox c = new Checkbox("Autorizado");
		Listcell c1 = new Listcell(String.valueOf(nodo.getAlias()));
		Listcell c3;
		if (nodo.getDireccionDelNodo()!=null){
			 c3 = new Listcell(nodo.getDireccionDelNodo().getCalle() + " " + nodo.getDireccionDelNodo().getAltura());
		}
		else{
			 c3 = new Listcell("");
		}
		Listcell c4 = new Listcell(nombre + " " + apellido);
		Listcell c5 = new Listcell(nodo.getEmailAdministradorNodo());
		Listcell c6 = new Listcell(telefono);
		Listcell c7 = new Listcell(celular);
		Listcell c100 = new Listcell(); //Se usa como padre de las demas
		
		//Toolbarbutton b = new Toolbarbutton("Autorizar");
		//b.setTooltiptext("Autorizar");
		//b.setImage("/imagenes/eye.png");
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("nodo", nodo);
		//b.addForward(Events.ON_CLICK, solicitudNodoWindow, Events.ON_USER, params);
		
		HashMap<String,Object> params2 = new HashMap<String,Object>();
		params2.put("nodo", nodo);
		Space s = new Space();
		s.setSpacing("10px");
	
		
		Hlayout hbox = new Hlayout();
		//b.setParent(hbox);
		c.setParent(hbox);
		s.setParent(hbox);
		hbox.setParent(c100);
		c1.setParent(item);
		c3.setParent(item);
		c4.setParent(item);
		c5.setParent(item);
		c6.setParent(item);
		c7.setParent(item);
		c100.setParent(item); //Padre de las demas
	}

}
