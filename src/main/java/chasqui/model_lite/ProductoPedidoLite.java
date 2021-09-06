package chasqui.model_lite;

public class ProductoPedidoLite {
  private Integer id;
	private String nombre;
	private String imagen;
	private Double precio;
	private Integer cantidad = 0;
	private Integer pesoGramosUnidad = 0; // peso por unidad en gramos
	private Double incentivo;
	private String nombreProductor;
	private Integer idVariante;
	private Integer idPedido;


  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdVariante() {
    return this.idVariante;
  }

  public void setIdVariante(Integer idVariante) {
    this.idVariante = idVariante;
  }

  public String getNombre() {
    return this.nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getImagen() {
    return this.imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public Double getPrecio() {
    return this.precio;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Integer getCantidad() {
    return this.cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  public Integer getPesoGramosUnidad() {
    return this.pesoGramosUnidad;
  }

  public void setPesoGramosUnidad(Integer pesoGramosUnidad) {
    this.pesoGramosUnidad = pesoGramosUnidad;
  }

  public Double getIncentivo() {
    return this.incentivo;
  }

  public void setIncentivo(Double incentivo) {
    this.incentivo = incentivo;
  }

  public String getNombreProductor() {
    return this.nombreProductor;
  }

  public void setNombreProductor(String nombreProductor) {
    this.nombreProductor = nombreProductor;
  }

  public Integer getIdPedido() {
    return this.idPedido;
  }

  public void setIdPedido(Integer idPedido) {
    this.idPedido = idPedido;
  }

}
