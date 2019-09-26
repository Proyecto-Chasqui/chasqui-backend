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
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
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
import chasqui.model.PuntoDeRetiro;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.VendedorService;
import chasqui.services.interfaces.ZonaService;
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
	private List<Zona> zonas;
	private Button buscar;
	private Textbox buscadorPorUsuario;
	private Combobox zonasListbox;
	private Zona zonaSeleccionada;
	private Combobox estadosListbox;
	
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
	private Combobox prCombobox;
	private String prSeleccionado;
	private List<String> puntosDeRetiro;
	private VendedorService vendedorService;
	private ZonaService zonaService;
	
	private boolean tienePuntosDeRetiro;
	private Menuitem menuItemMostrarFiltrosPedidosNodos;
	private Menuitem menuItemReiniciarFiltrosPedidosNodos;
	
	private Div filtros;
	
	public void doAfterCompose(Component c) throws Exception{
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(vendedorLogueado != null){
			super.doAfterCompose(c);
			component = c;
			tienePuntosDeRetiro = vendedorLogueado.getEstrategiasUtilizadas().isPuntoDeEntrega();
			nodoService = (NodoService) SpringUtil.getBean("nodoService");
			mailService = (MailService) SpringUtil.getBean("mailService");
			vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
			zonaService = (ZonaService) SpringUtil.getBean("zonaService");
			pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
			c.addEventListener(Events.ON_NOTIFY, new SolicitudEventListener(this));
			c.addEventListener(Events.ON_USER, new PediodosNodosListener(this));
			binder = new AnnotateDataBinder(c);
			pedidosNodos = pedidoColectivoService.obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(vendedorLogueado.getId(), null, null, null, null, null, null);
			solicitudesCreacionNodos = nodoService.obtenerSolicitudesDeCreacionDeVendedor(vendedorLogueado.getId());
			nodos = nodoService.obtenerNodosDelVendedor(vendedorLogueado.getId());
			zonas = zonaService.buscarZonasBy(vendedorLogueado.getId());
			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO,Constantes.ESTADO_PEDIDO_ABIERTO,Constantes.ESTADO_PEDIDO_PREPARADO);
			listboxPedidosNodo.setItemRenderer(new PedidoColectivoRenderer((Window) c));
			listboxSolicitudesCreacionNodos.setItemRenderer( new SolicitudCreacionNodosRenderer((Window) c));
			listboxNodos.setItemRenderer(new NodoRenderer((Window) c));
			if(!vendedorLogueado.getIsRoot()) {
				puntosDeRetiro = crearListaDeNombresDePR(vendedorService.obtenerPuntosDeRetiroDeVendedor(vendedorLogueado.getId()));
			}
			binder.loadAll();
			
		}
		
	}
	
	private List<String> crearListaDeNombresDePR(List<PuntoDeRetiro> obtenerPuntosDeRetiroDeVendedor) {
		List<String> list = new ArrayList<String>();
		for(PuntoDeRetiro pr: obtenerPuntosDeRetiroDeVendedor) {
			list.add(pr.getNombre());
		}
	    return list;
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
				return o2.getFechaCreacion().compareTo(o1.getFechaCreacion());
			}
		});
		return pedidosColectivos;
	}

	public void onBuscar(){
		onClick$buscar();
	}
	
	public void onMostrarFiltrosPedidosNodos() {
		filtros.setVisible(!filtros.isVisible());
		if(filtros.isVisible()) {
			menuItemMostrarFiltrosPedidosNodos.setLabel("Ocultar Filtros");
		}else {
			menuItemMostrarFiltrosPedidosNodos.setLabel("Mostrar Filtros");
		}
	}
	
	public void onReiniciarFiltrosPedidosNodos() {
		this.onClick$limpiarCamposbtn();
	}
	
	public void onSelect$estadosListbox(SelectEvent evt) {
			onClick$buscar();
	
	}
	
	public void onSelect$zonasListbox(SelectEvent evt) {
			onClick$buscar();
	}
	
	public void onClick$limpiarCamposbtn(){
		menuItemReiniciarFiltrosPedidosNodos.setVisible(false);
		estadoSeleccionado = "";
		zonaSeleccionada = null;
		desde.setValue(null);
		hasta.setValue(null);
		estadosListbox.setValue("");
		zonasListbox.setValue(null);
		buscadorPorUsuario.setValue("");
		prSeleccionado = null;
		prCombobox.setValue("");
		pedidosNodos = pedidoColectivoService.obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(vendedorLogueado.getId(), null, null, null, null, null, null);
		Clients.showNotification("Filtros restablecidos", "info", component, "middle_center", 2000, true);
		this.binder.loadAll();
	}

	public void onClick$buscar(){
		menuItemReiniciarFiltrosPedidosNodos.setVisible(true);
		Date d = desde.getValue();
		Date h = hasta.getValue();
		String email = buscadorPorUsuario.getValue();
		if(d != null && h != null){
			if(h.before(d)){
				Messagebox.show("La fecha hasta debe ser posterior a la fecha desde", "Error", Messagebox.OK,Messagebox.EXCLAMATION);
			}
		}		
		pedidosNodos.clear();
		Integer zonaId= null;
		if(zonaSeleccionada !=null){
			zonaId = zonaSeleccionada.getId();
		}
		pedidosNodos.addAll(pedidoColectivoService.obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(vendedorLogueado.getId(),d,h,estadoSeleccionado,zonaId, prSeleccionado, email));
		this.binder.loadAll();
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

	public Datebox getDesde() {
		return desde;
	}

	public Datebox getHasta() {
		return hasta;
	}

	public Button getConfirmarEntregabtn() {
		return confirmarEntregabtn;
	}

	public List<Zona> getZonas() {
		return zonas;
	}

	public Button getBuscar() {
		return buscar;
	}

	public Textbox getBuscadorPorUsuario() {
		return buscadorPorUsuario;
	}

	public Combobox getZonasListbox() {
		return zonasListbox;
	}

	public Combobox getPrCombobox() {
		return prCombobox;
	}

	public String getPrSeleccionado() {
		return prSeleccionado;
	}

	public List<String> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setDesde(Datebox desde) {
		this.desde = desde;
	}

	public void setHasta(Datebox hasta) {
		this.hasta = hasta;
	}

	public void setConfirmarEntregabtn(Button confirmarEntregabtn) {
		this.confirmarEntregabtn = confirmarEntregabtn;
	}

	public void setZonas(List<Zona> zonas) {
		this.zonas = zonas;
	}

	public void setBuscar(Button buscar) {
		this.buscar = buscar;
	}

	public void setBuscadorPorUsuario(Textbox buscadorPorUsuario) {
		this.buscadorPorUsuario = buscadorPorUsuario;
	}

	public void setZonasListbox(Combobox zonasListbox) {
		this.zonasListbox = zonasListbox;
	}

	public void setPrCombobox(Combobox prCombobox) {
		this.prCombobox = prCombobox;
	}

	public void setPrSeleccionado(String prSeleccionado) {
		this.prSeleccionado = prSeleccionado;
	}

	public void setPuntosDeRetiro(List<String> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}

	public Zona getZonaSeleccionada() {
		return zonaSeleccionada;
	}

	public void setZonaSeleccionada(Zona zonaSeleccionada) {
		this.zonaSeleccionada = zonaSeleccionada;
	}

	public boolean isTienePuntosDeRetiro() {
		return tienePuntosDeRetiro;
	}

	public void setTienePuntosDeRetiro(boolean tienePuntosDeRetiro) {
		this.tienePuntosDeRetiro = tienePuntosDeRetiro;
	}

	public Menuitem getMenuItemMostrarFiltrosPedidosNodos() {
		return menuItemMostrarFiltrosPedidosNodos;
	}

	public Menuitem getMenuItemReiniciarFiltrosPedidosNodos() {
		return menuItemReiniciarFiltrosPedidosNodos;
	}

	public void setMenuItemMostrarFiltrosPedidosNodos(Menuitem menuItemMostrarFiltrosPedidosNodos) {
		this.menuItemMostrarFiltrosPedidosNodos = menuItemMostrarFiltrosPedidosNodos;
	}

	public void setMenuItemReiniciarFiltrosPedidosNodos(Menuitem menuItemReiniciarFiltrosPedidosNodos) {
		this.menuItemReiniciarFiltrosPedidosNodos = menuItemReiniciarFiltrosPedidosNodos;
	}

	public Div getFiltros() {
		return filtros;
	}

	public void setFiltros(Div filtros) {
		this.filtros = filtros;
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