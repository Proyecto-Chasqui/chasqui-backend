package chasqui.model;

import java.text.DecimalFormat;

public class ProductoPedido {
	
	private Integer id;
	private Integer idVariante;
	private Double precio;
	private Double incentivo;
	private String nombreProducto;
	private String nombreVariante;
	private Integer pesoGramosUnidad = 0; // peso por unidad en gramos
	private Integer cantidad = 0;
	private String imagen;//TODO analizar
	private String nombreProductor;
	DecimalFormat df = new DecimalFormat("#.##");
	private Variante variante;
	private Pedido pedido;
	
	public ProductoPedido (){}
	
	public ProductoPedido(Variante v,Integer cant, String vnombreProductor) {
		idVariante = v.getId();
		cantidad = cant;
		nombreProducto = v.getProducto().getNombre();
		nombreVariante = " "; //v.getNombre();
		pesoGramosUnidad = v.getPesoGramos();
		precio = v.getPrecio();
		imagen = (v.getImagenes().size()>0)?v.getImagenes().get(0).getPath():null;
		setNombreProductor(vnombreProductor);
		incentivo = 0.0;
	}

	public Variante getVariante() {
		return variante;
	}

	public void setVariante(Variante v) {
		variante = v;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido p) {
		pedido = p;
	}

	//GETs & SETs
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Double getPrecio() {
		return trim2decimals(precio);
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	private Double trim2decimals(Double d) {
		String trim = df.format(d); 
		Double value = Double.parseDouble(trim.replace(",","."));
		return value;
	}

	public Integer getPesoGramosUnidad() {
		if(variante != null) {
			return variante.getPesoGramos();
		}
		return pesoGramosUnidad;
	}

	public Integer getPesoGramosTotal() {
		return cantidad * this.getPesoGramosUnidad();
	}

	public Integer getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
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

	public Integer getIdVariante() {
		return idVariante;
	}

	public void setIdVariante(Integer idVariante) {
		this.idVariante = idVariante;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public void restar(Integer cant) {
		cantidad -= cant;
	}

	public void sumarCantidad(Integer cant) {
		cantidad += cant;
		
	}

	public String getNombreProductor() {
		return nombreProductor;
	}

	public void setNombreProductor(String nombreProductor) {
		this.nombreProductor = nombreProductor;
	}

	public Double getIncentivo() {
		return incentivo;
	}

	public void setIncentivo(Double incentivo) {
		this.incentivo = incentivo;
	}
	
	
	
	
}
