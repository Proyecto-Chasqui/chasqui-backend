package chasqui.view.composer;

import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import chasqui.dtos.PedidosGrupalesDTO;
import chasqui.model.IPedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.PedidoService;
import chasqui.view.renders.PedidoColectivoRenderer;
import chasqui.view.renders.PedidoRenderer;

public class PreviewPedidosIndividualesComposer extends GenericForwardComposer<Component>{

	private Listbox listboxPedidos;
	public AnnotateDataBinder binder;
	private PedidoService pedidoService;
	private List<IPedido>pedidos;
	Vendedor usuarioLogueado;
	private Paging paginal;
	private Button buscar;
	private Window window;
//	private Integer maximaPaginaVisitada = 1;
	private List<PedidoColectivo> pedidosColectivos;
	
	public void doAfterCompose(Component component) throws Exception{
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("pedidosComposer", this);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
			pedidos  = (List<IPedido>) Executions.getCurrent().getArg().get("PedidosIndividuales");
			PedidosGrupalesDTO pdto= ((PedidosGrupalesDTO) Executions.getCurrent().getArg().get("PedidosColectivos"));
			if(pdto !=null) {
				pedidosColectivos = pdto.getPedidos();
			}
			binder = new AnnotateDataBinder(component);
			window = (Window) component;
			if(pedidos != null) {
				listboxPedidos.setItemRenderer(new PedidoRenderer((Window) component));
			}else {
				PedidoColectivoRenderer pr = new PedidoColectivoRenderer((Window) component);
				pr.setDatagrupo(((PedidosGrupalesDTO) Executions.getCurrent().getArg().get("PedidosColectivos")).getPedidoGrupo());
				listboxPedidos.setItemRenderer(pr);
			}
			binder.loadAll();
			
		}
	}
	

	public List<IPedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<IPedido> pedidos) {
		this.pedidos = pedidos;
	}


	public List<PedidoColectivo> getPedidosColectivos() {
		return pedidosColectivos;
	}


	public void setPedidosColectivos(List<PedidoColectivo> pedidosColectivos) {
		this.pedidosColectivos = pedidosColectivos;
	}


}
