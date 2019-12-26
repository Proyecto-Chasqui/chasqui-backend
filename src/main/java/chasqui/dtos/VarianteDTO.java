package chasqui.dtos;

import java.text.DecimalFormat;

public class VarianteDTO {
	
	 private String nombreProducto;
	 private String nombreVariante;
	 private String nombreProductor;
	 private Integer cantidad;
	 private Double precio;
	 DecimalFormat df = new DecimalFormat("#.##");
	 
	 public VarianteDTO(String nombreProducto, String nombreVariante, Integer cantidad, Double precio, String nombreProductor) {
		 super();
		 this.nombreProducto = nombreProducto;
		 this.nombreVariante = nombreVariante;
		 this.nombreProductor = nombreProductor;
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
	
	private Double trim2decimals(Double d) {
		String trim = df.format(d); 
		Double value = Double.parseDouble(trim.replace(",","."));
		return value;
	}
	
	public Double getSubTotal(){
		return trim2decimals(this.precio * this.cantidad);
	}

	public String getNombreProductor() {
		return nombreProductor;
	}

	public void setNombreProductor(String nombreProductor) {
		this.nombreProductor = nombreProductor;
	}

}
