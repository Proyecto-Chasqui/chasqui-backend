package chasqui.view.renders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import chasqui.model.GrupoCC;
import chasqui.model.PedidoColectivo;
import chasqui.model.Zona;
import chasqui.utils.PesoRender;
import chasqui.view.composer.Constantes;
import chasqui.view.composer.PedidosColectivosComposer;
import chasqui.view.composer.PedidosComposer;

public class PedidoColectivoRenderer implements ListitemRenderer<PedidoColectivo>{
	private Window pedidoWindow;
	private Map<Integer,GrupoCC> datagrupo;
	private Listcell celdaId,celdaUsr, celdaAdmn, celdaFechaCreacion, celdaFechaCierre, celdaZona, celdaMontoMinimo, celdaMontoActual, celdaPeso, celdaEstado,
			celdaDireccion, celdaBotones;
	
	private Menubar menubar;
	private Menu menu;

	public PedidoColectivoRenderer(Window w) {
		pedidoWindow = w;
	}

	public void render(Listitem item, final PedidoColectivo pedidoColectivo, int arg2) throws Exception {

		celdaId = new Listcell(String.valueOf(pedidoColectivo.getId()));
		
		if(pedidoColectivo.getColectivo() !=null) {
			celdaUsr = new Listcell(this.reducirTextoA(pedidoColectivo.getColectivo().getAlias(),16));
			celdaAdmn = new Listcell(this.reducirTextoA(pedidoColectivo.getColectivo().getAdministrador().getEmail(),32));
		}else {
			celdaUsr = new Listcell("N/D");
			celdaAdmn = new Listcell("N/D");
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
				celdaFechaCierre = new Listcell("N/D");
			}
		}
		if(pedidoColectivo.getFechaCreacion() != null){
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date(pedidoColectivo.getFechaCreacion().getMillis());
			celdaFechaCreacion = new Listcell(format.format(d));
		}else{
			celdaFechaCreacion = new Listcell("N/D");
		}

		// -----------------Mostrar la zona
		Zona zonaPedido = pedidoColectivo.getZona();
		if (zonaPedido == null) {
			if(pedidoColectivo.getPuntoDeRetiro() != null) {
				celdaZona = new Listcell(Constantes.ZONA_NO_NECESARIA);
				celdaZona.setStyle("color:green;");
			}else {
				celdaZona = new Listcell(Constantes.ZONA_NO_DEFINIDA);
				celdaZona.setStyle("color:red;");
			}
		} else {
			celdaZona = new Listcell(this.reducirTextoA(zonaPedido.getNombre(), 16));
		}

		celdaMontoMinimo = new Listcell("0");

		celdaMontoActual = new Listcell(String.valueOf(pedidoColectivo.getMontoTotal()));
//		if (pedidoColectivo.getMontoMinimo() < pedidoColectivo.getMontoActual()) {
//			celdaMontoActual.setStyle("color:green;");
//		} else {
//			celdaMontoActual.setStyle("color:red;");
//		}

		celdaPeso = new Listcell(PesoRender.pesoConUnidad(pedidoColectivo.getPesoGramosTotal()));

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
			direccion = this.reducirTextoA("PR: " + pedidoColectivo.getPuntoDeRetiro().getNombre(), 24);
		}
		if(direccion.equals("")) {
			direccion = "N/D";
		}
		celdaDireccion = new Listcell(direccion);
		if(pedidoColectivo.getPuntoDeRetiro() != null){
			celdaDireccion.setStyle("color:blue; font-family:Courier Black;");
		}
		

		celdaBotones = new Listcell();

		this.configurarAccionesEnSelector(pedidoColectivo);

		celdaId.setParent(item);
		celdaUsr.setParent(item);
		celdaAdmn.setParent(item);
		celdaFechaCreacion.setParent(item);
		celdaFechaCierre.setParent(item);
		celdaZona.setParent(item);
		celdaMontoMinimo.setParent(item);
		celdaMontoActual.setParent(item);
		celdaPeso.setParent(item);
		celdaEstado.setParent(item);
		celdaDireccion.setParent(item);
		celdaBotones.setParent(item);

	}
	
	private String reducirTextoA(String texto, Integer cantidadMaxima) {
		String textoCut = texto;
		if(textoCut==null) {
			textoCut = "";
		}
		if(textoCut.length() > cantidadMaxima) {
			textoCut = textoCut.substring(0, cantidadMaxima) + "...";
		}
		return textoCut;
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
	
	private void configurarAccionesEnSelector(final PedidoColectivo pedido) {
		Space espacio = new Space();
		espacio.setSpacing("10px");
		menubar = new Menubar();
		menu = new Menu("Ver Acciones");
		menu.setParent(menubar);
		Menupopup menupop = new Menupopup();
		menupop.setParent(menu);
		menubar.setAutodrop(false);
		
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
		
		//manuitem exportar
		Menuitem menuitemexportar = new Menuitem("Exportar");
		menuitemexportar.setTooltip("Exporta el pedido colectivo");
		menuitemexportar.setImage("/imagenes/export.png");
		HashMap<String, Object> paramsexport = new HashMap<String, Object>();
		paramsexport.put(PedidosComposer.PEDIDO_KEY, pedido);
		paramsexport.put(PedidosComposer.ACCION_KEY, "exportar");
		menuitemexportar.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsexport);
		menuitemexportar.setParent(menupop);
		
		menubar.setSclass("selectorpedidoscolectivos");
		//bindea los componentes a un hbox
		Hlayout hbox = new Hlayout();
		if(pedido.getPuntoDeRetiro() == null){
			itemeditarzona.setParent(menupop);
		}
		itemEntregar.setParent(menupop);
		if(estaPostConfirmado(pedido.getEstado())) {
			menunotificar.setParent(menupop);
		}
		espacio.setParent(hbox);
		menubar.setParent(hbox);
		hbox.setParent(celdaBotones);
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
		
		// ------------------------------Botón para notificar via email
		Toolbarbutton botonNotificarPedidoPreparado = new Toolbarbutton("Notificar");
		botonNotificarPedidoPreparado.setTooltiptext("Notifica con un email predefinido al usuario");
		botonNotificarPedidoPreparado.setImage("/imagenes/envelope.png");
		HashMap<String, Object> paramsemail = new HashMap<String, Object>();
		paramsemail.put(PedidosComposer.PEDIDO_KEY, pedidoColectivo);
		paramsemail.put(PedidosComposer.ACCION_KEY, PedidosComposer.ACCION_NOTIFICAR);
		botonNotificarPedidoPreparado.addForward(Events.ON_CLICK, pedidoWindow, Events.ON_USER, paramsemail);


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
		if(pedidoColectivo.getEstado().equals(Constantes.ESTADO_PEDIDO_PREPARADO)){
			botonEditarZona.setTooltiptext("El pedido esta entregado");
			botonEditarZona.setDisabled(true);
			botonEditarZona.setStyle("color:gray");
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
		if(estaPostConfirmado(pedidoColectivo.getEstado())) {
			botonNotificarPedidoPreparado.setParent(hbox);
		}
		hbox.setParent(celdaBotones);
	}
	
	private boolean estaPostConfirmado(String estado) {
		return estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO) || estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO);
	}

	public Map<Integer,GrupoCC> getDatagrupo() {
		return datagrupo;
	}

	public void setDatagrupo(Map<Integer,GrupoCC> datagrupo) {
		this.datagrupo = datagrupo;
	}
}
