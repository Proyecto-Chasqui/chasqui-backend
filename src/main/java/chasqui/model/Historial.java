package chasqui.model;

import java.util.ArrayList;
import java.util.List;

public class Historial {

	private Integer id;
	private String usuario;
	private List<Pedido> pedidos;
	
	//GETs & SETs 

	public Historial(String email) {
		usuario = email;
		pedidos = new ArrayList<Pedido>();
	}
	
	public Historial(){
		
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	public void agregarAHistorial(Pedido p) {
		pedidos.add(p);
		
	}
	
}
