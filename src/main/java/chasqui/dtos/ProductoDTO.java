package chasqui.dtos;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.Caracteristica;
import chasqui.model.Producto;
import chasqui.model.Variante;

public class ProductoDTO {
	String nombre;
	String codigoInterno;
	String nombreProductor;
	List<String> sellos;
	String categoria;
	Double precio;
	Integer idVendedor;
	Integer stock;
	String descripcion;
	



	public ProductoDTO(Producto p) {
		Variante v = p.getVariantes().get(0);
		this.nombre = p.getNombre();
		this.codigoInterno = v.getCodigo();
		this.nombreProductor = p.getFabricante().getNombre();
		this.sellos = new ArrayList<String>();
		for(Caracteristica c : p.getCaracteristicas()) {
			this.sellos.add(c.getNombre());
		}
		this.categoria = p.getCategoria().getNombre();
		this.precio = v.getPrecio();
		this.idVendedor = p.getFabricante().getIdVendedor();
		this.stock = p.getVariantes().get(0).getStock();
		this.descripcion = p.getVariantes().get(0).getDescripcion();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public String getNombreProductor() {
		return nombreProductor;
	}

	public void setNombreProductor(String nombreProductor) {
		this.nombreProductor = nombreProductor;
	}

	public List<String> getSellos() {
		return sellos;
	}

	public void setSellos(List<String> sellos) {
		this.sellos = sellos;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}
	

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}