package chasqui.model;

import java.util.ArrayList;
import java.util.List;

public class Categoria {

	private Integer id;
	private String nombre;
	private List<Producto> productos;
	private Vendedor vendedor;

	public Categoria () {
		productos = new ArrayList<Producto>();
	}
	public Categoria (Vendedor v, String nombreCategoria) {
		this.vendedor=v;
		productos = new ArrayList<Producto>();
		this.nombre=nombreCategoria;
	}
	
	//GETs & SETs
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String descripcion) {
		this.nombre = descripcion;
	}
	
	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
		for (Producto producto : productos) {
			producto.setCategoria(this);
		}
	}
	
	
	public Vendedor getVendedor() {
		return vendedor;
	}
	
	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
	
	
	public boolean equals(Categoria otra){
		return this.nombre.equals(otra.nombre);
	}


	//METHODS

	@Override
	public String toString(){
		return this.getNombre();
	}
	
	public void agregarProducto (Producto producto){
		productos.add(producto);
	}

	public void eliminarProducto (Producto producto) {
		productos.remove(producto);
	}
	
}
