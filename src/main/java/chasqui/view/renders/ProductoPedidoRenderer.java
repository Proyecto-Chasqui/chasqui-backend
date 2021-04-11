package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Window;

import chasqui.model.Cliente;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.utils.DateRender;
import chasqui.view.composer.Constantes;

public class ProductoPedidoRenderer implements ListitemRenderer<ProductoPedido>{

	Window window;
	
	public ProductoPedidoRenderer(Component w){
		this.window = (Window) w;
	}
	
	public void render(Listitem item, ProductoPedido p, int arg2) throws Exception {
		Menubar menubar;
		Menu menu;
		Listcell idCell = new Listcell(p.getId().toString());
    Listcell idPedidoCell = new Listcell(p.getId().toString());
		Listcell nombreCell = new Listcell(p.getNombreProducto());
		Listcell cantidadCell = new Listcell(p.getCantidad().toString());
    Listcell fechaPedidoCell = new Listcell();
    Listcell fechaVencimientoCell = new Listcell();
    Listcell estadoPedidoCell = new Listcell();
    Listcell esGrupalCell = new Listcell();

    Pedido pedido = p.getPedido();
    if(pedido != null) {
      fechaPedidoCell.setLabel(DateRender.renderDateTime(pedido.getFechaCreacion()));
      fechaVencimientoCell.setLabel(DateRender.renderDateTime(pedido.getFechaDeVencimiento()));
      estadoPedidoCell.setLabel(pedido.getEstado());
      Cliente cliente = pedido.getCliente();
      nombreCell.setLabel(cliente.getNombre() + " " +cliente.getApellido());
      idPedidoCell.setLabel(pedido.getId().toString());
      esGrupalCell.setLabel(pedido.getPerteneceAPedidoGrupal() ? "Si" : "No");

    } else {
      nombreCell.setLabel("?");
      fechaPedidoCell.setLabel("?");
      fechaVencimientoCell.setLabel("?");
      estadoPedidoCell.setLabel("?");
      idPedidoCell.setLabel("?");
      esGrupalCell.setLabel("?");
    }

    Boolean showVencimiento = pedido == null || pedido.getEstado() != Constantes.ESTADO_PEDIDO_CONFIRMADO;

    idCell.setParent(item);
    idCell.setStyle("color:#999;");
		cantidadCell.setParent(item);
    nombreCell.setParent(item);
    idPedidoCell.setParent(item);
		fechaPedidoCell.setParent(item);
    if(showVencimiento) {
      fechaVencimientoCell.setParent(item);
    }
		esGrupalCell.setParent(item);
		estadoPedidoCell.setParent(item);

    item.setAttribute("pedido", pedido);
	}
	
}
