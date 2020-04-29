package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import chasqui.dtos.PedidoDTO;
import chasqui.dtos.VarianteDTO;
import chasqui.model.Cliente;
import chasqui.model.GrupoCC;
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
	private Cliente clienteDelPedido;
	
	private Double total = 0.0;
	private Label callelbl;
	private Label alturalbl;
	private Label postallbl;
	private Label departamentolbl;
	private Label localidadlbl;
	private Label informacion;
	private Grid gridDireccion;
	private Map<String,String> respuestasAPreguntas;
	private Tab tabdetallepreguntas;
	private boolean tieneEntregaADomicilio;
	private boolean tienePuntoDeRetiro;
	private Listcell labelNombrePR;
	private Listcell labelDireccionPR;
	private String lng = "";
	private String lat = "";
	private boolean esIndividualConfirmado;
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		binder = new AnnotateDataBinder(c);
		Pedido p = (Pedido) Executions.getCurrent().getArg().get("pedido");

		
		this.pedidoMostrado = buildPedidoDTO( p);
		completarDatosPR(p);
		completarDatosDir(p);
		this.binder.loadAll();
	}


	private void completarDatosDir(Pedido p) {
		if(tieneEntregaADomicilio) {
			this.lat = p.getDireccionEntrega().getLatitud();
			this.lng = p.getDireccionEntrega().getLongitud();
			setEsIndividualConfirmado(!p.getPerteneceAPedidoGrupal() && estaConfirmado(p));
		}
	}


	private boolean estaConfirmado(Pedido p) {
		String estado = p.getEstado();
		return estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)|| estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO) || estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO);
	}


	private void completarDatosPR(Pedido p) {
		// TODO Auto-generated method stub
		if(tienePuntoDeRetiro) {
			this.labelDireccionPR.setLabel(p.getPuntoDeRetiro().getDireccion().toString());
			this.labelNombrePR.setLabel(p.getPuntoDeRetiro().getNombre());
			setEsIndividualConfirmado(!p.getPerteneceAPedidoGrupal() && estaConfirmado(p));
		}else {
			this.labelDireccionPR.setLabel("N/D");
			this.labelNombrePR.setLabel("N/D");
		}
	}
	
	public void onClick$ubicarEnMapa(){
		Executions.getCurrent().sendRedirect("https://www.google.com/maps?q="+ this.lat+","+this.lng, "_blank");
	}

	private PedidoDTO buildPedidoDTO(Pedido pedido){

				List<ProductoPedido> listaProductosEnPedido = new ArrayList();
				listaProductosEnPedido.addAll(pedido.getProductosEnPedido());		
		PedidoDTO pedidodto = new PedidoDTO(pedido);
// eliminar cuando se confirme la funcionalidad de nodos
//		if(pedido.getPerteneceAPedidoGrupal()) {
//			GrupoCC colectivo = pedido.getPedidoColectivo().getColectivo();
//			boolean esNodo = colectivo.isEsNodo();
//			boolean usaIncentivo = colectivo.getVendedor().getEstrategiasUtilizadas().isUtilizaIncentivos();
//			boolean esAdmin = colectivo.getAdministrador().getEmail().equals(pedido.getCliente().getEmail());
//			if(esNodo) {
//				if(usaIncentivo) {
//					pedidodto.addPedidoIndividual(pedido.getCliente().getEmail(), listaProductosEnPedido, esAdmin);
//				}else {
//					pedidodto.addPedidoIndividual(pedido.getCliente().getEmail(), listaProductosEnPedido);
//				}
//			}else {
//				pedidodto.addPedidoIndividual(pedido.getCliente().getEmail(), listaProductosEnPedido);
//			}
//		}else {
			pedidodto.addPedidoIndividual(pedido.getCliente().getEmail(), listaProductosEnPedido);
//		}
			if(pedido.getRespuestasAPreguntas() != null && (pedido.getRespuestasAPreguntas().size() > 0)) {
				this.respuestasAPreguntas = pedido.getRespuestasAPreguntas();
				this.tabdetallepreguntas.setVisible(true);
			}else {
				this.respuestasAPreguntas = new HashMap<String,String>();
				this.tabdetallepreguntas.setVisible(false);
			}
			clienteDelPedido = pedido.getCliente();
			tieneEntregaADomicilio = pedido.getDireccionEntrega() != null;
			tienePuntoDeRetiro = pedido.getPuntoDeRetiro() != null;
			
			
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




	public Map<String,String> getRespuestasAPreguntas() {
		return respuestasAPreguntas;
	}


	public void setRespuestasAPreguntas(Map<String,String> respuestasAPreguntas) {
		this.respuestasAPreguntas = respuestasAPreguntas;
	}


	public Tab getTabdetallepreguntas() {
		return tabdetallepreguntas;
	}


	public void setTabdetallepreguntas(Tab tabdetallepreguntas) {
		this.tabdetallepreguntas = tabdetallepreguntas;
	}


	public Cliente getClienteDelPedido() {
		return clienteDelPedido;
	}


	public void setClienteDelPedido(Cliente clienteDelPedido) {
		this.clienteDelPedido = clienteDelPedido;
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


	public Listcell getLabelNombrePR() {
		return labelNombrePR;
	}


	public void setLabelNombrePR(Listcell labelNombrePR) {
		this.labelNombrePR = labelNombrePR;
	}


	public Listcell getLabelDireccionPR() {
		return labelDireccionPR;
	}


	public void setLabelDireccionPR(Listcell labelDireccionPR) {
		this.labelDireccionPR = labelDireccionPR;
	}


	public boolean isEsIndividualConfirmado() {
		return esIndividualConfirmado;
	}


	public void setEsIndividualConfirmado(boolean esIndividualConfirmado) {
		this.esIndividualConfirmado = esIndividualConfirmado;
	}


	
	
	
	
	

}

