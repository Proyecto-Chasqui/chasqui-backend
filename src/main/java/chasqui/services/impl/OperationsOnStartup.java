package chasqui.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;
import chasqui.model.Pedido;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

/**
 * 
 * Esta clase esta definida para crear operaciones que sean aplicadas en el inicio del servidor
 * Por ejemplo ajustes en la BD, correcciones de errores previos, normalizacion de estructuras de datos, etc
 * puede ser modificado segun sea necesario.
 */

public class OperationsOnStartup {
	@Autowired
	VendedorService vendedorService;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	ProductorService productorService;
	@Autowired
	ProductoService productoService;
	@Autowired
	PedidoService pedidoService;

	//ajusta las reservas que quedaron en negativo y el stock, es un ajuste a un error no detectado en produccion.
	private void corregirReservas() {
		List<Vendedor> vendedores = vendedorService.obtenerVendedores();
		for(Vendedor v: vendedores) {
			try {
				corregirReservasDeVendedor(v.getId());
			} catch (EstadoPedidoIncorrectoException e) {
				e.printStackTrace();
			} catch (UsuarioInexistenteException e) {
				e.printStackTrace();
			} catch (VendedorInexistenteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void corregirReservasDeVendedor(Integer id) throws EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException {
		vencerPedidosDeVendedor(id);
		corregirReservasDe(id);
	}
	
	private void vencerPedidosDeVendedor(Integer id) throws EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException {
		 List<Pedido> pedidos = pedidoService.obtenerPedidosDeVendedor(id); 
		 for (Pedido pedido : pedidos) {
			 if(pedido.estaAbierto()) {
				 pedidoService.vencerPedido(pedido);
			 }
		 }
	}
	
	private void corregirReservasDe(Integer id) {
		List<Variante> variantes = productoService.obtenerTodasLasVariantes(id);
		for(Variante variante : variantes) {
			boolean modificado = false;
			if(variante.getCantidadReservada() < 0) {
				variante.setCantidadReservada(0);
				modificado = true;
			}
			if(variante.getStock() < 0) {
				variante.setStock(0);
				modificado = true;
			}
			if(modificado) {
				productoService.modificarVariante(variante);
			}
		}
	}


}
