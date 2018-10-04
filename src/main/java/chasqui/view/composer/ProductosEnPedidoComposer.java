package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;

import chasqui.dtos.PedidoDTO;
import chasqui.dtos.VarianteDTO;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;

@SuppressWarnings("deprecation")
public class ProductosEnPedidoComposer extends GenericForwardComposer<Component>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4655478743445258020L;
	private AnnotateDataBinder binder;
	
	private PedidoDTO pedidoMostrado; 
	
	private Double total = 0.0;
	private Label callelbl;
	private Label alturalbl;
	private Label postallbl;
	private Label departamentolbl;
	private Label localidadlbl;
	private Label informacion;
	private Grid gridDireccion;

	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		binder = new AnnotateDataBinder(c);
		Pedido p = (Pedido) Executions.getCurrent().getArg().get("pedido");

		
		this.pedidoMostrado = buildPedidoDTO( p);
//		HashMap<String,List<ProductoPedido>>param = new HashMap<String,List<ProductoPedido>>();
//		param.put(p.getUsuarioCreador(), new ArrayList<ProductoPedido>(p.getProductosEnPedido()));
//		
		//pedidos = toDTO(param);
//		for(PedidoDTO t : pedidos){
//			total =+ t.getMontoTotal();
//			
//		}
		
		this.binder.loadAll();
	}
	

	/**
	 * TODO Este metodo solo maneja pedidos individuales
	 * cambiar para que acepte pedidos colectivoss
	 */
	
	private PedidoDTO buildPedidoDTO(Pedido pedido){

				List<ProductoPedido> listaProductosEnPedido = new ArrayList();
				listaProductosEnPedido.addAll(pedido.getProductosEnPedido());		
		PedidoDTO pedidodto = new PedidoDTO(pedido);
	    pedidodto.addPedidoIndividual(pedido.getCliente().getEmail(), listaProductosEnPedido);
			
		return pedidodto;
	}
	
	private List<List<String>> getVariantesExportables(List<VarianteDTO> variantes) {
		List<List<String>> resultados = new ArrayList<List<String>>();
		
		for(VarianteDTO variante: variantes){
			resultados.add(Arrays.asList(variante.getNombreProducto(),variante.getNombreVariante(),variante.getCantidad().toString(),variante.getPrecio().toString()));
		}

		return resultados;
	}



	public PedidoDTO getPedidoMostrado() {
		return pedidoMostrado;
	}

	public void setPedidoMostrado(PedidoDTO pedidoMostrado) {
		this.pedidoMostrado = pedidoMostrado;
	}
	
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	
	
	
	
	

}

