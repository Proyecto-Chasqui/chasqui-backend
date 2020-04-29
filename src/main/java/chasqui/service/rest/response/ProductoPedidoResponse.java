package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.ProductoPedido;

public class ProductoPedidoResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4476615487794072597L;
	
	private String nombre;
	private Integer idVariante;
	private Double precio;
	private Integer cantidad;
	private String imagen;
	private Double incentivo;
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Double getPrecio() {
		return precio;
	}
	
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	public Integer getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	public String getImagen() {
		return imagen;
	}
	
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	
	public Integer getIdVariante() {
		return idVariante;
	}
	public void setIdVariante(Integer idVariante) {
		this.idVariante = idVariante;
	}
	
	public Double getIncentivo() {
		return incentivo;
	}

	public void setIncentivo(Double incentivo) {
		this.incentivo = incentivo;
	}
	
	
	public ProductoPedidoResponse(){}
	public ProductoPedidoResponse(ProductoPedido p){
		imagen = p.getImagen();
		cantidad = p.getCantidad();
		precio = p.getPrecio();
		nombre = p.getNombreProducto() +" "+ p.getNombreVariante();
		idVariante = p.getIdVariante();
		incentivo = p.getIncentivo();
	}



	
	
	
	
	
	
	
}
