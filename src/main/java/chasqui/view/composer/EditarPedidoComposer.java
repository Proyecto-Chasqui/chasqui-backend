package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

import chasqui.dtos.PedidoDTO;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ZonaService;

public class EditarPedidoComposer extends GenericForwardComposer<Component> {

	private AnnotateDataBinder binder;

	private PedidoDTO pedidoMostrado;
	
	private List<Zona> zonas;
	private Zona zonaSeleccionada;
	private PedidoService pedidoService;
    private Pedido pedido;
    private PedidosComposer pedidoscomposer;

	private Vendedor usuarioLogueado;

	private ZonaService zonaService;
    
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
		binder = new AnnotateDataBinder(c);

		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
		
		zonaService = (ZonaService) SpringUtil.getBean("zonaService");
		this.zonas = zonaService.buscarZonasBy(usuarioLogueado.getId());
		pedidoscomposer = (PedidosComposer) Executions.getCurrent().getSession().getAttribute("pedidosComposer");
		
		pedido = (Pedido) Executions.getCurrent().getArg().get("pedido");
		this.pedidoMostrado = buildPedidoDTO(pedido);
		
		this.binder.loadAll();
	}

	private PedidoDTO buildPedidoDTO(Pedido pedido) {

		PedidoDTO pedidodto = new PedidoDTO(pedido);
		
		return pedidodto;
	}

	public void onClick$confirmarEdicionbtn(){
		this.pedido.setZona(zonaSeleccionada);
		pedidoService.guardar(pedido);
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		pedidoscomposer.binder.loadAll();
		this.self.detach();
		//TODO como retornar este valor a la pantalla principal??
		
	}

	public void onClick$cancelarEdicionbtn(){
		Events.sendEvent(Events.ON_RENDER,this.self.getParent(),null);
		this.self.detach();
		//TODO como retornar este valor a la pantalla principal??
		
	}

}
