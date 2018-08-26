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

import chasqui.model.Pedido;
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;
import chasqui.view.composer.PedidosColectivosComposer;
import chasqui.view.composer.PedidosComposer;
//Esta clase es usuada en todas las areas en las que se muestra un pedido individual:
//Area de pedidos individuales.
//Area de pedidos individuales dentro de un grupo.
//Pop up de advertencia al tratar de eliminar un punto de retiro asociado a uno o varios pedidos.
//Si se desea cambiar algo revisar todas las areas afectadas para mantener la coherencia.
public class PedidoRenderer implements ListitemRenderer<Pedido> {

	private Window pedidoWindow;
	private Listcell celdaId, celdaUsr, celdaFechaCreacion, celdaFechaCierre, celdaZona, celdaMontoMinimo, celdaMontoActual, celdaEstado,
			celdaDireccion, celdaBotones;

	public PedidoRenderer(Window w) {
		pedidoWindow = w;
	}

	public void render(Listitem item, final Pedido pedido, int arg2) throws Exception {

		celdaId = new Listcell(String.valueOf(pedido.getId()));

		celdaUsr = new Listcell(pedido.getCliente().getEmail());

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date d = new Date(pedido.getFechaCreacion().getMillis());
		celdaFechaCreacion = new Listcell(format.format(d));
		Date d2 = null;
		if(pedido.getFechaModificacion()!=null) {
			d2 = new Date(pedido.getFechaModificacion().getMillis());
		}
		if(d2 == null || pedido.estaAbierto() ||pedido.estaCancelado()|| pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)) {
			celdaFechaCierre = new Listcell("N/D");
		}else {
			celdaFechaCierre = new Listcell(format.format(d2));
		}
		// -----------------Mostrar la zona
		Zona zonaPedido = pedido.getZona();
		if (zonaPedido == null) {
			if(pedido.getPuntoDeRetiro() != null) {
				celdaZona = new Listcell(Constantes.ZONA_NO_NECESARIA);
				celdaZona.setStyle("color:green;");
			}else {
				celdaZona = new Listcell(Constantes.ZONA_NO_DEFINIDA);
				celdaZona.setStyle("color:red;");
			}
		} else {
			celdaZona = new Listcell(zonaPedido.getNombre());
		}

		celdaMontoMinimo = new Listcell(String.valueOf(pedido.getMontoMinimo()));

		celdaMontoActual = new Listcell(String.valueOf(pedido.getMontoActual()));
		if (pedido.getMontoMinimo() < pedido.getMontoActual()) {
			celdaMontoActual.setStyle("color:green;");
		} else {
			celdaMontoActual.setStyle("color:red;");
		}

		celdaEstado = crearCeldaSegunEstado(pedido);
		String estado = pedido.getEstado();

