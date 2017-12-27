package chasqui.quartz;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.cxf.common.util.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

public class AvisoPedidoQuartz {

	@Autowired
	UsuarioService usuarioService;
	@Autowired
	VendedorService vendedorService;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	ZonaService zonaService;
	@Autowired
	String nombreServidor;
	@Autowired
	Integer cantidadDeDiasParaEnviarNotificacion;
	@Autowired
	String cuerpoEmail;// Obtenido mediante chasqui.properties
	@Autowired
	String mensajeNotificacionChasqui;
	@Autowired
	NotificacionService notificacionService;
	@Autowired
	MailService mailService;

	public void execute() {
		if (obtenerHostname().equals(nombreServidor)) {
			List<Vendedor> vendedores = vendedorService.obtenerVendedores();
			for (Vendedor v : vendedores) {
				enviarNotificacionesDePedidos(v);
			}
		}

	}

	/**
	 * TODO: HACER DE NUEVO!!! Para cada zona, obtener usuarios cuyo domicilio
	 * predeterminado esté en una zona (consulta espacial) y notificar fecha de
	 * cierre de pedidos
	 * 
	 * @param v
	 */
	@Transactional
	public void enviarNotificacionesDePedidos(Vendedor v) {
		try {

			List<Zona> zonas = zonaService.buscarZonasBy(v.getId());
			for (Zona zona : zonas) {
				this.notificarCierrePedidosPorZona(zona, v.getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// logear
		}
	}

	private void notificarCierrePedidosPorZona(Zona zona, Integer idVendedor)
			throws UsuarioInexistenteException, IOException, MessagingException, TemplateException {

		List<Pedido> pedidos = pedidoService.obtenerPedidosProximosAVencerEnDeterminadaZona(
				cantidadDeDiasParaEnviarNotificacion, idVendedor, zona.getFechaCierrePedidos(), zona.getId());
		for (Pedido p : pedidos) {
			DateTime dt = new DateTime();

			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(p.getCliente().getEmail());
			if (dt.getDayOfYear() == zona.getFechaCierrePedidos().getDayOfYear()) {
				mailService.enviarEmailNotificacionPedido(p.getCliente().getEmail(), cuerpoEmail, c.getNombre(),
						c.getApellido());
			} else {
				// String mensajeNotificacion = obtenerMensajeNotificacion(v);
				Notificacion n = new Notificacion("Chasqui", p.getCliente().getEmail(), zona.getDescripcion(),
						Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
				notificacionService.guardar(n, c.getIdDispositivo());
			}
		}
	}

	private String obtenerMensajeNotificacion(Vendedor v) {
		if (!StringUtils.isEmpty(v.getMsjCierrePedido())) {
			return mensajeNotificacionChasqui;
		}
		return v.getMsjCierrePedido();
	}

	private String obtenerHostname() {
		InetAddress inetAddr;
		try {
			inetAddr = InetAddress.getLocalHost();
			return inetAddr.getHostName();
		} catch (UnknownHostException e) {
			return nombreServidor;
		}
	}
}
