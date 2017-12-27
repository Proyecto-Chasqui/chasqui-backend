package chasqui.view.composer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Window;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import chasqui.dtos.PedidoIndividualDTO;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.view.renders.GrupoCCRenderer;
import chasqui.view.renders.PedidoRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class VerPedidosColectivosComposer  extends GenericForwardComposer<Component>{
	

	public static final String ACCION_VER = "VER";
	public static final String ACCION_EDITAR = "editar";
	public static final String ACCION_KEY = "accion";
	public static final String PEDIDO_KEY = "pedido";
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listBoxPedidosColectivos;
	private Button confirmarEntregabtn;
	private AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private ProductoService productoService;
	private GrupoService grupoService;
	private PedidoColectivoService pedidoColectivoService;
	private Combobox estadosListbox;
	private String estadoSeleccionado;
	private String grupalSeleccionado;
	private List<String>estados;
	private List<Pedido>pedidosDentroDeColectivo;
	private List<Pedido> pedidosCopiaSinFiltrar= new ArrayList<Pedido>();
	//private List<Pedido> pedidosCopiaFiltrados= new ArrayList<Pedido>();
	Vendedor usuarioLogueado;
	private Paging paginal;
	private Button buscar;
	private List<Integer> idsSeleccionados;
	private XlsExporter export  = new XlsExporter();
	private Integer idPedidoColectivo;
	private GrupoCC grupo;
	private Boolean exportar;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			component.addEventListener(Events.ON_USER, new VerPedidoColectivoEventListener(this));
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			productoService = (ProductoService) SpringUtil.getBean("productoService");
			pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_ABIERTO,Constantes.ESTADO_PEDIDO_CANCELADO,Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO);
			pedidosDentroDeColectivo = (List<Pedido>) Executions.getCurrent().getArg().get("pedidoColectivo");
			grupo = (GrupoCC) Executions.getCurrent().getArg().get("grupo");
			exportar = (Boolean) Executions.getCurrent().getArg().get("exportar");
			idPedidoColectivo= (Integer) Executions.getCurrent().getArg().get("id");
			pedidosCopiaSinFiltrar.addAll(pedidosDentroDeColectivo);
			binder = new AnnotateDataBinder(component);
			listBoxPedidosColectivos.setItemRenderer(new PedidoRenderer((Window) component));
			binder.loadAll();
			
		}
	}
	
	public void onClick$buscar(){

	}

	public List<Pedido> getPedidosDentroDeColectivo() {
		return pedidosDentroDeColectivo;
	}

	public void setPedidosDentroDeColectivo(List<Pedido> pedidos) {
		this.pedidosDentroDeColectivo = pedidos;
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
		desde.setValue(null);
		hasta.setValue(null);
		estadosListbox.setValue("");
		pedidosDentroDeColectivo =  (List<Pedido>) Executions.getCurrent().getArg().get("pedidoColectivo");
		this.binder.loadAll();
	}

	public void onClick$confirmarEntregabtn() throws EstadoPedidoIncorrectoException{
	}
	
	public void onSelect$listboxPedidos(SelectEvent evt) {
		idsSeleccionados = new ArrayList<Integer>();		
		ArrayList<Object> ch =  new ArrayList<>(Arrays.asList(evt.getSelectedItems().toArray()));
		for(Object check: ch){
				idsSeleccionados.add(Integer.parseInt(((Listitem) check).getLabel()));
		}
	}	
	
	public void onClick$exportarSeleccionados() throws Exception{
//		List<Pedido> pedidosSeleccionados = new ArrayList<Pedido>();
//		for(Pedido p: pedidosDentroDeColectivo){
//			for(Integer id : idsSeleccionados){
//				if(p.getId()==id){
//					pedidosSeleccionados.add(p);
//				}
//			}
//		}
//		List<Pedido> pedidomerge = this.pedidoColectivoMerge(pedidosSeleccionados);
//		export.fullexport(pedidomerge);
	}
	
	public void onClick$exportarTodosbtn(){
		try {
			PedidoColectivo pedidoc = pedidoColectivoService.obtenerPedidoColectivoPorID(idPedidoColectivo);
			List<Pedido> pedidomerge = this.pedidoColectivoMerge(pedidosDentroDeColectivo,pedidoc);
			this.pedidosDentroDeColectivo = new ArrayList<Pedido>(pedidoc.getPedidosIndividuales().values());
			pedidomerge.addAll(pedidosDentroDeColectivo);
			pedidomerge = obtenerSoloConfirmados(pedidomerge);
			export.exportColectivos(pedidomerge);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.binder.loadAll();
	}
	
	private List<Pedido> obtenerSoloConfirmados(List<Pedido> pedidosTotales){
		List<Pedido> pedidos = new ArrayList<Pedido> ();
		
		for(Pedido p: pedidosTotales){
			if(p.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
				pedidos.add(p);
			}
		}
		
		return pedidos;
	}
	
	private List<Pedido> pedidoColectivoMerge(List<Pedido> pedidosgenerados, PedidoColectivo pedidoColectivo) throws EstadoPedidoIncorrectoException {
		List<Pedido>pedidoGrupalCompleto = new ArrayList<Pedido>();
		Pedido pedidogeneralgrupal = new Pedido(usuarioLogueado,grupo.getAdministrador(),false, new DateTime());
		pedidogeneralgrupal.setDireccionEntrega(pedidoColectivo.getDireccionEntrega());
		pedidogeneralgrupal.setComentario(pedidoColectivo.getComentario());
		pedidogeneralgrupal.setZona(pedidoColectivo.getZona());
		
		for(Pedido p : pedidosgenerados){
			if(p.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
				for(ProductoPedido pp : p.getProductosEnPedido()){
					pedidogeneralgrupal.agregarProductoPedido(pp, null);
				}
			}
		}
		
		pedidogeneralgrupal.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
		pedidoGrupalCompleto.add(pedidogeneralgrupal);
		return pedidoGrupalCompleto;
		
	}


	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}

	public Boolean getExportar() {
		return exportar;
	}

	public void setExportar(Boolean exportar) {
		this.exportar = exportar;
	}
	
	
	}

class VerPedidoColectivoEventListener implements EventListener<Event>{

	VerPedidosColectivosComposer composer;
	
	public VerPedidoColectivoEventListener(VerPedidosColectivosComposer verPedidosColectivosComposer){
		this.composer = verPedidosColectivosComposer;
	}
	
	public void onEvent(Event event) throws Exception {
		
		Map<String,Object> params = (Map<String,Object>) event.getData();
		
		String accion = (String) params.get(VerPedidosColectivosComposer.ACCION_KEY);
		
		Pedido p = (Pedido) params.get(VerPedidosColectivosComposer.PEDIDO_KEY);
		
		if (accion.equals(PedidosColectivosComposer.ACCION_VER)) {
			composer.onVerPedido(p);
			
		} else{
			composer.onEditarZona(p);
				
		}
			
	}
	
}


