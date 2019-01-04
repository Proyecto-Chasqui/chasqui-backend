package chasqui.quartz;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Pedido;
import chasqui.model.Vendedor;
import chasqui.service.websocket.impl.WebSocketManager;
import chasqui.service.websocket.messages.StatusMessage;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;

public class EliminacionPedidoQuartz {

	@Autowired PedidoService pedidoService;
	@Autowired String nombreServidor;
	@Autowired UsuarioService usuarioService;
	@Autowired VendedorService vendedorService;
	@Autowired WebSocketManager webSocketManager;
	ObjectMapper mapper = new ObjectMapper();
	
	
	public void execute() throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException{
		if(obtenerHostname().equals(nombreServidor)){
			List<Pedido> pedidosVencidos = new ArrayList<Pedido>();
			for(Vendedor v : vendedorService.obtenerVendedores()) {
				if(v.getTiempoVencimientoPedidos() > 0) {
					List<Pedido> ps = pedidoService.obtenerPedidosExpirados(v.getId());
					for(Pedido p : ps){
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
	
	//String r = mapper.writeValueAsString(crearMensaje(p));
	private void enviarNotificaciones(List<Pedido> pedidosVencidos) {
		List<StatusMessage> mensajes = new ArrayList<StatusMessage>();
		for(Pedido p : pedidosVencidos) {
			mensajes.add(crearMensaje(p));
		}
		try {
			String r = mapper.writeValueAsString(mensajes);
			webSocketManager.sendAll(r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private StatusMessage crearMensaje(Pedido p) {
		return new StatusMessage(p.getCliente().getId(),Constantes.ESTADO_PEDIDO_VENCIDO, p.getId(), Constantes.AVISO_PEDIDO_VENCIDO, p.getIdVendedor());
	}



	private String obtenerHostname(){
		InetAddress inetAddr;
		try {
			inetAddr = InetAddress.getLocalHost();
			return  inetAddr.getHostName();  
		} catch (UnknownHostException e) {
			return nombreServidor;
		}
	}
	
}
