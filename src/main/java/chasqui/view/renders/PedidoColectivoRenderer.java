package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;
import chasqui.view.composer.PedidosColectivosComposer;
import chasqui.view.composer.PedidosComposer;

public class PedidoColectivoRenderer implements ListitemRenderer<PedidoColectivo>{
	private Window pedidoWindow;
	private Map<Integer,GrupoCC> datagrupo;
	private Listcell celdaId,celdaUsr, celdaAdmn, celdaFechaCreacion, celdaFechaCierre, celdaZona, celdaMontoMinimo, celdaMontoActual, celdaEstado,
			celdaDireccion, celdaBotones;

	public PedidoColectivoRenderer(Window w) {
		pedidoWindow = w;
	}

	public void render(Listitem item, final PedidoColectivo pedidoColectivo, int arg2) throws Exception {

		celdaId = new Listcell(String.valueOf(pedidoColectivo.getId()));
		
		if(pedidoColectivo.getColectivo() !=null) {
			celdaUsr = new Listcell(pedidoColectivo.getColectivo().getAlias());
			celdaAdmn = new Listcell(pedidoColectivo.getColectivo().getAdministrador().getEmail());
		}
		
		if(pedidoColectivo.estaAbierto() || 
				 pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO) || 
				 pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)) {
			celdaFechaCierre = new Listcell("N/D");
		}else {
			if(pedidoColectivo.getFechaModificacion() != null) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Date d1 = new Date(pedidoColectivo.getFechaModificacion().getMillis());
				celdaFechaCierre = new Listcell(format.format(d1));
			}else {
				celdaFechaCierre = new Listcell("N/D(error 68)");
			}
		}
		if(pedidoColectivo.getFechaCreacion() != null){
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date(pedidoColectivo.getFechaCreacion().getMillis());
			celdaFechaCreacion = new Listcell(format.format(d));
		}else{
			celdaFechaCreacion = new Listcell("Sin Fecha");
		}

		// -----------------Mostrar la zona
		Zona zonaPedido = pedidoColectivo.getZona();
		if (zonaPedido == null) {
			celdaZona = new Listcell(Constantes.ZONA_NO_DEFINIDA);
			celdaZona.setStyle("color:red;");
		} else {
			celdaZona = new Listcell(zonaPedido.getNombre());
		}

		celdaMontoMinimo = new Listcell("0");

		celdaMontoActual = new Listcell(String.valueOf(pedidoColectivo.getMontoTotal()));
