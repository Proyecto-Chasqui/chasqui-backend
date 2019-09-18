package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Cliente;
import chasqui.model.Nodo;
import chasqui.view.composer.Constantes;

public class NodoRenderer implements ListitemRenderer<Nodo>{
	private Window nodoWindow;
	public NodoRenderer(Window w){
		nodoWindow = w;
	}
	@Override
	public void render(Listitem item, Nodo nodo, int index) throws Exception {
		String tipo;
		String cliente;
		String mail;
		String telfijo;
		String celular;
		String barrio;
		String nombreNodo;
		String direccion;
		Cliente datacliente = ((Cliente) nodo.getAdministrador()); 
		tipo = this.renderizarTipo(nodo.getTipo());
		nombreNodo = nodo.getAlias();
		direccion = nodo.getDireccionDelNodo().toString();
		cliente = datacliente.getNombre() + " " + datacliente.getApellido();
		mail = datacliente.getEmail();
		telfijo = (datacliente.getTelefonoFijo().equals(""))? "N/D" : datacliente.getTelefonoFijo();
		celular = (datacliente.getTelefonoMovil().equals(""))? "N/D" : datacliente.getTelefonoMovil();
		barrio = nodo.getBarrio();
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Toolbarbutton c = new Toolbarbutton();
		params1.put("accion", "detallenodo");
		params1.put("nodo", nodo);
		c.setTooltiptext(Labels.getLabel("Ver detalles del nodo"));
		c.setImage("/imagenes/editar.png");
		c.setLabel("Ver Detalles");
		c.addForward(Events.ON_CLICK, nodoWindow, Events.ON_NOTIFY, params1);
		
		Listcell c1 = new Listcell(String.valueOf(nombreNodo));
		Listcell c2 = new Listcell(String.valueOf(tipo));
		Listcell c3 = new Listcell(String.valueOf(cliente));
		Listcell c4 = new Listcell(String.valueOf(mail));
		Listcell c5 = new Listcell(String.valueOf(telfijo));
		Listcell c6 = new Listcell(String.valueOf(celular));
		Listcell c7 = new Listcell(String.valueOf(direccion));
		Listcell c8 = new Listcell(String.valueOf(barrio));
		

		Listcell c100 = new Listcell(); //Se usa como padre de las demas
	
		
		Hlayout hbox = new Hlayout();
		c.setParent(hbox);
		hbox.setParent(c100);
		c1.setParent(item);
		c2.setParent(item);
		c3.setParent(item);
		c4.setParent(item);
		c5.setParent(item);
		c6.setParent(item);
		c7.setParent(item);
		c8.setParent(item);
		c100.setParent(item); //Padre de las demas
		
	}
	private String renderizarTipo(String tipo) {
		return tipo.equals(Constantes.NODO_ABIERTO) ?  "ABIERTO" : "CERRADO";
	}
}
