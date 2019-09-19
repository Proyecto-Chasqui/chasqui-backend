package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Nodo;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.Vendedor;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.view.renders.NodoRenderer;
import chasqui.view.renders.PedidoColectivoRenderer;
import chasqui.view.renders.PedidoRenderer;
import chasqui.view.renders.SolicitudCreacionNodosRenderer;
import chasqui.view.renders.SolicitudRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class NodosComposer  extends GenericForwardComposer<Component>{
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listboxPedidosNodo;
	private Listbox listboxSolicitudesCreacionNodos;
	private Listbox listboxNodos;
	private Button confirmarEntregabtn;
	private AnnotateDataBinder binder;
	
	private List<String> estados; 
	private String estadoSeleccionado;
	private List<PedidoColectivo> pedidosNodos;
	private List<Nodo> nodos;
	
	private NodoService nodoService;
	private Vendedor vendedorLogueado;
	private MailService mailService;
	private PedidoColectivoService pedidoColectivoService;
	private List<SolicitudCreacionNodo> solicitudesCreacionNodos;
	
	private Component component;
	
	
	public void doAfterCompose(Component c) throws Exception{
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(vendedorLogueado != null){
			super.doAfterCompose(c);
			component = c;
			nodoService = (NodoService) SpringUtil.getBean("nodoService");
			mailService = (MailService) SpringUtil.getBean("mailService");
			pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
			c.addEventListener(Events.ON_NOTIFY, new SolicitudEventListener(this));
			c.addEventListener(Events.ON_USER, new PediodosNodosListener(this));
			binder = new AnnotateDataBinder(c);
			pedidosNodos = this.obtenerPedidosColectivos(nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId()));
			//pedidosDeLosNodos = nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId());
			solicitudesCreacionNodos = nodoService.obtenerSolicitudesDeCreacionDeVendedor(vendedorLogueado.getId());
			nodos = nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId());
			listboxPedidosNodo.setItemRenderer(new PedidoColectivoRenderer((Window) c));
			listboxSolicitudesCreacionNodos.setItemRenderer( new SolicitudCreacionNodosRenderer((Window) c));
			listboxNodos.setItemRenderer(new NodoRenderer((Window) c));
			binder.loadAll();
			
		}
		
	}

	private List<PedidoColectivo> obtenerPedidosColectivos(List<Nodo> nodosDelVendedor) {
		List<PedidoColectivo> pedidosColectivos = new ArrayList<PedidoColectivo>();
		for(Nodo nodo: nodosDelVendedor) {
			pedidosColectivos.addAll(nodo.getHistorial().getPedidosGrupales());
			pedidosColectivos.add(nodo.getPedidoActual());
		}
		pedidosColectivos.sort(new Comparator<PedidoColectivo>() {
			@Override
			public int compare(PedidoColectivo o1, PedidoColectivo o2) {
				return o1.getFechaCreacion().compareTo(o2.getFechaCreacion());
			}
		});
		return pedidosColectivos;
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
	
	public void actualizarData() throws VendedorInexistenteException {
		nodos = nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId());
		pedidosNodos = this.obtenerPedidosColectivos(nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId()));
		binder.loadAll();
	}

	public void abrirPopUpGestion(SolicitudCreacionNodo solicitud) {
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("solicitud", solicitud);
		Window w = (Window) Executions.createComponents("/previewGestionSolicitudCreacionNodo.zul", this.self, params);
		w.doModal();
	}


	public Listbox getListboxNodos() {
		return listboxNodos;
	}


	public void setListboxNodos(Listbox listboxNodos) {
		this.listboxNodos = listboxNodos;
	}


	public void abrirDetalleNodo(Nodo nodo) {
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("nodo", nodo);
		Window w = (Window) Executions.createComponents("/detalleNodo.zul", this.self, params);
		w.doModal();
	}

	public Listbox getListboxPedidosNodo() {
		return listboxPedidosNodo;
	}

	public void setListboxPedidosNodo(Listbox listboxPedidosNodo) {
		this.listboxPedidosNodo = listboxPedidosNodo;
	}

	public List<PedidoColectivo> getPedidosNodos() {
		return pedidosNodos;
	}

	public void setPedidosNodos(List<PedidoColectivo> pedidosNodos) {
		this.pedidosNodos = pedidosNodos;
	}

	public List<Nodo> getNodos() {
		return nodos;
	}

	public void setNodos(List<Nodo> nodos) {
		this.nodos = nodos;
	}
	
	public void onVerPedido(PedidoColectivo p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", new ArrayList<Pedido>(p.getPedidosIndividuales().values()) );
		params.put("id",p.getId());
		params.put("exportar",true);
		Window w = (Window) Executions.createComponents("/verPedidosColectivos.zul", this.self, params);
		w.doModal();
		
	}

	
	public void onEditarZona(PedidoColectivo p, GrupoCC grupo){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", p);
		params.put("grupo", p.getColectivo());
		params.put("zonas", vendedorLogueado.getZonas());
		Window w = (Window) Executions.createComponents("/editarPedidoColectivo.zul", this.self, params);
		w.doModal();
	}
	
	public void entregarPedidoColectivo(PedidoColectivo p) throws EstadoPedidoIncorrectoException{
		p.entregarte();
		pedidoColectivoService.guardarPedidoColectivo(p);
		this.binder.loadAll();
	}
	
	public void onNotificar(final PedidoColectivo p) {
		Messagebox.show(
				"¿Desea enviar un email de notificación de pedido preparado al email "+p.getColectivo().getAdministrador().getEmail()+" del administrador?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				Object eventclick = event.getData();
				if(eventclick != null) {
					String edata= event.getData().toString();
					switch (edata){
					case "YES":
						try {
							notificar(p);
							Clients.showNotification("El email se envió correctamente", "info", component, "middle_center", 2000);
						} catch (Exception e) {
							Clients.showNotification("Ocurrio un error desconocido", "error", component, "middle_center", 3000);
							e.printStackTrace();						
						}
						break;
					case "ABORT":
					}
				}
			}
			});
	}
	
	public void prepararPedidoColectivo(PedidoColectivo pedidoColectivo) throws EstadoPedidoIncorrectoException{
		pedidoColectivo.preparado();
		pedidoColectivoService.guardarPedidoColectivo(pedidoColectivo);
		this.binder.loadAll();
	}
	
	public void onPreguntarPerpararEntrega(final PedidoColectivo p){
		Messagebox.show(
				"¿Esta seguro que desea preparar la entrega para el pedido colectivo del grupo "+p.getColectivo().getAlias()+" ?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata= event.getData().toString();
				switch (edata){
				case "YES":
					try {
						prepararPedidoColectivo(p);
						Clients.showNotification("El pedido colectivo se preparó exitosamente", "info", component, "middle_center", 2000);
					} catch (Exception e) {
						Clients.showNotification("Ocurrio un error desconocido", "error", component, "middle_center", 3000);
						e.printStackTrace();						
					}
					break;
				case "ABORT":
				}
			}
			});

	}
	
	private void notificar(PedidoColectivo p) {
		//Notificar por mail que el pedido ha sido preparado
		mailService.enviarEmailPreparacionDePedidoColectivo(p);
	}
	
	public void onPreguntarConfirmacionEntrega(final PedidoColectivo p){
		Messagebox.show(
				"¿Esta seguro que desea confirmar la entrega para pedido colectivo del grupo " + p.getColectivo().getAlias() + " ?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata= event.getData().toString();
				switch (edata){
				case "YES":
					try {
						entregarPedidoColectivo(p);
						Clients.showNotification("La entrega del pedido se confirmó exitosamente", "info", component, "middle_center", 2000);
					} catch (Exception e) {
						Clients.showNotification("Ocurrio un error desconocido", "error", component, "middle_center", 3000);
						e.printStackTrace();						
					}
					break;
				case "ABORT":
				}
			}
			});
	}
	
}

