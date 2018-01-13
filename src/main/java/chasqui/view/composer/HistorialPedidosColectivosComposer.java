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

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.renders.PedidoColectivoRenderer;
import chasqui.view.renders.PedidoRenderer;


@SuppressWarnings({"serial","deprecation","unused"})
public class HistorialPedidosColectivosComposer extends GenericForwardComposer<Component> {
	
	public static final String ACCION_VER = "VER";
	public static final String ACCION_EDITAR = "editar";
	public static final String ACCION_KEY = "accion";
	public static final String PEDIDO_KEY = "pedido";
	public static final Object ACCION_ENTREGAR = "entregar";
	public static final String ACCION_PREPARAR= "preparado";
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listboxPedidos;
	private Button confirmarEntregabtn;
	public AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private ProductoService productoService;
	private Combobox estadosListbox;
	private Combobox zonasListbox;
	private Zona zonaSeleccionada;
	private String estadoSeleccionado;
	private String grupalSeleccionado;
	private List<Zona> zonas;
	private List<String>estados;
	private List<Pedido>pedidos;
	private List<PedidoColectivo>pedidosColectivos;
	Vendedor usuarioLogueado;
	private Paging paginal;
	private Button buscar;
	private List<Integer> idsSeleccionados;
	private GrupoCC grupo;
	private PedidosColectivosComposer comp;
	private GrupoService grupoService;
	private PedidoColectivoService pedidoColectivoService;
	private ZonaService zonaService;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("historialPedidosColectivosComposer",this);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			grupo = (GrupoCC) Executions.getCurrent().getArg().get("Grupo");
			component.addEventListener(Events.ON_USER, new HitorialPedidosColectivosEventListener(this,grupo));
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			productoService = (ProductoService) SpringUtil.getBean("productoService");
			pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
			grupoService = (GrupoService) SpringUtil.getBean("grupoService");
			pedidos  = pedidoService.obtenerPedidosIndividualesDeVendedor(usuarioLogueado.getId());
			zonaService = (ZonaService) SpringUtil.getBean("zonaService");
			zonas = zonaService.buscarZonasBy(usuarioLogueado.getId());
			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO);
			pedidosColectivos = (List<PedidoColectivo>) Executions.getCurrent().getArg().get("HistorialDePedidoColectivo");			
			binder = new AnnotateDataBinder(component);
			listboxPedidos.setItemRenderer(new PedidoColectivoRenderer((Window) component));
			this.onClick$limpiarCamposbtn();
			
		}
	}
	
	public void onClick$buscar(){
		Date d = desde.getValue();
		Date h = hasta.getValue();
		
		if(d != null && h != null){
			if(h.before(d)){
				Messagebox.show("La fecha hasta debe ser posterior a la fecha desde", "Error", Messagebox.OK,Messagebox.EXCLAMATION);
			}
		}		
		pedidosColectivos.clear();
		Integer zonaId= null;
		if(zonaSeleccionada !=null){
			zonaId = zonaSeleccionada.getId();
		}
		pedidosColectivos.addAll(pedidoColectivoService.obtenerPedidosColectivosDeVendedorDeGrupo(usuarioLogueado.getId(),grupo.getId(),d,h,estadoSeleccionado,zonaId));
		this.binder.loadAll();
	}


	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}


	
	public void onVerPedido(PedidoColectivo p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", new ArrayList<Pedido>(p.getPedidosIndividuales().values()) );
		params.put("id",p.getId());
		params.put("grupo",grupo);
		params.put("exportar",true);
		Window w = (Window) Executions.createComponents("/verPedidosColectivos.zul", this.self, params);
		w.doModal();
		
	}

	
	public void onEditarZona(PedidoColectivo p, GrupoCC grupo){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", p);
		params.put("grupo", grupo);
		params.put("zonas", usuarioLogueado.getZonas());
		Window w = (Window) Executions.createComponents("/editarPedidoColectivo.zul", this.self, params);
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
		zonasListbox.setValue(null);
		pedidosColectivos = pedidoColectivoService.obtenerPedidosColectivosDeGrupo(grupo.getId());
		this.binder.loadAll();
	}
	
	public void entregarPedidoColectivo(PedidoColectivo p) throws EstadoPedidoIncorrectoException{
		p.entregarte();
		pedidoColectivoService.guardarPedidoColectivo(p);
		this.binder.loadAll();
	}
	
	public void prepararPedidoColectivo(PedidoColectivo p) throws EstadoPedidoIncorrectoException{
		p.preparado();
		pedidoColectivoService.guardarPedidoColectivo(p);
		this.binder.loadAll();
	}
	
	public void onPreguntarConfirmacionEntrega(final PedidoColectivo p){
		EventListener evt = new EventListener() {
			public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
				if(evt.getName().equals("onOK")){
					entregarPedidoColectivo(p);
				}
			}
		};
		Messagebox.show("¿Esta seguro que desea confirmar la entrega para este pedido colectivo?",
				"Confirmar", 
				Messagebox.OK|Messagebox.CANCEL,
				Messagebox.QUESTION,
				evt
				);
	}
	
	
	public void onPreguntarPerpararEntrega(final PedidoColectivo p){
		EventListener evt = new EventListener() {
			public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
				if(evt.getName().equals("onOK")){
					prepararPedidoColectivo(p);
				}
			}
		};
		Messagebox.show("¿Esta seguro que desea preparar la entrega para este pedido colectivo?",
				"Confirmar", 
				Messagebox.OK|Messagebox.CANCEL,
				Messagebox.QUESTION,
				evt
				);
	}
	
	public void onConfirmarEntrega(PedidoColectivo p) throws EstadoPedidoIncorrectoException {
	}
	
	public void mostrarAdvertenciaDeEntrega(PedidoColectivo p){
	}
	
	public void onSelect$listboxPedidos(SelectEvent evt) {
	}	
	
	public void onClick$exportarSeleccionados() throws Exception{
	}
	
	public void onClick$exportarTodosbtn() throws EstadoPedidoIncorrectoException{
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}
	
	public List<PedidoColectivo> getPedidosColectivos() {
		return pedidosColectivos;
	}

	public void setPedidosColectivos(List<PedidoColectivo> pedidosColectivos) {
		this.pedidosColectivos = pedidosColectivos;
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

	public void exportarPedidosCSV(List<PedidoColectivo> pPedidos) throws Exception {

	}


}

class HitorialPedidosColectivosEventListener implements EventListener<Event>{

	HistorialPedidosColectivosComposer composer;
	GrupoCC grupo;
	
	public HitorialPedidosColectivosEventListener(
			HistorialPedidosColectivosComposer historialPedidosColectivosComposer, GrupoCC grupo) {
		this.composer = historialPedidosColectivosComposer;
		this.grupo = grupo;
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
			composer.onEditarZona(p, grupo);
				
		}
		
		if(accion.equals(PedidosComposer.ACCION_ENTREGAR)){
			composer.onPreguntarConfirmacionEntrega(p);				
		}

			
	}
}
