package chasqui.dtos.queries;

public class ProductoPedidoQueryDTO {
  private Integer idVariante;
  private String estado;
  private Integer idPedido;

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public Integer getIdVariante() {
    return this.idVariante;
  }

  public void setIdVariante(Integer idVariante) {
    this.idVariante = idVariante;
  }

  public Integer getIdPedido() {
    return this.idPedido;
  }

  public void setIdPedido(Integer idPedido) {
    this.idPedido = idPedido;
  }
}