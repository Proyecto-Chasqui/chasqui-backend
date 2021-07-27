package chasqui.model_lite;

import java.util.Date;

public class PedidoColectivoStats {
  private Double montoActual;
  private Double incentivoActual;
  private Integer pesoGramosActual;
  private Integer cantPedidos;
  private String estadoPedido;
  private Date snapshotDate;

  public Double getMontoActual() {
    return this.montoActual;
  }

  public void setMontoActual(Double montoActual) {
    this.montoActual = montoActual;
  }

  public Double getIncentivoActual() {
    return this.incentivoActual;
  }

  public void setIncentivoActual(Double incentivoActual) {
    this.incentivoActual = incentivoActual;
  }

  public Double getMontoTotal() {
    return this.montoActual + this.incentivoActual;
  }

  public Integer getCantPedidos() {
    return this.cantPedidos;
  }

  public void setCantPedidos(Integer cantPedidos) {
    this.cantPedidos = cantPedidos;
  }

  public Date getSnapshotDate() {
    return this.snapshotDate;
  }

  public void setSnapshotDate(Date snapshotDate) {
    this.snapshotDate = snapshotDate;
  }

  public String getEstadoPedido() {
    return this.estadoPedido;
  }

  public void setEstadoPedido(String estadoPedido) {
    this.estadoPedido = estadoPedido;
  }


  public Integer getPesoGramosActual() {
    return this.pesoGramosActual;
  }

  public void setPesoGramosActual(Integer pesoGramosActual) {
    this.pesoGramosActual = pesoGramosActual;
  }

}