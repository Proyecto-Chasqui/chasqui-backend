package chasqui.model_lite;

import java.util.Date;

/**
 * Este fué necesario crear para sumarizar 
 * los montos con y sin incetivos de los productos
 */

public class PedidoStatsLite {
  private Integer id;
  private String estado;
	private Double montoActual;
  private Double incentivoActual;
  private Double montoActualSinIncetivo; // (deberia dar =montoActual-incentivoActual)
  private Integer pesoGramosActual;
  private Integer cantidadProductos;
  private Integer cantidadItems;
  private Date snapshotDate;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

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

  public Double getMontoActualSinIncetivo() {
    return this.montoActualSinIncetivo;
  }

  public void setMontoActualSinIncetivo(Double montoActualSinIncetivo) {
    this.montoActualSinIncetivo = montoActualSinIncetivo;
  }

  public Integer getPesoGramosActual() {
    return this.pesoGramosActual;
  }

  public void setPesoGramosActual(Integer pesoGramosActual) {
    this.pesoGramosActual = pesoGramosActual;
  }

  public Integer getCantidadProductos() {
    return this.cantidadProductos;
  }

  public void setCantidadProductos(Integer cantidadProductos) {
    this.cantidadProductos = cantidadProductos;
  }

  public Integer getCantidadItems() {
    return this.cantidadItems;
  }

  public void setCantidadItems(Integer cantidadItems) {
    this.cantidadItems = cantidadItems;
  }

  public Date getSnapshotDate() {
    return this.snapshotDate;
  }

  public void setSnapshotDate(Date snapshotDate) {
    this.snapshotDate = snapshotDate;
  }

}
