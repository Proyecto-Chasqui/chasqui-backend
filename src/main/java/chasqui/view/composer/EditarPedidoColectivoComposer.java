package chasqui.view.composer;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

import chasqui.dtos.PedidoDTO;
import chasqui.model.GrupoCC;
import chasqui.model.PedidoColectivo;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.ZonaService;

public class EditarPedidoColectivoComposer extends GenericForwardComposer<Component> {

	private AnnotateDataBinder binder;

	private PedidoDTO pedidoMostrado;
	
	private List<Zona> zonas;
	private Zona zonaSeleccionada;
    private PedidoColectivo pedido;
    private HistorialPedidosColectivosComposer comp;
	private GrupoCC grupo;

	private PedidoColectivoService pedidoColectivoService;
	private ZonaService zonaService;

	private Vendedor usuarioLogueado;
	
	private GrupoService grupoService;
    
	public PedidoDTO getPedidoMostrado() {
		return pedidoMostrado;
	}

	public void setPedidoMostrado(PedidoDTO pedidoMostrado) {
		this.pedidoMostrado = pedidoMostrado;
	}

	public List<Zona> getZonas() {
		return zonas;
	}

	public void setZonas(List<Zona> zonas) {
		this.zonas = zonas;
	}

	public Zona getZonaSeleccionada() {
		return zonaSeleccionada;
	}

	public void setZonaSeleccionada(Zona zonaSeleccionada) {
		this.zonaSeleccionada = zonaSeleccionada;
	}

	public String getZonaActualDelPedido(){
		if (this.pedidoMostrado.getZona()==null) {
			return Constantes.ZONA_NO_DEFINIDA;
		}
		else return this.pedidoMostrado.getZona().getNombre();
	}
	
	
	
	public void doAfterCompose(Component c) throws Exception {
		super.doAfterCompose(c);
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		comp = (HistorialPedidosColectivosComposer) Executions.getCurrent().getSession().getAttribute("historialPedidosColectivosComposer");
		binder = new AnnotateDataBinder(c);
		pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
		grupoService = (GrupoService) SpringUtil.getBean("grupoService");
		zonaService = (ZonaService) SpringUtil.getBean("zonaService");
		
		pedido = (PedidoColectivo) Executions.getCurrent().getArg().get("pedidoColectivo");
		this.zonas = pedido.getColectivo().getVendedor().getZonas();
		grupo = (GrupoCC) Executions.getCurrent().getArg().get("grupo");
		this.pedidoMostrado = buildPedidoDTO(pedido,grupo);
		
		this.binder.loadAll();
	}


	private PedidoDTO buildPedidoDTO(PedidoColectivo pedido,GrupoCC grupo) {

		PedidoDTO pedidodto = new PedidoDTO(pedido,grupo);
		
		return pedidodto;
	}

	public void onClick$confirmarEdicionbtn(){
		this.pedido.setZona(zonaSeleccionada);
		grupoService.guardarGrupo(grupo);
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		comp.binder.loadAll();
		this.self.detach();
		
	}
	
	private void cambiarZonaAPedido(Integer id, Zona zona) {
		for(PedidoColectivo pc : this.grupo.getHistorial().getPedidosGrupales()) {
			if(pc.getId() == id) {
				pc.setZona(zona);
			}
		}
	}

	public void onClick$cancelarEdicionbtn(){
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		this.self.detach();		
	}

}