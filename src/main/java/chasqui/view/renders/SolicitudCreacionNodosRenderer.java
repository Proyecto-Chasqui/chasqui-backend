package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
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
		String fechaCreacion;
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
		fechaCreacion = parsearFechaDeModificacion(solicitud.getFechaCreacion());
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Toolbarbutton c = new Toolbarbutton();
		params1.put("accion", "gestionar");
		params1.put("solicitud", solicitud);
		c.setTooltiptext(Labels.getLabel("Permite gestionar la solicitud"));
		c.setImage("/imagenes/gestion_16.png");
		c.setLabel("Gestionar");
		c.addForward(Events.ON_CLICK, solicitudCreacionNodoWindow, Events.ON_NOTIFY, params1);
		
		Listcell c1 = new Listcell(String.valueOf(fechaCreacion));
		Listcell c2 = new Listcell(String.valueOf(estado));
		this.aplicarEstiloAEstado(solicitud.getEstado(), c2);
		Listcell c3 = new Listcell(String.valueOf(cliente));
		Listcell c4 = new Listcell(String.valueOf(mail));
		Listcell c5 = new Listcell(String.valueOf(telfijo));
		Listcell c6 = new Listcell(String.valueOf(celular));
		Listcell c7 = new Listcell(String.valueOf(barrio));
		

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
		c100.setParent(item); //Padre de las demas
	}

	private String parsearFechaDeModificacion(DateTime fechaCreacion) {
		if(fechaCreacion != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date( fechaCreacion.getMillis());
			return format.format(d);	
		}
		return "N/D";
	}

	private void aplicarEstiloAEstado(String estado, Listcell c1) {
		if(estado.equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			c1.setStyle("color:#34B65A;");
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_APROBADO)) {
			c1.setStyle("color:#3371FF;");
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_RECHAZADO)) {
			c1.setStyle("color:#FF3333;");
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_CANCELADO)) {
			c1.setStyle("color:#FF9F33;");
		}
		
	}

	private String renderizarEstado(String estado) {
		if(estado.equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			return "EN GESTIÓN";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_APROBADO)) {
			return "APROBADA";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_RECHAZADO)) {
			return "RECHAZADA";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_CANCELADO)) {
			return "CANCELADA";
		}
		return "N/D";
	}

}
