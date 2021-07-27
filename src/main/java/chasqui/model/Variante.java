package chasqui.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Variante {

	private Integer id;
	private List<Imagen> imagenes;
	private String descripcion;
	private Integer stock;
	private Integer cantidadReservada;
	private Integer pesoGramos = 0;
	private Double precio;
	private Double incentivo;
	private String nombre;
	private Producto producto;
	private Boolean destacado;
	private String codigo;
	DecimalFormat df = new DecimalFormat("#.##");
	
	//GETs & SETs
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Variante(){
		imagenes = new ArrayList<Imagen>();
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
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

	public Integer getPesoGramos() {
		return pesoGramos;
	}

	public void setPesoGramos(Integer pesoGramos) {
		this.pesoGramos = pesoGramos;
	}
	
	public Double getPrecio() {
		return trim2decimals(precio);
	}

	public Double getPrecioTotal() {
		return precio + incentivo; 
	}
	
	private Double trim2decimals(Double d) {
		String trim = df.format(d); 
		Double value = Double.parseDouble(trim.replace(",","."));
		return value;
	}
	
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public List<Imagen> getImagenes() {
		return imagenes;
	}
	
	public void setImagenes(List<Imagen> imagenes) {
		this.imagenes = imagenes;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getCantidadReservada() {
		return cantidadReservada;
	}

	public void setCantidadReservada(Integer cantidadReservada) {
		this.cantidadReservada = cantidadReservada;
	}

	
	public Integer getIdVendedor() {
		return this.producto.getFabricante().getIdVendedor();
	}
		
	public Boolean getDestacado() {
		return destacado;
	}

	public void setDestacado(Boolean destacado) {
		this.destacado = destacado;
	}
	
	// METHODS

	@Override
	public String toString(){
		return "Variante: [ id:"+id+" Stock:"+stock+"Reservados:"+cantidadReservada+
		 "Precio:"+precio+" Nombre:"+nombre+" idProducto:"+ producto.getId()+" ]";
		
	}

	public String obtenerImagenDePrevisualizacion() {
		for(Imagen i : imagenes){
			if(i.getPreview()){
				return i.getPath();
			}
		}
		return imagenes.get(0).getPath();
	}
	
	public boolean tieneStockParaReservar(Integer cantidad) {
		if(this.getStock() > 0) {
			return (this.getStock() - (this.getCantidadReservada() + cantidad))>= 0;
		}else {
			return false;
		}
	}
	
	public void reservarCantidad(Integer cantidad) {
		if(this.getCantidadReservada()<0) {
			cantidadReservada = 0;
		}
		cantidadReservada = cantidadReservada + cantidad;
	}

	public void eliminarReserva(Integer cantidad) {
		cantidadReservada = cantidadReservada - cantidad;
		if(this.getCantidadReservada()<0) {
			cantidadReservada = 0;
		}
		
	}

	public Double getIncentivo() {
		return incentivo;
	}

	public void setIncentivo(Double incentivo) {
		this.incentivo = incentivo;
	}

	public void retornarStock(Integer cantidad) {
		this.stock = this.getStock() + cantidad;
		
	}	
	
}
