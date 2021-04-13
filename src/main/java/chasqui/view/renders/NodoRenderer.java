package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
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
import chasqui.model.PedidoColectivo;
import chasqui.view.composer.Constantes;

public class NodoRenderer implements ListitemRenderer<Nodo>{
	private Window nodoWindow;
	public NodoRenderer(Window w){
		nodoWindow = w;
	}
	@Override
	public void render(Listitem item, Nodo nodo, int index) throws Exception {
		String estado;
		String tipo;
		String cliente;
		String mail;
		String fechaCreacion;
		String barrio;
		String nombreNodo;
		String direccion;
		String nombreZona;
		Cliente datacliente = ((Cliente) nodo.getAdministrador()); 
		estado = this.parsearEstado(nodo);
		tipo = this.renderizarTipo(nodo.getTipo());
		nombreNodo = nodo.getAlias();
		direccion = nodo.getDireccionDelNodo().toString();
		cliente = datacliente.getNombre() + " " + datacliente.getApellido();
		mail = datacliente.getEmail();
		fechaCreacion = this.parsearFechaDeModificacion(nodo.getFechaCreacion());
		barrio = nodo.getBarrio();
		nombreZona = "No definida";
		if(nodo.getZona()!=null) {
			nombreZona = nodo.getZona().getNombre();
		}
		
		
		Map<String,Object>params1 = new HashMap<String,Object>();
		Toolbarbutton c = new Toolbarbutton();
		params1.put("accion", "detallenodo");
		params1.put("nodo", nodo);
		c.setTooltiptext(Labels.getLabel("Ver detalles del nodo"));
		c.setImage("/imagenes/info.png");
		c.setLabel("Mas información");
		c.addForward(Events.ON_CLICK, nodoWindow, Events.ON_NOTIFY, params1);
		Listcell idCell = new Listcell(nodo.getId().toString()); 
		Listcell c0 = new Listcell(String.valueOf(estado));
		this.aplicarEstiloAEstado(estado, c0);
		Listcell c1 = new Listcell(String.valueOf(nombreNodo));
		Listcell c2 = new Listcell(String.valueOf(fechaCreacion));
		Listcell c3 = new Listcell(String.valueOf(tipo));
		this.aplicarEstiloATipo(nodo.getTipo(),c3);
		Listcell c4 = new Listcell(String.valueOf(cliente));
		Listcell c5 = new Listcell(String.valueOf(mail));
		Listcell c6 = new Listcell(String.valueOf(direccion));
		Listcell c7 = new Listcell(String.valueOf(barrio));
		Listcell c8 = new Listcell(String.valueOf(nombreZona));
		aplicarEstiloAZona(nombreZona, c8);


		Listcell c100 = new Listcell(); //Se usa como padre de las demas

		Hlayout hbox = new Hlayout();
		c.setParent(hbox);
		hbox.setParent(c100);
		idCell.setParent(item);
		c0.setParent(item);
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
	
	private void aplicarEstiloAEstado(String estado, Listcell c0) {
		if(estado.equals("ACTIVO")) {
			c0.setStyle("color:green;");
		} else {
			c0.setStyle("color:red;");
		}
		
	}
	private String parsearEstado(Nodo nodo) {
		DateTime fechaDeHoy = new DateTime();
		Integer cantidadDeDias = 90;
		//caso inicial
		if(nodo.getHistorial().getPedidosGrupales().isEmpty() && nodo.getPedidoActual().getFechaModificacion() != null) {
			if(nodo.getPedidoActual().getFechaModificacion().plusDays(cantidadDeDias).isBefore(fechaDeHoy)) {
				return "INACTIVO";
			}
		};
		//caso medio
		if(!nodo.getHistorial().getPedidosGrupales().isEmpty() && nodo.getPedidoActual().getFechaModificacion() == null) {
			Integer idMasAlto = 0;
			PedidoColectivo ultimoPedido = null;
			for (PedidoColectivo pedidoColectivo: nodo.getHistorial().getPedidosGrupales()) {
				if(pedidoColectivo.getFechaModificacion()!=null) {
					if(pedidoColectivo.getId() > idMasAlto) {
						idMasAlto = pedidoColectivo.getId();
						ultimoPedido = pedidoColectivo;
					}
				}
			}
			if(ultimoPedido.getFechaModificacion().plusDays(cantidadDeDias).isBefore(fechaDeHoy)) {
				return "INACTIVO";
			}
		}
		//caso final
		if(!nodo.getHistorial().getPedidosGrupales().isEmpty() && nodo.getPedidoActual().getFechaModificacion() != null) {
			if(nodo.getPedidoActual().getFechaModificacion().plusDays(cantidadDeDias).isBefore(fechaDeHoy)) {
				return "INACTIVO";
			}
		}
		
		return "ACTIVO";	
	}
	private String parsearFechaDeModificacion(DateTime fechaCreacion) {
		if(fechaCreacion != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date( fechaCreacion.getMillis());
			return format.format(d);	
		}
		return "N/D";
	}

	
	private void aplicarEstiloATipo(String tipo, Listcell c2) {
		if(tipo.equals(Constantes.NODO_ABIERTO)) {
			c2.setStyle("color:#3371FF;");
		} else {
			c2.setStyle("color:#34B65A;");
		}	
	}
	
	private void aplicarEstiloAZona(String zona, Listcell c2) {
		if(zona.equals("No definida")) {
			c2.setStyle("color:Red;");
		} 
	}
	private String renderizarTipo(String tipo) {
		return tipo.equals(Constantes.NODO_ABIERTO) ?  "ABIERTO" : "CERRADO";
	}
}
