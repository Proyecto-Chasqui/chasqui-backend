package chasqui.model;

public class ProductoPedido {

	private Integer id;
	private Integer idVariante;
	private Double precio;
	private String nombreProducto;
	private String nombreVariante;
	private Integer cantidad;
	private String imagen;//TODO analizar
	private String nombreProductor;
	
	
	public ProductoPedido (){}
	
	public ProductoPedido(Variante v,Integer cant, String vnombreProductor) {
		idVariante = v.getId();
		cantidad = cant;
		nombreProducto = v.getProducto().getNombre();
		nombreVariante = " "; //v.getNombre();
		precio = v.getPrecio();
		imagen = (v.getImagenes().size()>0)?v.getImagenes().get(0).getPath():null;
		setNombreProductor(vnombreProductor);
	}

	//GETs & SETs
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
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
	
	
	
	
}
