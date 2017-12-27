package chasqui.quartz;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Pedido;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;

public class EliminacionPedidoQuartz {

	@Autowired PedidoService pedidoService;
	@Autowired String nombreServidor;
	@Autowired UsuarioService usuarioService;
	
	
	public void execute() throws PedidoVigenteException, RequestIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException{
		if(obtenerHostname().equals(nombreServidor)){
			List<Pedido> ps = pedidoService.obtenerPedidosExpirados();
			for(Pedido p : ps){

				try {
					pedidoService.vencerPedido(p);
				} catch (EstadoPedidoIncorrectoException e) {
					e.printStackTrace();
				}
			}
		}
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
