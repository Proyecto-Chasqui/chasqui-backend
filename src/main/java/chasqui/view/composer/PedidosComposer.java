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
import org.joda.time.DateTime;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import chasqui.dtos.PedidoIndividualDTO;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.services.interfaces.VendedorService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.composer.Constantes;
import chasqui.view.renders.PedidoRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class PedidosComposer  extends GenericForwardComposer<Component>{
	

	public static final String ACCION_VER = "VER";
	public static final String ACCION_EDITAR = "editar";
	public static final String ACCION_KEY = "accion";
	public static final String PEDIDO_KEY = "pedido";
	public static final Object ACCION_ENTREGAR = "entregar";
	public static final String ACCION_PREPARAR = "preparado";
	public static final String ACCION_NOTIFICAR = "notificar";
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listboxPedidos;
	private Button confirmarEntregabtn;
	public AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private ProductoService productoService;
	private Combobox estadosListbox;
	private Combobox zonasListbox;
	private String estadoSeleccionado = Constantes.ESTADO_PEDIDO_CONFIRMADO;
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
	private Textbox buscadorPorCliente;
	private List<String> puntosDeRetiro;
	private Combobox prCombobox;
	private String prSeleccionado;
	private VendedorService vendedorService;
	private List<Pedido> pedidosSeleccionados;
//	private Integer maximaPaginaVisitada = 1;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("pedidosComposer", this);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			binder = new AnnotateDataBinder(component);
			component.addEventListener(Events.ON_USER, new PedidoEventListener(this));
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			productoService = (ProductoService) SpringUtil.getBean("productoService");
			vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
			mailService = (MailService) SpringUtil.getBean("mailService");
			zonaService = (ZonaService)SpringUtil.getBean("zonaService");

			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_ABIERTO,Constantes.ESTADO_PEDIDO_CANCELADO,Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO, Constantes.ESTADO_PEDIDO_PREPARADO, Constantes.ESTADO_PEDIDO_VENCIDO);
			zonas = zonaService.buscarZonasBy(usuarioLogueado.getId());
			
			pedidos = new ArrayList();
			this.onClick$buscar();

			if(!usuarioLogueado.getIsRoot()) {
				puntosDeRetiro = crearListaDeNombresDePR(vendedorService.obtenerPuntosDeRetiroDeVendedor(usuarioLogueado.getId()));
			}
			window = (Window) component;
			listboxPedidos.setItemRenderer(new PedidoRenderer((Window) component));

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

	public void onClick$buscar(){
		Date d = desde.getValue();
		Date h = hasta.getValue();
		Integer zonaId = null;
		String email = buscadorPorCliente.getValue();
		String nombrePR = null;
		if(zonaSeleccionada!=null){
			zonaId = zonaSeleccionada.getId();
		}
		if(prSeleccionado!= null) {
			nombrePR = prSeleccionado;
		}
		if(d != null && h != null){
			if(h.before(d)){
				Messagebox.show("La fecha hasta debe ser posterior a la fecha desde", "Error", Messagebox.OK,Messagebox.EXCLAMATION);
			}
		}		
		pedidos.clear();
		pedidos.addAll(eliminarPedidosConNDEnFechaModificacion((List<Pedido>) pedidoService.obtenerPedidosIndividualesDeVendedorConPRPorNombre(usuarioLogueado.getId(),d,h,estadoSeleccionado,zonaId,nombrePR,email)));
		this.binder.loadAll();
	}
	
	private List<Pedido> eliminarPedidosConNDEnFechaModificacion(List<Pedido> pedidosIndividuales) {
		if(desde.getValue() != null || hasta.getValue() != null) {
			ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
			for(Pedido p : pedidosIndividuales) {
				if(p.getFechaModificacion() != null && estaConfirmado(p)) {
					pedidos.add(p);
				}
			}
			return pedidos;
		}else {
			return pedidosIndividuales;
		}
		
	}

	private boolean estaConfirmado(Pedido p) {
		String estado = p.getEstado();
		return estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) || estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO) || estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO);
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
		estadoSeleccionado = Constantes.ESTADO_PEDIDO_CONFIRMADO;
		zonaSeleccionada = null;
		desde.setValue(null);
		hasta.setValue(null);
		prSeleccionado =null;
		estadosListbox.setValue("");
		zonasListbox.setValue("");
		buscadorPorCliente.setValue(null);
		prCombobox.setValue("");
		idsSeleccionados = new ArrayList<Integer>();
		this.onClick$buscar();
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
		Messagebox.show(
				"¿Esta seguro que desea marcar el pedido de " + p.getCliente().getEmail() + " a preparado?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata= event.getData().toString();
				switch (edata){
				case "YES":
					try {
						onPrepararEntrega(p);
						Clients.showNotification("El pedido se preparó exitosamente", "info", window, "middle_center", 2000);
					} catch (Exception e) {
						Clients.showNotification("Ocurrio un error desconocido", "error", window, "middle_center", 3000);
						e.printStackTrace();						
					}
					break;
				case "ABORT":
				}
			}
			});
	}
	
	public void onPreguntarConfirmacionEntrega(final Pedido p){
		Messagebox.show(
				"¿Esta seguro que desea confirmar la entrega para el pedido de " + p.getCliente().getEmail() + " ?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata= event.getData().toString();
				switch (edata){
				case "YES":
					try {
						onConfirmarEntrega(p);
						Clients.showNotification("El pedido se confirmó exitosamente", "info", window, "middle_center", 2000);
					} catch (Exception e) {
						Clients.showNotification("Ocurrio un error desconocido", "error", window, "middle_center", 3000);
						e.printStackTrace();						
					}
					break;
				case "ABORT":
				}
			}
			});
		
	}
	
	
	public void onPrepararEntrega(Pedido pedido) throws EstadoPedidoIncorrectoException {
		pedido.preparado();
		pedidoService.guardar(pedido);
		this.binder.loadAll();
	}
	
	public void onNotificar(final Pedido p) {
		Messagebox.show(
				"¿Desea enviar un email de notificación de que pedido esta listo a " + p.getCliente().getEmail() + " ?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				Object eventclick = event.getData();
				if(eventclick != null) {
				String edata= eventclick.toString();
					switch (edata){
					case "YES":
						try {
							notificar(p);
							Clients.showNotification("El email se envió correctamente", "info", window, "middle_center", 2000);
						} catch (Exception e) {
							Clients.showNotification("Ocurrio un error desconocido", "error", window, "middle_center", 3000);
							e.printStackTrace();						
						}
						break;
					case "ABORT":
					}
				}
			}
			});
	}
	
	public void notificar(Pedido p) throws VendedorInexistenteException {
		////////////////////
		//Notificar por mail que el pedido ha sido preparado
		mailService.enviarEmailPreparacionDePedido(p);
		///////////////////
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
		ArrayList<Object> ch =  new ArrayList<>(Arrays.asList(evt.getPreviousSelectedObjects().toArray()));
		insertObjectsFromTo(Arrays.asList(evt.getSelectedObjects().toArray()),ch);
		removeObjectsFromTo(Arrays.asList(evt.getUnselectedObjects().toArray()),ch);
		for(Object check: ch){
				idsSeleccionados.add(((Pedido) check).getId());
		}
		
	}
	
	private void insertObjectsFromTo(List<Object> from, ArrayList<Object> to){
		for(Object element: from){
			if(!to.contains(element)) {
				to.add(element);
			}			
		}
	}
	private void removeObjectsFromTo(List<Object> from, ArrayList<Object> to){
		for(Object element: from){
			to.remove(element);
		}
	}
	
	public void onBuscar(){
		onClick$buscar();
	}
	
	public void onSelect$estadosListbox(SelectEvent evt) {
		onClick$buscar();
	}
	
	public void onSelect$prCombobox(SelectEvent evt) {
		onClick$buscar();
	}
	
	public void onSelect$zonasListbox(SelectEvent evt) {
		onClick$buscar();
	}
	
	public void onClick$exportarSeleccionados() throws Exception{
		pedidosSeleccionados = new ArrayList<Pedido>();
		for(Pedido p: pedidos){
			for(Integer id : idsSeleccionados){
				if(p.getId().equals(id)){
					pedidosSeleccionados.add(p);
				}
			}
		}
		if(pedidosSeleccionados.size() > 0) {
			Messagebox.show(
					"Va a exportar "+pedidosSeleccionados.size()+" pedido/s, es esto correcto?",
					"Pregunta",
		    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.ABORT},
		    		new String[] {"Si","No","Cancelar"},
		    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){
	
				public void onEvent(ClickEvent event) throws Exception {
					String edata = "NO";
					if(event.getData() != null) {
						edata = event.getData().toString();
					}
					switch (edata){
					case "YES":
						try {
						export.fullexport(pedidosSeleccionados);
						Clients.showNotification("Archivo generado correctamente", "info", window, "middle_center", 3000);
						} catch (Exception e) {
							Clients.showNotification(e.getMessage(), "error", window, "middle_center", 20000,true);
							e.printStackTrace();						
						}
						break;
					case "NO":
						break;
					case "ABORT":
					}
				}
				});
		}else {
			Clients.showNotification("Debe seleccionar como minimo 1 pedido para exportar", "warning", window, "middle_center", 5000,true);
		}

	}
	
	private String formarListaDeIDs(List<Pedido> pedidosSeleccionados2) {
		String lista = "";
		for(Pedido pedido: pedidosSeleccionados2) {
			lista = lista + pedido.getId() + ", ";
		}
		return lista;
	}

	public void onClick$exportarTodosbtn() throws EstadoPedidoIncorrectoException{
		Messagebox.show(
				"¿Desea que se genere un resumen de todos los productos de los pedidos?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO, Messagebox.Button.ABORT},
	    		new String[] {"Si","No","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata = "NO";
				if(event.getData() != null) {
					edata = event.getData().toString();
				}
				switch (edata){
				case "YES":
					try {
						List<Pedido> resumen = generarPaginaResumen(pedidos);
						resumen.addAll(pedidos);
						export.fullexport(resumen);
						Clients.showNotification("Archivo generado correctamente", "info", window, "middle_center", 3000);
					} catch (Exception e) {
						Clients.showNotification(e.getMessage(), "error", window, "middle_center", 20000,true);
						e.printStackTrace();						
					}
					break;
				case "NO":
					break;
				case "ABORT":
				}
			}
			});

		this.binder.loadAll();
	}
	

	
	private List<Pedido> generarPaginaResumen(List<Pedido> pedidosgenerados) throws EstadoPedidoIncorrectoException {
		List<Pedido>paginaResumenCompleto = new ArrayList<Pedido>();
		Pedido resumenPedidogeneral = new Pedido(usuarioLogueado,null,false, new DateTime());
		for(Pedido p : pedidosgenerados){
			for(ProductoPedido pp : p.getProductosEnPedido()){
				ProductoPedido ppc = this.copiarProducto(pp);
				resumenPedidogeneral.agregarProductoPedido(ppc, null);
			}
		}
		resumenPedidogeneral.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
		paginaResumenCompleto.add(resumenPedidogeneral);
		return paginaResumenCompleto;	
	}
	
	private ProductoPedido copiarProducto(ProductoPedido pp) {
		ProductoPedido ppc = new ProductoPedido();
		ppc.setCantidad(pp.getCantidad());
		ppc.setIdVariante(pp.getIdVariante());
		ppc.setNombreProductor(pp.getNombreProductor());
		ppc.setImagen(pp.getImagen());
		ppc.setNombreProducto(pp.getNombreProducto());
		ppc.setNombreVariante(pp.getNombreVariante());
		ppc.setPrecio(pp.getPrecio());
		return ppc;
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

	public List<String> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setPuntosDeRetiro(List<String> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}

	public Combobox getPrCombobox() {
		return prCombobox;
	}

	public void setPrCombobox(Combobox prCombobox) {
		this.prCombobox = prCombobox;
	}

	public String getPrSeleccionado() {
		return prSeleccionado;
	}

	public void setPrSeleccionado(String prSeleccionado) {
		this.prSeleccionado = prSeleccionado;
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
		
		if(accion.equals(PedidosComposer.ACCION_NOTIFICAR)){
			composer.onNotificar(p);
				
		}
			
	}
	
}
