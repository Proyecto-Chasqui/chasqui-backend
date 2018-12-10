package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
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
	private Menubar menubar;
	private Menu menu;

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
		if(!pedido.getPerteneceAPedidoGrupal()) {
			if (pedido.getMontoMinimo() <= pedido.getMontoActual()|| pedido.getPuntoDeRetiro() != null) {
				celdaMontoActual.setStyle("color:green;");
			} else {
				celdaMontoActual.setStyle("color:red;");
			}
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
		menubar = new Menubar();
		menu = new Menu("Ver Acciones");
		menu.setParent(menubar);
		Menupopup menupop = new Menupopup();
		menupop.setParent(menu);
		menubar.setAutodrop(true);
		
		//Menuitem ver detalle
		Menuitem menuitemdetalle = new Menuitem("Ver detalle");
		menuitemdetalle.setTooltip("Ver detalle del pedido");
		menuitemdetalle.setImage("/imagenes/eye.png");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(PedidosComposer.PEDIDO_KEY, pedido);
		params.put(PedidosComposer.ACCION_KEY, PedidosComposer.ACCION_VER);
		menuitemdetalle.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, params);
		menuitemdetalle.setParent(menupop);
		
		//menuitem notificar
		Menuitem menunotificar = new Menuitem("Notificar");
		menunotificar.setTooltip("Notifica con un email predefinido al usuario");
		menunotificar.setImage("/imagenes/envelope.png");
		HashMap<String, Object> paramsemail = new HashMap<String, Object>();
		paramsemail.put(PedidosComposer.PEDIDO_KEY, pedido);
		paramsemail.put(PedidosComposer.ACCION_KEY, PedidosComposer.ACCION_NOTIFICAR);
		menunotificar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsemail);

		// menu editar zona
		Menuitem itemeditarzona = new Menuitem("Cambiar Zona");
		itemeditarzona.setImage("/imagenes/mapIcon.png");
		if (pedido.estaAbierto()) {
			itemeditarzona.setTooltiptext("El pedido debe estar confirmado");
			itemeditarzona.setStyle("color:gray");
			itemeditarzona.setDisabled(true);
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO)){
			itemeditarzona.setTooltiptext("El pedido esta vencido");
			itemeditarzona.setStyle("color:gray");
			itemeditarzona.setDisabled(true);
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO)){
			itemeditarzona.setTooltiptext("El pedido esta cancelado");
			itemeditarzona.setDisabled(true);
			itemeditarzona.setStyle("color:gray");
		}
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)){
			itemeditarzona.setDisabled(false);
			itemeditarzona.setTooltiptext("Cambiar zona de entrega del pedido");
			HashMap<String, Object> paramsZona = new HashMap<String, Object>();
			paramsZona.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsZona.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_EDITAR);
			itemeditarzona.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsZona);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			itemeditarzona.setTooltiptext("El pedido esta preparado");
			itemeditarzona.setDisabled(true);
			itemeditarzona.setStyle("color:gray");
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			itemeditarzona.setTooltiptext("El pedido esta entregado");
			itemeditarzona.setDisabled(true);
			itemeditarzona.setStyle("color:gray");
		}
		
		// menu entregar
		Menuitem itemEntregar = new Menuitem("Entregar");
		itemEntregar.setImage("/imagenes/pedidoVacio.png");
		String msj = "Prepara el pedido";
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) && (pedido.getZona() != null || pedido.getPuntoDeRetiro() != null)){
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			msj = "Prepara el pedido";
			itemEntregar.setLabel("Preparar");
			itemEntregar.setTooltiptext(msj);
			itemEntregar.setImage("/imagenes/pedidoPreparado.png");
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_PREPARAR);
			itemEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			msj = "Confirmar Entrega";
			itemEntregar.setLabel("Entregar");
			HashMap<String, Object> paramsEntrega = new HashMap<String, Object>();
			itemEntregar.setTooltiptext(msj);
			itemEntregar.setImage("/imagenes/pedidoEntregado.png");
			paramsEntrega.put(PedidosColectivosComposer.PEDIDO_KEY, pedido);
			paramsEntrega.put(PedidosColectivosComposer.ACCION_KEY, PedidosComposer.ACCION_ENTREGAR);
			itemEntregar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsEntrega);
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
			msj = "Entregado";
			itemEntregar.setLabel(msj);
			itemEntregar.setImage("/imagenes/pedidoEntregado.png");
			itemEntregar.setTooltiptext("El pedido esta entregado");
			itemEntregar.setDisabled(true);
			itemEntregar.setStyle("color:gray");
		}
		
		if(pedido.getZona() == null){
			itemEntregar.setLabel(msj);
			itemEntregar.setTooltiptext("El pedido no esta confirmado y/o no posee una zona asignada");
			itemEntregar.setDisabled(true);
			itemEntregar.setStyle("color:gray");
		}
		
		if(pedido.getPuntoDeRetiro() != null){
			itemEntregar.setLabel(msj);
			if(! pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ENTREGADO)){
				itemEntregar.setDisabled(false);
				itemEntregar.setStyle("color:black");
				itemEntregar.setTooltiptext("Prepara el pedido");
			}else{
				itemEntregar.setDisabled(true);
				itemEntregar.setStyle("color:gray");
				itemEntregar.setTooltiptext("Pedido entregado");
			}
		}
		
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO)){
			itemEntregar.setDisabled(true);
			itemEntregar.setStyle("color:black");
			itemEntregar.setTooltiptext("Pedido cancelado");
		}
		menubar.setSclass("selectorpedidos");
		//bindea los componentes a un hbox
		Hlayout hbox = new Hlayout();
		if(!pedido.getPerteneceAPedidoGrupal()){
			if(pedido.getPuntoDeRetiro() == null){
				itemeditarzona.setParent(menupop);
			}
			itemEntregar.setParent(menupop);
		}
		if(estaPostConfirmado(pedido.getEstado())&&!pedido.getPerteneceAPedidoGrupal()) {
			menunotificar.setParent(menupop);
		}
		espacio.setParent(hbox);
		menubar.setParent(hbox);
		hbox.setParent(celdaBotones);
	}

	private boolean estaPostConfirmado(String estado) {
		return estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO) || estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO);
	}

	public Listcell getCeldaFechaCierre() {
		return celdaFechaCierre;
	}

	public void setCeldaFechaCierre(Listcell celdaFechaCierre) {
		this.celdaFechaCierre = celdaFechaCierre;
	}

}
