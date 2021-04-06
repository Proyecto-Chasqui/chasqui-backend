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
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import chasqui.dtos.PedidoIndividualDTO;
import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoPedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.view.renders.ProductoPedidoRenderer;

@SuppressWarnings({"serial","deprecation","unused"})
public class VerProductoPedidoComposer  extends GenericForwardComposer<Component>{
	
  private Window window;
  private AnnotateDataBinder binder;
  Vendedor usuarioLogueado;

  private ProductoPedidoService productoPedidoService;
  private Producto producto;
  private List<ProductoPedido> productoPedidos;

  private Div listboxPedidosContainer;
  private Listbox listboxPedidos;
  private Listheader vencimientoColumn;
  private Label loadingLabel;
  private Label messageLabel;

  private ProductoPedidoQueryDTO queryDTO;

  private String message;
  private Integer cantidadTotal = 0;

  public void doAfterCompose(Component component) throws Exception{
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(usuarioLogueado != null){
			super.doAfterCompose(component);
			window = (Window) component;

      producto = (Producto) Executions.getCurrent().getArg().get("producto");
      productoPedidoService = (ProductoPedidoService) SpringUtil.getBean("productoPedidoService");

      queryDTO = new ProductoPedidoQueryDTO();
      queryDTO.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
		  
      listboxPedidos.setItemRenderer(new ProductoPedidoRenderer(window));

      binder = new AnnotateDataBinder(component);

      this.refresh();
		}
	}

  private void setLoading(Boolean show) {
    loadingLabel.setVisible(show);
  }

  private void showMessageLabel(Boolean show) {
    messageLabel.setVisible(show);
  }

  private void showVencimiento(Boolean show) {
    vencimientoColumn.setVisible(show);
  }

  private void showListPedidos(Boolean show) {
    listboxPedidosContainer.setVisible(show);
  }

  private void refreshCantidadTotal() {
    cantidadTotal = 0;
    if(productoPedidos == null || productoPedidos.size() == 0) {
      return;
    }
    for (ProductoPedido pp :productoPedidos) {
      cantidadTotal += pp.getCantidad();
    }
  }

  private void refresh() {
    List<Variante> variantes = producto.getVariantes();
    if(variantes.size() > 0) {
      queryDTO.setIdVariante(variantes.get(0).getId());
      showVencimiento(queryDTO.getEstado() != Constantes.ESTADO_PEDIDO_CONFIRMADO);
      setLoading(true);
      productoPedidos = productoPedidoService.obtener(queryDTO);
      setLoading(false);
      refreshCantidadTotal();
      if(productoPedidos.size() > 0) {
        showListPedidos(true);
        showMessageLabel(false);
        message = "";
      } else {
        showListPedidos(false);
        showMessageLabel(true);
        message = "sin pedidos";
      }
    } else {
      message = "Producto sin variante";
      showMessageLabel(true);
    }
    this.binder.loadAll();
  }

  private void openPedido (Pedido pedido) {
    HashMap<String,Object>params = new HashMap<String,Object>();
		params.put("pedido", pedido);
		Window w = (Window) Executions.createComponents("/verPedido.zul", this.self, params);
		w.doModal();
  }

  public void onClick$tabFiltroAbiertos() {
    queryDTO.setEstado(Constantes.ESTADO_PEDIDO_ABIERTO);
    this.refresh();
  
  }
  public void onClick$tabFiltroConfirmados() {
    queryDTO.setEstado(Constantes.ESTADO_PEDIDO_CONFIRMADO);
    this.refresh();
  }

  public void onDoubleClick$listboxPedidos() {
    Listitem item = listboxPedidos.getSelectedItem();
    if(item == null) {
      return;
    }
    Pedido pedido = (Pedido) item.getAttribute("pedido");
    if(pedido != null) {
      this.openPedido(pedido);
    }
  }

  public Integer getCantidadTotal() {
    return cantidadTotal;
  }

  public Producto getProducto() {
    return this.producto;
  }

  public List<ProductoPedido> getProductoPedidos() {
    return this.productoPedidos;
  }

  public String getMessage() {
    return this.message;
  }
}






