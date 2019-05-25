package chasqui.quartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Pedido;
import chasqui.model.Vendedor;
//import chasqui.service.websocket.impl.WebSocketManager;
import chasqui.service.websocket.messages.Messages;
import chasqui.service.websocket.messages.StatusMessage;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;

public class EliminacionPedidoQuartz {

	@Autowired
	PedidoService pedidoService;
	@Autowired
	String nombreServidor;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	VendedorService vendedorService;
//	@Autowired
//	WebSocketManager webSocketManager;
	ObjectMapper mapper = new ObjectMapper();

	public void execute() throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException,
			VendedorInexistenteException {
		if (obtenerHostname().equals(nombreServidor)) {
			List<Pedido> pedidosVencidos = new ArrayList<Pedido>();
			for (Vendedor v : vendedorService.obtenerVendedores()) {
				if (v.getTiempoVencimientoPedidos() > 0) {
					List<Pedido> ps = pedidoService.obtenerPedidosExpirados(v.getId());
					for (Pedido p : ps) {
						try {
							pedidoService.vencerPedido(p);
						} catch (EstadoPedidoIncorrectoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pedidosVencidos.add(p);
					}
				}
			}
			enviarNotificaciones(pedidosVencidos);
		}
	}

	// String r = mapper.writeValueAsString(crearMensaje(p));
	private void enviarNotificaciones(List<Pedido> pedidosVencidos) {
		List<StatusMessage> mensajes = new ArrayList<StatusMessage>();
		for (Pedido p : pedidosVencidos) {
			mensajes.add(crearMensaje(p));
		}
		Messages m = new Messages(mensajes);
		try {
			String r = mapper.writeValueAsString(m);
			sendMessage(r);
			//webSocketManager.sendAll(r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessage(String r) {

		try {

			URL url = new URL("http://"+InetAddress.getLocalHost().getHostAddress() + ":8998/message");
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = r;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	private StatusMessage crearMensaje(Pedido p) {
		return new StatusMessage(p.getCliente().getId(), Constantes.ESTADO_PEDIDO_VENCIDO, p.getId(),
				Constantes.AVISO_PEDIDO_VENCIDO, p.getIdVendedor());
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