//		if (pedidoColectivo.getMontoMinimo() < pedidoColectivo.getMontoActual()) {
//			celdaMontoActual.setStyle("color:green;");
//		} else {
//			celdaMontoActual.setStyle("color:red;");
//		}

		celdaEstado = new Listcell(pedidoColectivo.getEstado());
		String estado = pedidoColectivo.getEstado();

		if (estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
			celdaEstado.setStyle("color:blue; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO)) {
			celdaEstado.setStyle("color:Magenta; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_CANCELADO)) {
			celdaEstado.setStyle("color:red; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO)) {
			celdaEstado.setStyle("color:green; font-family:Arial Black;");
		}
		
		String direccion = "";
		if (pedidoColectivo.getDireccionEntrega() != null) {
			direccion = pedidoColectivo.getDireccionEntrega().getCalle() + " " + pedidoColectivo.getDireccionEntrega().getAltura();
		}
		if(pedidoColectivo.getPuntoDeRetiro() != null){
			direccion = "Punto de Retiro: " + pedidoColectivo.getPuntoDeRetiro().getNombre();
		}
		if(direccion.equals("")) {
			direccion = "N/D";
		}
		celdaDireccion = new Listcell(direccion);
		if(pedidoColectivo.getPuntoDeRetiro() != null){
			celdaDireccion.setStyle("color:blue; font-family:Courier Black;");
		}
		

		celdaBotones = new Listcell();

		this.configurarAcciones(pedidoColectivo);

		celdaId.setParent(item);
		celdaUsr.setParent(item);
		celdaAdmn.setParent(item);
		celdaFechaCreacion.setParent(item);
		celdaFechaCierre.setParent(item);
		celdaZona.setParent(item);
		celdaMontoMinimo.setParent(item);
		celdaMontoActual.setParent(item);
		celdaEstado.setParent(item);
		celdaDireccion.setParent(item);
		celdaBotones.setParent(item);

	}

	private void completarCeldas(Listcell celdaUsr2, Listcell celdaAdmn2,Integer idpedido) {
		GrupoCC grupo = this.datagrupo.get(idpedido);
		if(grupo!=null) {
			celdaUsr2 = new Listcell(grupo.getAlias());
			celdaAdmn2 = new Listcell(grupo.getAdministrador().getEmail());
		}else {
			celdaUsr2 = new Listcell("sin nombre grupo");
			celdaAdmn2 = new Listcell("sin nombre admin");
		}		
	}

	private void configurarAcciones(final PedidoColectivo pedidoColectivo) {
		Space espacio = new Space();
		espacio.setSpacing("10px");

		// ------------------------------Botón para abrir el pedido en una
		// pantalla
		Toolbarbutton botonVerPedido = new Toolbarbutton("Ver detalle");
		botonVerPedido.setTooltiptext("Muestra los pedidos de los integrantes del pedido colectivo");
		botonVerPedido.setImage("/imagenes/eye.png");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(PedidosComposer.PEDIDO_KEY, pedidoColectivo);
		params.put(PedidosComposer.ACCION_KEY, PedidosComposer.ACCION_VER);
		botonVerPedido.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, params);
		
		


		// ------------------------------Botón para editar la zona
		Toolbarbutton botonEditarZona = new Toolbarbutton("Cambiar Zona");
		if (pedidoColectivo.estaAbierto()) {
			botonEditarZona.setTooltiptext("El pedido debe estar confirmado");
			botonEditarZona.setStyle("color:gray");
			botonEditarZona.setDisabled(true);
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)){
			botonEditarZona.setTooltiptext("El pedido esta vencido");
			botonEditarZona.setStyle("color:gray");
			botonEditarZona.setDisabled(true);
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO)){
			botonEditarZona = new Toolbarbutton("Cancelado");
			botonEditarZona.setTooltiptext("El pedido debe esta cancelado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
			botonEditarZona.setDisabled(false);
			botonEditarZona.setTooltiptext("Cambiar zona de entrega del pedido");
			HashMap<String, Object> paramsZona = new HashMap<String, Object>();
			paramsZona.put(PedidosColectivosComposer.PEDIDO_KEY, pedidoColectivo);
			paramsZona.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_EDITAR);
			botonEditarZona.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsZona);
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			botonEditarZona.setTooltiptext("El pedido esta entregado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
		}
		
		//----------------------------Botón para Entregar
		Toolbarbutton botonEntregar = new Toolbarbutton("Preparar el pedido");
		String msj = "Prepara el pedido";
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)&& (pedidoColectivo.getZona() != null || pedidoColectivo.getPuntoDeRetiro() != null)){
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			msj = "Prepara el pedido";
			botonEntregar.setTooltiptext(msj);
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedidoColectivo);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosColectivosComposer.ACCION_PREPARAR);
			botonEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			msj = "Confirmar pedido";
			botonEntregar = new Toolbarbutton(msj);
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			botonEntregar.setTooltiptext("Confirma la entrega");
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedidoColectivo);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosColectivosComposer.ACCION_ENTREGAR);
			botonEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			msj = "Entregado";
			botonEntregar = new Toolbarbutton(msj);
			botonEntregar.setTooltiptext("El pedido esta entregado");
			botonEntregar.setDisabled(true);
			botonEntregar.setStyle("color:gray");
		}
		
		if(pedidoColectivo.getZona() == null){
				botonEntregar.setLabel(msj);
				botonEntregar.setTooltiptext("El pedido no esta confirmado y/o no posee una zona asignada");
				botonEntregar.setDisabled(true);
				botonEntregar.setStyle("color:gray");
		}
		
		if(pedidoColectivo.getPuntoDeRetiro() != null){
			botonEntregar.setLabel(msj);
			if(! pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
				botonEntregar.setDisabled(false);
				botonEntregar.setStyle("color:black");
				botonEntregar.setTooltiptext("Prepara el pedido");
			}else{
				botonEntregar.setDisabled(true);
				botonEntregar.setStyle("color:gray");
				botonEntregar.setTooltiptext("Pedido entregado");
			}
		}
		
				
		Hlayout hbox = new Hlayout();
		botonVerPedido.setParent(hbox);
		if(pedidoColectivo.getPuntoDeRetiro() == null){
			botonEditarZona.setParent(hbox);
		}
		botonEntregar.setParent(hbox);
		espacio.setParent(hbox);
		hbox.setParent(celdaBotones);
	}

	public Map<Integer,GrupoCC> getDatagrupo() {
		return datagrupo;
	}

	public void setDatagrupo(Map<Integer,GrupoCC> datagrupo) {
		this.datagrupo = datagrupo;
	}
}
