package chasqui.view.composer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.web.servlet.dsp.action.Set;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import chasqui.dtos.PedidoIndividualDTO;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.model.Direccion;
import chasqui.model.Pedido;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.renders.PedidoRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class PedidosComposer  extends GenericForwardComposer<Component>{
	

	public static final String ACCION_VER = "VER";
	public static final String ACCION_EDITAR = "editar";
	public static final String ACCION_KEY = "accion";
	public static final String PEDIDO_KEY = "pedido";
	public static final Object ACCION_ENTREGAR = "entregar";
	public static final String ACCION_PREPARAR = "preparado";
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listboxPedidos;
	private Button confirmarEntregabtn;
	public AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private ProductoService productoService;
	private Combobox estadosListbox;
	private Combobox zonasListbox;
	private String estadoSeleccionado;
	private Zona zonaSeleccionada;
	private String grupalSeleccionado;
	private List<Zona> zonas;
	private List<String>estados;
	private List<Pedido>pedidos;
	Vendedor usuarioLogueado;
	private Paging paginal;
	private Button buscar;
	private List<Integer> idsSeleccionados;
	private ZonaService zonaService;
	private XlsExporter export  = new XlsExporter();
	private MailService mailService;
	private Window window;
//	private Integer maximaPaginaVisitada = 1;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("pedidosComposer", this);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			component.addEventListener(Events.ON_USER, new PedidoEventListener(this));
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			productoService = (ProductoService) SpringUtil.getBean("productoService");
			mailService = (MailService) SpringUtil.getBean("mailService");
			zonaService = (ZonaService)SpringUtil.getBean("zonaService");
			pedidos  = pedidoService.obtenerPedidosIndividualesDeVendedor(usuarioLogueado.getId());
			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_ABIERTO,Constantes.ESTADO_PEDIDO_CANCELADO,Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO, Constantes.ESTADO_PEDIDO_PREPARADO);
			zonas = zonaService.buscarZonasBy(usuarioLogueado.getId());
			binder = new AnnotateDataBinder(component);
			window = (Window) component;
			listboxPedidos.setItemRenderer(new PedidoRenderer((Window) component));
			binder.loadAll();
			
		}
	}
	
	public void onClick$buscar(){
		Date d = desde.getValue();
		Date h = hasta.getValue();
		Integer zonaId = null;
		if(zonaSeleccionada!=null){
			zonaId = zonaSeleccionada.getId();
		}
		if(d != null && h != null){
			if(h.before(d)){
				Messagebox.show("La fecha hasta debe ser posterior a la fecha desde", "Error", Messagebox.OK,Messagebox.EXCLAMATION);
			}
		}		
		pedidos.clear();
		pedidos.addAll(pedidoService.obtenerPedidosIndividualesDeVendedor(usuarioLogueado.getId(),d,h,estadoSeleccionado,zonaId));
		this.binder.loadAll();
	}


	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}


	
	public void onVerPedido(Pedido p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedido", p);
		Window w = (Window) Executions.createComponents("/verPedido.zul", this.self, params);
		w.doModal();
		
	}

	
	public void onEditarZona(Pedido p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedido", p);
		params.put("zonas", usuarioLogueado.getZonas());
		Window w = (Window) Executions.createComponents("/editarPedido.zul", this.self, params);
		w.doModal();
		
	}
	
	
	
	public String getEstadoSeleccionado() {
		return estadoSeleccionado;
	}

	public void setEstadoSeleccionado(String estadoSeleccionado) {
		this.estadoSeleccionado = estadoSeleccionado;
	}
	
	public void onClick$limpiarCamposbtn(){
		estadoSeleccionado = "";
		zonaSeleccionada = null;
		desde.setValue(null);
		hasta.setValue(null);
		estadosListbox.setValue("");
		zonasListbox.setValue("");
		pedidos = pedidoService.obtenerPedidosIndividualesDeVendedor(usuarioLogueado.getId());
		this.binder.loadAll();
	}

	public void onClick$confirmarEntregabtn() throws EstadoPedidoIncorrectoException{
		for(Pedido p : this.pedidos){
			if(p.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
				p.entregarte();
				pedidoService.guardar(p);
			}
		}
		this.binder.loadAll();
	}
	
	//onPreguntarPreparacionDeEntrega
	
	public void onPreguntarPreparacionDeEntrega(final Pedido p){
		EventListener evt = new EventListener() {
			public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
				if(evt.getName().equals("onOK")){
					onPrepararEntrega(p);
				}
			}
		};
		Messagebox.show("¿Esta seguro que desea marcar este pedido a preparado? (Recuerde que se enviara un email al consumidor)",
				"Confirmar", 
				Messagebox.OK|Messagebox.CANCEL,
				Messagebox.QUESTION,
				evt
				);
	}
	
	public void onPreguntarConfirmacionEntrega(final Pedido p){
		EventListener evt = new EventListener() {
			public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
				if(evt.getName().equals("onOK")){
					onConfirmarEntrega(p);
				}
			}
		};
		Messagebox.show("¿Esta seguro que desea confirmar la entrega para este pedido?",
				"Confirmar", 
				Messagebox.OK|Messagebox.CANCEL,
				Messagebox.QUESTION,
				evt
				);
	}
	
	
	public void onPrepararEntrega(Pedido pedido) throws EstadoPedidoIncorrectoException {
		pedido.preparado();
		pedidoService.guardar(pedido);
		////////////////////
		//Notificar por mail que el pedido ha sido preparado
		mailService.enviarEmailPreparacionDePedido(pedido);
		///////////////////
		this.binder.loadAll();
	}
	
	
	public void onConfirmarEntrega(Pedido p) throws EstadoPedidoIncorrectoException {
		p.entregarte();
		pedidoService.guardar(p);
		this.binder.loadAll();
	}
	
	public void mostrarAdvertenciaDeEntrega(Pedido p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("composer", this);
		params.put("mensaje", "¿Esta seguro que desea marcar como entregado este pedido?");
		params.put("pedido", p);
		Window w = (Window) Executions.createComponents("/advertencia.zul", this.self, params);
		w.doModal();
	}
	
	public void onSelect$listboxPedidos(SelectEvent evt) {
		idsSeleccionados = new ArrayList<Integer>();		
		ArrayList<Object> ch =  new ArrayList<>(Arrays.asList(evt.getSelectedItems().toArray()));
		for(Object check: ch){
				idsSeleccionados.add(Integer.parseInt(((Listitem) check).getLabel()));
		}
	}
	
	public void onBuscar(){
		onClick$buscar();
	}
	
	public void onSelect$estadosListbox(SelectEvent evt) {
		onClick$buscar();
	}
	
	public void onSelect$zonasListbox(SelectEvent evt) {
		onClick$buscar();
	}
	
	public void onClick$exportarSeleccionados() throws Exception{
		List<Pedido> pedidosSeleccionados = new ArrayList<Pedido>();
		for(Pedido p: pedidos){
			for(Integer id : idsSeleccionados){
				if(p.getId()==id){
					pedidosSeleccionados.add(p);
				}
			}
		}
		export.fullexport(pedidosSeleccionados);
	}
	
	public void onClick$exportarTodosbtn() throws EstadoPedidoIncorrectoException{
		try {
			Clients.showBusy(window,"Generando el archivo, por favor espere...");
			export.fullexport(this.pedidos);
			Clients.clearBusy(window);
			Clients.showNotification("Archivo generado correctamente", "info", window, "middle_center", 3000);
		} catch (Exception e) {
			Clients.clearBusy(window);
			Clients.showNotification("Ocurrio un error al generar el archivo", "error", window, "middle_center", 3000);
			e.printStackTrace();
		}
		Clients.clearBusy(window);
		this.binder.loadAll();
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}
	
