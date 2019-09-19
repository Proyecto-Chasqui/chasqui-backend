package chasqui.model;

import java.util.ArrayList;
import java.util.List;

public class Fabricante {

	private Integer id;
	private String nombre;
	private String calle;
	private Integer altura;
	private String pais;
	private String provincia;
	private String localidad;
	private Integer idVendedor;
	private String descripcionCorta;
	private String descripcionLarga;
	private String pathImagen;
	private List<Producto> productos;
	private CaracteristicaProductor caracteristica;
	private List<CaracteristicaProductor> caracteristicas;
 	
	//CONSTRUCTORs

	public Fabricante(){
		productos = new ArrayList<Producto>();
	}

	public Fabricante(String nombre){
		this.nombre = nombre;
		productos = new ArrayList<Producto>();
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

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public Integer  getAltura() {
		return altura;
	}

	public void setAltura(Integer altura) {
		this.altura = altura;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	public CaracteristicaProductor getCaracteristica() {
		return caracteristica;
	}
	
	public void setCaracteristica(CaracteristicaProductor caracteristica) {
		this.caracteristica = caracteristica;
	}
	
	public String getPais() {
		return pais;
	}	
	
	public String getDescripcionCorta() {
		return descripcionCorta;
	}

	public void setDescripcionCorta(String descripcionCorta) {
		this.descripcionCorta = descripcionCorta;
	}

	public String getDescripcionLarga() {
		return descripcionLarga;
	}

	public void setDescripcionLarga(String descripcionLarga) {
		this.descripcionLarga = descripcionLarga;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public String getLocalidad() {
		return localidad;
	}
	
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}	
	public String getPathImagen() {
		return pathImagen;
	}
	
	public void setPathImagen(String pathImagen) {
		this.pathImagen = pathImagen;
	}
	
	public Integer getIdVendedor() {
		return idVendedor;
	}
	
	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	
	//METHODS

	@Override
	public String toString(){
		return this.getNombre();
	}

	public void agregarProducto(Producto model) {
		if(!ProductoExiste(model)){
			this.productos.add(model);
		}else{
			this.reemplazarProducto(model);
		}
	}

	private void reemplazarProducto(Producto model) {
		int index = 0;
		for(int i=0; i<this.productos.size() ;i++){
			Producto p = this.productos.get(i);
			if(p.getId().equals(model.getId())){
				index = i;
				i = this.productos.size();
			}
		}
		
		this.productos.remove(index);
		this.productos.add(model);
	}

	private boolean ProductoExiste(Producto model) {
		for(Producto p: this.productos){
			if(model.getId() == null){
				return false;
			} else if(p.getId().equals(model.getId())){
				return true;
			}
		}
		return false;
	}

	public void eliminarProducto (Producto producto) {
		productos.remove(producto);
	}

	public List<CaracteristicaProductor> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<CaracteristicaProductor> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	
	@Override
	public boolean equals(Object obj) {
		Fabricante f = (Fabricante)obj;
		return this.nombre.equals(f.getNombre());
	}

}