class PediodosNodosListener implements EventListener<Event>{

	NodosComposer composer;
	
	public PediodosNodosListener(
			NodosComposer nodosComposer) {
		this.composer = nodosComposer;
	}

	public void onEvent(Event event) throws Exception {
		
		Map<String,Object> params = (Map<String,Object>) event.getData();
		
		String accion = (String) params.get(PedidosComposer.ACCION_KEY);
		
		PedidoColectivo p = (PedidoColectivo) params.get(PedidosComposer.PEDIDO_KEY);
		
		if (accion.equals(PedidosComposer.ACCION_VER)) {
			composer.onVerPedido(p);
			
		}
		
		if(accion.equals(PedidosComposer.ACCION_PREPARAR)){
			composer.onPreguntarPerpararEntrega(p);				
		}

		if(accion.equals(PedidosComposer.ACCION_EDITAR)){
			composer.onEditarZona(p, null);
				
		}
		
		if(accion.equals(PedidosComposer.ACCION_ENTREGAR)){
			composer.onPreguntarConfirmacionEntrega(p);				
		}
		
		if(accion.equals(PedidosComposer.ACCION_NOTIFICAR)){
			composer.onNotificar(p);				
		}
			
	}
}

class SolicitudEventListener implements EventListener<Event>{

	NodosComposer composer;
	
	public SolicitudEventListener(NodosComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		
		Map<String,Object> params = (Map<String,Object>) event.getData();
		
		String accion = (String) params.get(PedidosComposer.ACCION_KEY);
		
		SolicitudCreacionNodo solicitud = (SolicitudCreacionNodo) params.get("solicitud");
		Nodo nodo = (Nodo) params.get("nodo");
		if (accion.equals("actualizardata")) {
			composer.actualizarData();			
		}
		
		if(accion.equals("gestionar")){
			composer.abrirPopUpGestion(solicitud);	
		}
		
		if(accion.equals("detallenodo")) {
			composer.abrirDetalleNodo(nodo);
		}
		
	}
	
}