package chasqui.dtos;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;

public class PedidoDTO {
	private String emailCliente;
	private Direccion domicilio;
	private String username;
	private Zona zona;
	private Double montoTotal;
	private List<PedidoIndividualDTO> pedidosIndividuales;
	private Boolean esIndividual = true;
	private Boolean sinConfirmar = false;

	public PedidoDTO(Pedido pedido) {
		this.emailCliente = pedido.getCliente().getEmail();
		this.username = pedido.getCliente().getUsername();
		this.zona = pedido.getZona();
		domicilio = pedido.getDireccionEntrega();
		montoTotal = pedido.getMontoTotal();
		pedidosIndividuales = new ArrayList<PedidoIndividualDTO>();
		esIndividual = !pedido.getPerteneceAPedidoGrupal()&&pedido.getPuntoDeRetiro()==null;
		sinConfirmar = !pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO);
	}
	
	public PedidoDTO(PedidoColectivo pedido, GrupoCC grupo) {
		this.emailCliente = grupo.getAdministrador().getEmail();
		this.username = grupo.getAdministrador().getUsername();
		this.zona = pedido.getZona();
		domicilio = pedido.getDireccionEntrega();
		montoTotal = pedido.getMontoTotal();
		pedidosIndividuales = new ArrayList<PedidoIndividualDTO>();
		esIndividual = false;
		sinConfirmar = !pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO);
	}

	public List<PedidoIndividualDTO> getPedidosIndividuales() {
		return pedidosIndividuales;
	}

	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}

	public void setPedidosIndividuales(List<PedidoIndividualDTO> pedidosIndividuales) {
		this.pedidosIndividuales = pedidosIndividuales;
	}

	public void addPedidoIndividual(String userMail, List<ProductoPedido> productosPedidos) {

		PedidoIndividualDTO nuevoPedido = new PedidoIndividualDTO(userMail);

		for (ProductoPedido pp : productosPedidos) {
			nuevoPedido.addVariante(pp.getPrecio(), pp.getNombreProducto(), pp.getNombreVariante(), pp.getCantidad());
		}

		this.pedidosIndividuales.add(nuevoPedido);

	}

	public Direccion getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Direccion domicilio) {
		this.domicilio = domicilio;
	}

	public PedidoIndividualDTO getPrimero() {
		return this.pedidosIndividuales.get(0);
	}

	public String getEmailCliente() {
		return emailCliente;
	}

	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}

	public Double getMontoTotal() {
		return montoTotal;
	}

	public void setMontoTotal(Double montoTotal) {
		this.montoTotal = montoTotal;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getEsIndividual() {
		return esIndividual;
	}

	public void setEsIndividual(Boolean esIndividual) {
		this.esIndividual = esIndividual;
	}

	public Boolean getSinConfirmar() {
		return sinConfirmar;
	}

	public void setSinConfirmar(Boolean sinConfirmar) {
		this.sinConfirmar = sinConfirmar;
	}
	
	
}
