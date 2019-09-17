package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Nodo;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.Cliente;
import chasqui.services.interfaces.NodoService;
import chasqui.view.composer.Constantes;

public class SolicitudCreacionNodosRenderer implements ListitemRenderer<SolicitudCreacionNodo>{
	
	private Window solicitudCreacionNodoWindow;
	public SolicitudCreacionNodosRenderer(Window w){
		solicitudCreacionNodoWindow = w;
	}
	
	public void render(Listitem item, final SolicitudCreacionNodo solicitud, int arg2) throws Exception {
		
		String estado;
		String cliente;
		String mail;
		String telfijo;
		String celular;
		String barrio;
		Cliente datacliente = ((Cliente) solicitud.getUsuarioSolicitante()); 
		estado = this.renderizarEstado(solicitud.getEstado());
		cliente = datacliente.getNombre() + " " + datacliente.getApellido();
		mail = datacliente.getEmail();
		telfijo = (datacliente.getTelefonoFijo().equals(""))? "N/D" : datacliente.getTelefonoFijo();
		celular = (datacliente.getTelefonoMovil().equals(""))? "N/D" : datacliente.getTelefonoMovil();
		barrio = solicitud.getBarrio();
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Toolbarbutton c = new Toolbarbutton();
		params1.put("accion", "gestionar");
		params1.put("solicitud", solicitud);
		c.setTooltiptext(Labels.getLabel("Permite gestionar la solicitud"));
		c.setImage("/imagenes/editar.png");
		c.setLabel("GESTIONAR");
		c.addForward(Events.ON_CLICK, solicitudCreacionNodoWindow, Events.ON_NOTIFY, params1);
		
		Listcell c1 = new Listcell(String.valueOf(estado));
		Listcell c2 = new Listcell(String.valueOf(cliente));
		Listcell c3 = new Listcell(String.valueOf(mail));
		Listcell c4 = new Listcell(String.valueOf(telfijo));
		Listcell c5 = new Listcell(String.valueOf(celular));
		Listcell c6 = new Listcell(String.valueOf(barrio));

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
		c100.setParent(item); //Padre de las demas
	}

	private String renderizarEstado(String estado) {
		if(estado.equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			return "EN GESTIÓN";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_APROBADO)) {
			return "APROBADO";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_RECHAZADO)) {
			return "RECHAZADO";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_CANCELADO)) {
			return "CANCELADO";
		}
		return "N/D";
	}

}
