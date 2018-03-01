package chasqui.view.renders;

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
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;
import chasqui.view.composer.PedidosColectivosComposer;

public class GrupoCCRenderer implements ListitemRenderer<GrupoCC> {

	private Window grupoWindow;
	private Listcell celdaId, celdaGrupo, celdaUsr, celdaFechaCreacion, celdaZona, celdaMontoMinimo, celdaMontoActual, celdaEstado,
			celdaDireccion, celdaBotones;

	public GrupoCCRenderer(Window w) {
		grupoWindow = w;
	}

	public void render(Listitem item, GrupoCC grupo, int arg2) throws Exception {

		celdaId = new Listcell(String.valueOf(grupo.getId()));

		celdaUsr = new Listcell(grupo.getAdministrador().getEmail());
		
		celdaGrupo = new Listcell(grupo.getAlias());

//		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//		Date d = new Date(grupo.getFechaCreacion().getMillis());
		celdaFechaCreacion = new Listcell(" Pedido Grupal ");

		// -----------------Mostrar la zona
		Zona zonaPedido = grupo.getPedidoActual().getZona();
		if (zonaPedido == null) {
			celdaZona = new Listcell(Constantes.ZONA_NO_DEFINIDA);
			celdaZona.setStyle("color:red;");
		} else {
			celdaZona = new Listcell(zonaPedido.getNombre());
		}

		celdaMontoMinimo = new Listcell("0");

		celdaMontoActual = new Listcell(String.valueOf(grupo.getPedidoActual().getMontoTotal()));
//		if (pedido.getMontoMinimo() < pedido.getMontoActual()) {
//			celdaMontoActual.setStyle("color:green;");
//		} else {
//			celdaMontoActual.setStyle("color:red;");
//		}

		celdaEstado = new Listcell(grupo.getPedidoActual().getEstado());
		String estado = grupo.getPedidoActual().getEstado();

		if (estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
			celdaEstado.setStyle("color:blue; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_CANCELADO)) {
			celdaEstado.setStyle("color:red; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO)) {
			celdaEstado.setStyle("color:green; font-family:Arial Black;");
		}
		if (estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO)) {
			celdaEstado.setStyle("color:Magenta; font-family:Arial Black;");
		}

		String direccion = "";
		if (grupo.getPedidoActual().getDireccionEntrega() != null) {
			direccion = grupo.getPedidoActual().getDireccionEntrega().toString();
		}
		celdaDireccion = new Listcell(direccion);
		celdaBotones = new Listcell();

		this.configurarAcciones(grupo);

		celdaId.setParent(item);
		celdaGrupo.setParent(item);
		celdaUsr.setParent(item);
		celdaFechaCreacion.setParent(item);
		celdaZona.setParent(item);
		celdaMontoMinimo.setParent(item);
		celdaMontoActual.setParent(item);
		celdaEstado.setParent(item);
		celdaDireccion.setParent(item);
		celdaBotones.setParent(item);

	}

	private void configurarAcciones(final GrupoCC grupo) {
		Space espacio = new Space();
		espacio.setSpacing("10px");
		
		// Botón abrir historial de pedido en pantalla
		Toolbarbutton botonVerHistorialDePedidos = new Toolbarbutton("Ver Historial de Pedidos");
		botonVerHistorialDePedidos.setTooltiptext("Muestra los pedidos grupales confirmados");
		botonVerHistorialDePedidos.setImage("/imagenes/eye.png");
		HashMap<String, Object> paramsHistorial = new HashMap<String, Object>();
		paramsHistorial.put(PedidosColectivosComposer.PEDIDO_KEY_HISTORIAL, grupo.getHistorial());
		paramsHistorial.put(PedidosColectivosComposer.GRUPO_KEY, grupo);
		paramsHistorial.put(PedidosColectivosComposer.ACCION_KEY, PedidosColectivosComposer.ACCION_VER_HISTORIAL);
		botonVerHistorialDePedidos.addForward(Events.ON_CLICK, grupoWindow, Events.ON_USER, paramsHistorial);
		//Botón ver pedido actual
		Toolbarbutton botonVerPedioActual = new Toolbarbutton("Ver pedido grupal en progreso");
		botonVerPedioActual.setTooltiptext("Muestra el detalle del pedido grupal abierto");
		botonVerPedioActual.setImage("/imagenes/eye.png");
		HashMap<String, Object> paramsVerPedidoActual = new HashMap<String, Object>();
		paramsVerPedidoActual.put(PedidosColectivosComposer.PEDIDO_KEY, grupo.getPedidoActual());
		paramsVerPedidoActual.put(PedidosColectivosComposer.ACCION_KEY, PedidosColectivosComposer.ACCION_VER);
		botonVerPedioActual.addForward(Events.ON_CLICK, grupoWindow, Events.ON_USER, paramsVerPedidoActual);
		//Botón ver datos del usuario
		Hlayout hbox = new Hlayout();
		botonVerPedioActual.setParent(hbox);
		botonVerHistorialDePedidos.setParent(hbox);
		espacio.setParent(hbox);
		hbox.setParent(celdaBotones);
	}

}