public Combobox getZonasListbox() {
		return zonasListbox;
	}

	public void setZonasListbox(Combobox zonasListbox) {
		this.zonasListbox = zonasListbox;
	}

	public Zona getZonaSeleccionada() {
		return zonaSeleccionada;
	}

	public void setZonaSeleccionada(Zona zonaSeleccionada) {
		this.zonaSeleccionada = zonaSeleccionada;
	}

	public List<Zona> getZonas() {
		return zonas;
	}

	public void setZonas(List<Zona> zonas) {
		this.zonas = zonas;
	}
}

class PedidoEventListener implements EventListener<Event>{

	PedidosComposer composer;
	
	public PedidoEventListener(PedidosComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		
		Map<String,Object> params = (Map<String,Object>) event.getData();
		
		String accion = (String) params.get(PedidosComposer.ACCION_KEY);
		
		Pedido p = (Pedido) params.get(PedidosComposer.PEDIDO_KEY);
		
		if (accion.equals(PedidosComposer.ACCION_VER)) {
			composer.onVerPedido(p);
			
		}
		if(accion.equals(PedidosComposer.ACCION_EDITAR)){
			composer.onEditarZona(p);
				
		}
		
		if(accion.equals(PedidosComposer.ACCION_PREPARAR)){
			composer.onPreguntarPreparacionDeEntrega(p);
				
		}
		
		if(accion.equals(PedidosComposer.ACCION_ENTREGAR)){
			composer.onPreguntarConfirmacionEntrega(p);
				
		}
			
	}
	
}
