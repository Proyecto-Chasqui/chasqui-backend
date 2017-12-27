package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
	private Listcell celdaId,celdaUsr, celdaFechaCreacion, celdaZona, celdaMontoMinimo, celdaMontoActual, celdaEstado,
			celdaDireccion, celdaBotones;

	public PedidoColectivoRenderer(Window w) {
		pedidoWindow = w;
	}

	public void render(Listitem item, final PedidoColectivo pedidoColectivo, int arg2) throws Exception {

		celdaId = new Listcell(String.valueOf(pedidoColectivo.getId()));

		celdaUsr = new Listcell("Sin User");
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
			celdaEstado.setStyle("color:blue;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_CANCELADO)) {
			celdaEstado.setStyle("color:red;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO)) {
			celdaEstado.setStyle("color:green");
		}
		
		//TODO: HACKASO no deberia ser el grupo si no el pedido colectivo quien tenga el domicilio.
		String direccion = "";
		if (pedidoColectivo.getDireccionEntrega() != null) {
			direccion = pedidoColectivo.getDireccionEntrega().getCalle() + " " + pedidoColectivo.getDireccionEntrega().getAltura();
		}
		celdaDireccion = new Listcell(direccion);
		celdaBotones = new Listcell();

		this.configurarAcciones(pedidoColectivo);

		celdaId.setParent(item);
		celdaUsr.setParent(item);
		celdaFechaCreacion.setParent(item);
		celdaZona.setParent(item);
		celdaMontoMinimo.setParent(item);
		celdaMontoActual.setParent(item);
		celdaEstado.setParent(item);
		celdaDireccion.setParent(item);
		celdaBotones.setParent(item);

	}

	private void configurarAcciones(final PedidoColectivo pedidoColectivo) {
		Space espacio = new Space();
		espacio.setSpacing("10px");

		// ------------------------------Botón para abrir el pedido en una
		// pantalla
		Toolbarbutton botonVerPedido = new Toolbarbutton("Ver en detalle los pedidos");
		botonVerPedido.setTooltiptext("Ver detalle del pedido");
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
		Toolbarbutton botonEntregar = new Toolbarbutton("Confirmar Entrega");
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)&& pedidoColectivo.getZona() != null){
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			botonEntregar.setTooltiptext("Confirma la entrega");
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedidoColectivo);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_ENTREGAR);
			botonEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			botonEntregar.setTooltiptext("El pedido esta entregado");
			botonEntregar.setDisabled(true);
			botonEntregar.setStyle("color:gray");
		}
		if(pedidoColectivo.getZona() == null){
				botonEntregar = new Toolbarbutton("Confirmar Entrega");
				botonEntregar.setTooltiptext("El pedido no esta confirmado y/o no posee una zona asignada");
				botonEntregar.setDisabled(true);
				botonEntregar.setStyle("color:gray");
		}
		
				
		Hlayout hbox = new Hlayout();
		botonVerPedido.setParent(hbox);
		botonEditarZona.setParent(hbox);
		botonEntregar.setParent(hbox);
		espacio.setParent(hbox);
		hbox.setParent(celdaBotones);
	}
}
