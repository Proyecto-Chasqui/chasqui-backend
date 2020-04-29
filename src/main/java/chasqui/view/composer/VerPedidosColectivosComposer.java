package chasqui.view.composer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.zkoss.zul.Tab;
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
	private Window window;
	private Cliente clienteDelPedido;
	private PedidoColectivo pedidoColectivo;
	private String direccion;
	private String zona;
	private String nombrePr;
	private String comentario;
	private boolean tieneEntregaADomicilio = false;
	private boolean tienePuntoDeRetiro = false;
	private boolean estaConfirmado = false;
	private boolean tieneRespuestas = false;
	private Map<String,String> respuestas = new HashMap<String,String>();
	private Tab tabdatosusuario;
	
	public void doAfterCompose(Component component) throws Exception{
		idsSeleccionados = new ArrayList<Integer>();
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			window = (Window) component;
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
			pedidoColectivo = pedidoColectivoService.obtenerPedidoColectivoPorID(idPedidoColectivo);
			clienteDelPedido = pedidoColectivo.getColectivo().getAdministrador();
			completardatos();
			binder = new AnnotateDataBinder(component);
			listBoxPedidosColectivos.setItemRenderer(new PedidoRenderer((Window) component));
			binder.loadAll();
			
		}
	}
	
	private void completardatos() {
		estaConfirmado = validarConfirmado();
		respuestas = pedidoColectivo.getRespuestasAPreguntas();
		tieneRespuestas = respuestas.size() > 0;
		direccion = "N/D";
		if(pedidoColectivo.getColectivo().isEsNodo()) {
			tabdatosusuario.setLabel("Datos del coordinador");
		}
		if(pedidoColectivo.getDireccionEntrega() != null) {
			tieneEntregaADomicilio = true;
			direccion = pedidoColectivo.getDireccionEntrega().toString();
		}else {
			tieneEntregaADomicilio = false;
		}
		if(pedidoColectivo.getZona() != null) {
			zona = pedidoColectivo.getZona().toString();
		}else {
			zona = "No definida";
		}
		if(pedidoColectivo.getPuntoDeRetiro() != null) {
			tienePuntoDeRetiro = true;
			direccion = pedidoColectivo.getPuntoDeRetiro().getDireccion().toString();
			nombrePr = pedidoColectivo.getPuntoDeRetiro().getNombre();
		}else {
			nombrePr = "N/D";
		}
		if(pedidoColectivo.getComentario() != null) {
			comentario = pedidoColectivo.getComentario();
		}else {
			comentario = "";
		}
		
	}
	
	private boolean validarConfirmado() {
		return pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) || pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO) || pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO);
	}

	public void onClick$ubicarEnMapa() {
		if(tieneEntregaADomicilio) {
			String lat = pedidoColectivo.getDireccionEntrega().getLatitud();
			String lng = pedidoColectivo.getDireccionEntrega().getLongitud();
			Executions.getCurrent().sendRedirect("https://www.google.com/maps?q="+ lat+","+lng, "_blank");
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
	
	public void onClick$exportarTodosbtn(){	
		
		try {
			Clients.showBusy(window,"Generando el archivo, por favor espere...");
			PedidoColectivo pedidoc = pedidoColectivoService.obtenerPedidoColectivoPorID(idPedidoColectivo);
			/* eliminar cuando se confirme la funcionalidad completa de nodos.
			if(pedidoc.getColectivo().getVendedor().getEstrategiasUtilizadas().isUtilizaIncentivos() && pedidoc.getColectivo().isEsNodo()) {
				this.pedidosDentroDeColectivo = new ArrayList<Pedido>(pedidoc.getPedidosIndividuales().values());
				List<Pedido> pedidomerge = this.pedidoColectivoMergeParaIncentivos(pedidosDentroDeColectivo,pedidoc);
				this.pedidosDentroDeColectivo = new ArrayList<Pedido>(pedidoc.getPedidosIndividuales().values());
				alterarPreciosDelPedidoDelAdmin(pedidosDentroDeColectivo,pedidoc.getColectivo().getAdministrador().getEmail());
				pedidomerge.addAll(pedidosDentroDeColectivo);
				pedidomerge = obtenerSoloConfirmados(pedidomerge);
				export.exportColectivos(pedidomerge);
			}else {
			*/
				this.pedidosDentroDeColectivo = new ArrayList<Pedido>(pedidoc.getPedidosIndividuales().values());
				List<Pedido> pedidomerge = this.pedidoColectivoMerge(pedidosDentroDeColectivo,pedidoc);
				pedidomerge.addAll(pedidoColectivoService.obtenerPedidoColectivoPorID(idPedidoColectivo).getPedidosIndividuales().values());
				pedidomerge = obtenerSoloConfirmados(pedidomerge);
				export.exportColectivos(pedidomerge);
			//}
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
	
	private List<Pedido> alterarPreciosDelPedidoDelAdmin(List<Pedido> pedidosmerge, String email) {
		List<Pedido> pedidos = new ArrayList<Pedido> ();
		for(Pedido p: pedidosmerge) {
			if(p.getCliente().getEmail().equals(email)) {
				alterarPreciosConIncentivo(p);
				pedidos.add(p);
			}else {
				pedidos.add(p);
			}
		}
		return pedidos;
	}

	private void alterarPreciosConIncentivo(Pedido p) {
		for(ProductoPedido pp : p.getProductosEnPedido()){
			pp.setPrecio(pp.getPrecio() + pp.getIncentivo());
		}
	}

	private List<Pedido> pedidoColectivoMergeParaIncentivos(List<Pedido> pedidosDentroDeColectivo,
			PedidoColectivo pedidoColectivo) throws EstadoPedidoIncorrectoException {
		List<Pedido>pedidoGrupalCompleto = new ArrayList<Pedido>();
		Pedido pedidogeneralgrupal = new Pedido(usuarioLogueado,pedidoColectivo.getColectivo().getAdministrador(),false, new DateTime());
		pedidogeneralgrupal.setDireccionEntrega(pedidoColectivo.getDireccionEntrega());
		pedidogeneralgrupal.setPuntoDeRetiro(pedidoColectivo.getPuntoDeRetiro());
		pedidogeneralgrupal.setComentario(pedidoColectivo.getComentario());
		pedidogeneralgrupal.setZona(pedidoColectivo.getZona());
		pedidogeneralgrupal.setRespuestasAPreguntas(pedidoColectivo.getRespuestasAPreguntas());
		String emailadmin = pedidoColectivo.getColectivo().getAdministrador().getEmail();
		for(Pedido p : pedidosDentroDeColectivo){
			if(this.pedidoEnEstadoConfirmado(p)){
				if(p.getCliente().getEmail().equals(emailadmin)) {
					for(ProductoPedido pp : p.getProductosEnPedido()){
						ProductoPedido ppcopia = copiarProducto(pp);
						ppcopia.setPrecio(ppcopia.getPrecio() + ppcopia.getIncentivo());
						pedidogeneralgrupal.agregarProductoPedidoConValidaciones(ppcopia, null);
					}
				}else {
					for(ProductoPedido pp : p.getProductosEnPedido()){
						ProductoPedido ppcopia = copiarProducto(pp);
						pedidogeneralgrupal.agregarProductoPedidoConValidaciones(ppcopia, null);
					}
				}
			}
		}
		
		pedidogeneralgrupal.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
		pedidoGrupalCompleto.add(pedidogeneralgrupal);
		return pedidoGrupalCompleto;
	}

	private void alterarPreciosDePedidoAdministrador(List<Pedido> pedidosDentroDeColectivo2, String emailadmin) {
		// TODO Auto-generated method stub
		
	}

	private List<Pedido> obtenerSoloConfirmados(List<Pedido> pedidosTotales){
		List<Pedido> pedidos = new ArrayList<Pedido> ();
		
		for(Pedido p: pedidosTotales){
			if(this.pedidoEnEstadoConfirmado(p)){
				pedidos.add(p);
			}
		}
		return pedidos;
	}
	

	private boolean pedidoEnEstadoConfirmado(Pedido p) {
		return p.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) || p.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)
				|| p.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO);
	}
	
	private List<Pedido> pedidoColectivoMerge(List<Pedido> pedidosgenerados, PedidoColectivo pedidoColectivo) throws EstadoPedidoIncorrectoException {
		List<Pedido>pedidoGrupalCompleto = new ArrayList<Pedido>();
		Pedido pedidogeneralgrupal = new Pedido(usuarioLogueado,pedidoColectivo.getColectivo().getAdministrador(),false, new DateTime());
		pedidogeneralgrupal.setDireccionEntrega(pedidoColectivo.getDireccionEntrega());
		pedidogeneralgrupal.setPuntoDeRetiro(pedidoColectivo.getPuntoDeRetiro());
		pedidogeneralgrupal.setComentario(pedidoColectivo.getComentario());
		pedidogeneralgrupal.setZona(pedidoColectivo.getZona());
		pedidogeneralgrupal.setRespuestasAPreguntas(pedidoColectivo.getRespuestasAPreguntas());
		for(Pedido p : pedidosgenerados){
			if(this.pedidoEnEstadoConfirmado(p)){
				for(ProductoPedido pp : p.getProductosEnPedido()){
					ProductoPedido ppcopia = copiarProducto(pp);
					//si falla cambiar a "agregarProductoPedido(ppcopia,null)";
					pedidogeneralgrupal.agregarProductoPedidoConValidaciones(ppcopia, null);
				}
			}
		}
		
		pedidogeneralgrupal.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
		pedidoGrupalCompleto.add(pedidogeneralgrupal);
		return pedidoGrupalCompleto;
		
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
		ppc.setIncentivo(pp.getIncentivo());
		return ppc;
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

	public Cliente getClienteDelPedido() {
		return clienteDelPedido;
	}

	public void setClienteDelPedido(Cliente clienteDelPedido) {
		this.clienteDelPedido = clienteDelPedido;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public boolean isTieneEntregaADomicilio() {
		return tieneEntregaADomicilio;
	}

	public void setTieneEntregaADomicilio(boolean tieneEntregaADomicilio) {
		this.tieneEntregaADomicilio = tieneEntregaADomicilio;
	}

	public boolean isTienePuntoDeRetiro() {
		return tienePuntoDeRetiro;
	}

	public void setTienePuntoDeRetiro(boolean tienePuntoDeRetiro) {
		this.tienePuntoDeRetiro = tienePuntoDeRetiro;
	}

	public String getNombrePr() {
		return nombrePr;
	}

	public void setNombrePr(String nombrePr) {
		this.nombrePr = nombrePr;
	}

	public boolean isEstaConfirmado() {
		return estaConfirmado;
	}

	public void setEstaConfirmado(boolean estaConfirmado) {
		this.estaConfirmado = estaConfirmado;
	}

	public boolean isTieneRespuestas() {
		return tieneRespuestas;
	}

	public void setTieneRespuestas(boolean tieneRespuestas) {
		this.tieneRespuestas = tieneRespuestas;
	}

	public Map<String, String> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(Map<String, String> respuestas) {
		this.respuestas = respuestas;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
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




