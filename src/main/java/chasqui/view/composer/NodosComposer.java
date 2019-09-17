package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.Vendedor;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.services.interfaces.NodoService;
import chasqui.view.renders.PedidoRenderer;
import chasqui.view.renders.SolicitudCreacionNodosRenderer;
import chasqui.view.renders.SolicitudRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class NodosComposer  extends GenericForwardComposer<Component>{
	
	private Datebox desde;
	private Datebox hasta;
	@Deprecated
	private Listbox listboxSolicitudesNodos;
	private Listbox listboxSolicitudesCreacionNodos;
	private Button confirmarEntregabtn;
	private AnnotateDataBinder binder;
	
	private List<String> estados; 
	private String estadoSeleccionado;
	private List<Nodo> nodosSolicitados;
	
	private NodoService nodoService;
	private Vendedor vendedorLogueado;
	private List<SolicitudCreacionNodo> solicitudesCreacionNodos;
	
	
	public void doAfterCompose(Component c) throws Exception{
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(vendedorLogueado != null){
			super.doAfterCompose(c);
			nodoService = (NodoService) SpringUtil.getBean("nodoService");
			c.addEventListener(Events.ON_NOTIFY, new SolicitudEventListener(this));
			binder = new AnnotateDataBinder(c);
			nodosSolicitados = new ArrayList<Nodo>();
			nodosSolicitados = nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId());
			solicitudesCreacionNodos = nodoService.obtenerSolicitudesDeCreacionDeVendedor(vendedorLogueado.getId());
			listboxSolicitudesNodos.setItemRenderer(new SolicitudRenderer((Window) c));
			listboxSolicitudesCreacionNodos.setItemRenderer( new SolicitudCreacionNodosRenderer((Window) c));
			binder.loadAll();
			
		}
		
	}


	public List<Nodo> getNodosSolicitados() {
		return nodosSolicitados;
	}

	public void setNodosSolicitados(List<Nodo> nodos) {
		this.nodosSolicitados = nodos;
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}
	
	public String getEstadoSeleccionado() {
		return estadoSeleccionado;
	}

	public void setEstadoSeleccionado(String estadoSeleccionado) {
		this.estadoSeleccionado = estadoSeleccionado;
	}


	public List<SolicitudCreacionNodo> getSolicitudesCreacionNodos() {
		return solicitudesCreacionNodos;
	}


	public void setSolicitudesCreacionNodos(List<SolicitudCreacionNodo> solicitudesCreacionNodos) {
		this.solicitudesCreacionNodos = solicitudesCreacionNodos;
	}


	public Listbox getListboxSolicitudesCreacionNodos() {
		return listboxSolicitudesCreacionNodos;
	}


	public void setListboxSolicitudesCreacionNodos(Listbox listboxSolicitudesCreacionNodos) {
		this.listboxSolicitudesCreacionNodos = listboxSolicitudesCreacionNodos;
	}


	public void abrirPopUpGestion(SolicitudCreacionNodo solicitud) {
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("solicitud", solicitud);
		Window w = (Window) Executions.createComponents("/previewGestionSolicitudCreacionNodo.zul", this.self, params);
		w.doModal();
	}

	
}

class SolicitudEventListener implements EventListener<Event>{

	NodosComposer composer;
	
	public SolicitudEventListener(NodosComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		Map<String,Object> params = (Map<String,Object>) event.getData();
		SolicitudCreacionNodo solicitud = (SolicitudCreacionNodo) params.get("solicitud");
		composer.abrirPopUpGestion(solicitud);
		//composer.onAutorizar(nodo);
		
	}
	
}