package chasqui.model_lite;

public class ProductoPedidoLiteAgrupados {
	private String nombre;
	private String imagen;
	private Double precio;
	private Integer cantidad = 0;
	private Integer pesoGramosUnidad = 0; // peso por unidad en gramos
	private Double incentivo;
	private Integer idVariante;
	private Integer idPedidoColectivo;


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

  public Integer getIdVariante() {
    return this.idVariante;
  }

  public void setIdVariante(Integer idVariante) {
    this.idVariante = idVariante;
  }

  public Integer getIdPedidoColectivo() {
    return this.idPedidoColectivo;
  }

  public void setIdPedidoColectivo(Integer idPedidoColectivo) {
    this.idPedidoColectivo = idPedidoColectivo;
  }
}
