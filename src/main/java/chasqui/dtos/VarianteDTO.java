package chasqui.dtos;

public class VarianteDTO {
	
	 private String nombreProducto;
	 private String nombreVariante;
	 private Integer cantidad;
	 private Double precio;
	
	 
	 public VarianteDTO(String nombreProducto, String nombreVariante, Integer cantidad, Double precio) {
		 super();
		 this.nombreProducto = nombreProducto;
		 this.nombreVariante = nombreVariante;
		 this.cantidad = cantidad;
		 this.precio = precio;
	 }
	 
	 public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getNombreVariante() {
		return nombreVariante;
	}

	public void setNombreVariante(String nombreVariante) {
		this.nombreVariante = nombreVariante;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getPrecio() {
		return this.precio;
	}
	
	public Double getSubTotal(){
		return this.precio * this.cantidad;
	}

}