		if (estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
			celdaEstado.setStyle("color:blue; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_ABIERTO)) {
			celdaEstado.setStyle("color:DarkKhaki; font-family:Arial Black;");
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
		if (estado.equals(Constantes.ESTADO_PEDIDO_VENCIDO)) {
			celdaEstado.setStyle("color:LightSalmon; font-family:Arial Black;");
		}
	
		String direccion = "";
		if (pedido.getDireccionEntrega() != null) {
			direccion = pedido.getDireccionEntrega().getCalle() + " " + pedido.getDireccionEntrega().getAltura();
		}
		if(pedido.getPuntoDeRetiro() != null){
			direccion = "Punto de Retiro: " + pedido.getPuntoDeRetiro().getNombre();
		}
		celdaDireccion = new Listcell(direccion);
		if(pedido.getPuntoDeRetiro() != null){
			celdaDireccion.setStyle("color:blue; font-family:Courier Black;");
		}
		celdaBotones = new Listcell();

		this.configurarAcciones(pedido);
		
		celdaId.setParent(item);
		celdaUsr.setParent(item);
		celdaFechaCreacion.setParent(item);
		celdaFechaCierre.setParent(item);
		celdaZona.setParent(item);
		celdaMontoMinimo.setParent(item);
		celdaMontoActual.setParent(item);
		celdaEstado.setParent(item);
		celdaDireccion.setParent(item);
		celdaBotones.setParent(item);

	}
	
	private Listcell crearCeldaSegunEstado(Pedido pedido) {
		Listcell ret;
		if(pedido.getPerteneceAPedidoGrupal() && pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
			ret = new Listcell("CONFIRMADO");
		}else{
			ret = new Listcell(pedido.getEstado());
		}
		return ret;
	}

	private void configurarAcciones(final Pedido pedido) {
		Space espacio = new Space();
		espacio.setSpacing("10px");

		// ------------------------------Botón para abrir el pedido en una
		// pantalla
		Toolbarbutton botonVerPedido = new Toolbarbutton("Ver Detalle");
		botonVerPedido.setTooltiptext("Ver detalle del pedido");
		botonVerPedido.setImage("/imagenes/eye.png");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(PedidosComposer.PEDIDO_KEY, pedido);
		params.put(PedidosComposer.ACCION_KEY, PedidosComposer.ACCION_VER);
		botonVerPedido.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, params);


		// ------------------------------Botón para editar la zona
		Toolbarbutton botonEditarZona = new Toolbarbutton("Cambiar Zona");
		if (pedido.estaAbierto()) {
			botonEditarZona.setTooltiptext("El pedido debe estar confirmado");
			botonEditarZona.setStyle("color:gray");
			botonEditarZona.setDisabled(true);
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)){
			botonEditarZona.setTooltiptext("El pedido esta vencido");
			botonEditarZona.setStyle("color:gray");
			botonEditarZona.setDisabled(true);
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO)){
			botonEditarZona.setTooltiptext("El pedido debe esta cancelado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
			botonEditarZona.setDisabled(false);
			botonEditarZona.setTooltiptext("Cambiar zona de entrega del pedido");
			HashMap<String, Object> paramsZona = new HashMap<String, Object>();
			paramsZona.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsZona.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_EDITAR);
			botonEditarZona.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsZona);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			botonEditarZona.setTooltiptext("El pedido esta preparado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			botonEditarZona.setTooltiptext("El pedido esta entregado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
		}
		
		//----------------------------Botón para Entregar
		
		Toolbarbutton botonEntregar = new Toolbarbutton("Preparar el pedido");
		String msj = "Prepara el pedido";
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) && (pedido.getZona() != null || pedido.getPuntoDeRetiro() != null)){
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			msj = "Prepara el pedido";
			botonEntregar.setTooltiptext(msj);
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_PREPARAR);
			botonEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			msj = "Confirmar Entrega";
			botonEntregar.setLabel(msj);
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			botonEntregar.setTooltiptext(msj);
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_ENTREGAR);
			botonEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			msj = "Entregado";
			botonEntregar.setLabel(msj);
			botonEntregar.setTooltiptext("El pedido esta entregado");
			botonEntregar.setDisabled(true);
			botonEntregar.setStyle("color:gray");
		}
		
		if(pedido.getZona() == null){
			botonEntregar.setLabel(msj);
			botonEntregar.setTooltiptext("El pedido no esta confirmado y/o no posee una zona asignada");
			botonEntregar.setDisabled(true);
			botonEntregar.setStyle("color:gray");
		}
		
		if(pedido.getPuntoDeRetiro() != null){
			botonEntregar.setLabel(msj);
			if(! pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
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
		if(!pedido.getPerteneceAPedidoGrupal()){
			if(pedido.getPuntoDeRetiro() == null){
				botonEditarZona.setParent(hbox);
			}
			botonEntregar.setParent(hbox);
		}
		espacio.setParent(hbox);
		hbox.setParent(celdaBotones);
	}

	public Listcell getCeldaFechaCierre() {
		return celdaFechaCierre;
	}

	public void setCeldaFechaCierre(Listcell celdaFechaCierre) {
		this.celdaFechaCierre = celdaFechaCierre;
	}

}
