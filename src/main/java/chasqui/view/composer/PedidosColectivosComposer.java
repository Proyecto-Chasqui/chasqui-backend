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
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.HistorialGCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.view.renders.GrupoCCRenderer;
import chasqui.view.renders.PedidoRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class PedidosColectivosComposer  extends GenericForwardComposer<Component>{
	

	public static final String ACCION_VER = "VER";
	public static final String ACCION_EDITAR = "editar";
	public static final String ACCION_KEY = "accion";
	public static final String PEDIDO_KEY = "pedido";
	public static final String GRUPO_KEY = "grupo";
	public static final String ACCION_ENTREGAR = "entregar";
	public static final String ACCION_PREPARAR = "preparado";
	public static final Object ACCION_VER_HISTORIAL = "verHistorial";
	public static final String PEDIDO_KEY_HISTORIAL = "pedidoHistorial";
	
	private Datebox desde;
	private Datebox hasta;
	private Listbox listBoxGruposCC;
	private Button confirmarEntregabtn;
	public AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private ProductoService productoService;
	private GrupoService grupoService;
	private Combobox estadosListbox;
	private String estadoSeleccionado;
	private String grupalSeleccionado;
	private List<String>estados;
	private List<GrupoCC>gruposColectivos;
	Vendedor usuarioLogueado;
	private Paging paginal;
	private Button buscar;
	private List<Integer> idsSeleccionados;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("pedidosColectivosComposer", this);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			component.addEventListener(Events.ON_USER, new PedidoColectivoEventListener(this));
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			productoService = (ProductoService) SpringUtil.getBean("productoService");
			grupoService = (GrupoService) SpringUtil.getBean("grupoService");
			gruposColectivos  = grupoService.obtenerGruposDe(usuarioLogueado.getId());
			estados = Arrays.asList(Constantes.ESTADO_PEDIDO_ABIERTO,Constantes.ESTADO_PEDIDO_CONFIRMADO,Constantes.ESTADO_PEDIDO_ENTREGADO);
			
			binder = new AnnotateDataBinder(component);
			listBoxGruposCC.setItemRenderer(new GrupoCCRenderer((Window) component));
			binder.loadAll();
			
		}
	}
	
	public void onClick$buscar() throws VendedorInexistenteException{
		Date d = desde.getValue();
		Date h = hasta.getValue();
		
		if(d != null && h != null){
			if(h.before(d)){
				Messagebox.show("La fecha hasta debe ser posterior a la fecha desde", "Error", Messagebox.OK,Messagebox.EXCLAMATION);
			}
		}		
		gruposColectivos.clear();
		gruposColectivos.addAll(grupoService.obtenerGruposDe(usuarioLogueado.getId(),d,h,estadoSeleccionado));

		this.binder.loadAll();
	}


	public List<GrupoCC> getPedidos() {
		return gruposColectivos;
	}

	public void setPedidos(List<GrupoCC> pedidos) {
		this.gruposColectivos = pedidos;
	}


	
	public void onVerPedido(PedidoColectivo p){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", new ArrayList<Pedido>(p.getPedidosIndividuales().values()) );
		params.put("exportar", false);
		Window w = (Window) Executions.createComponents("/verPedidosColectivos.zul", this.self, params);
		w.doModal();
		
	}
	
	public void onVerHistorialDePedidos(List<PedidoColectivo> list, GrupoCC grupo) {
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("HistorialDePedidoColectivo", list );
		params.put("Grupo", grupo);
		Window w = (Window) Executions.createComponents("/verHistorialPedidosColectivos.zul", this.self, params);
		w.doModal();	
	}
	@Deprecated
	public void onEditarZona(PedidoColectivo p, GrupoCC grupo){
		HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedidoColectivo", p);
		params.put("grupo", grupo);
		params.put("zonas", usuarioLogueado.getZonas());
		Window w = (Window) Executions.createComponents("/editarPedidoColectivo.zul", this.self, params);
		w.doModal();
		this.binder.loadAll();
	}
	
	
	
	public String getEstadoSeleccionado() {
		return estadoSeleccionado;
	}

	public void setEstadoSeleccionado(String estadoSeleccionado) {
		this.estadoSeleccionado = estadoSeleccionado;
	}
	
	public void onClick$limpiarCamposbtn() throws VendedorInexistenteException{
		estadoSeleccionado = "";
		desde.setValue(null);
		hasta.setValue(null);
		estadosListbox.setValue("");
		gruposColectivos = grupoService.obtenerGruposDe(usuarioLogueado.getId());
		this.binder.loadAll();
	}
	
	public void onSelect$listboxPedidos(SelectEvent evt) {
		idsSeleccionados = new ArrayList<Integer>();		
		ArrayList<Object> ch =  new ArrayList<>(Arrays.asList(evt.getSelectedItems().toArray()));
		for(Object check: ch){
				idsSeleccionados.add(Integer.parseInt(((Listitem) check).getLabel()));
		}
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
	
	public void exportarPedidosCSV(List<Pedido> pPedidos) throws Exception {
	
	}
}

class PedidoColectivoEventListener implements EventListener<Event>{

	PedidosColectivosComposer composer;
	
	public PedidoColectivoEventListener(PedidosColectivosComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		
		Map<String,Object> params = (Map<String,Object>) event.getData();
		
		String accion = (String) params.get(PedidosColectivosComposer.ACCION_KEY);
		
		GrupoCC grupo = (GrupoCC) params.get(PedidosColectivosComposer.GRUPO_KEY);
		
		PedidoColectivo p = (PedidoColectivo) params.get(PedidosColectivosComposer.PEDIDO_KEY);
		
		HistorialGCC historial = (HistorialGCC) params.get(PedidosColectivosComposer.PEDIDO_KEY_HISTORIAL);
		
		if (accion.equals(PedidosColectivosComposer.ACCION_VER)) {
			composer.onVerPedido(p);
			
		}
		if (accion.equals(PedidosColectivosComposer.ACCION_VER_HISTORIAL)) {
			composer.onVerHistorialDePedidos(historial.getPedidosGrupales(), grupo);
			
		}
		if(accion.equals(PedidosColectivosComposer.ACCION_EDITAR)){
			composer.onEditarZona(p,grupo);
		}
		
	}
	
}

