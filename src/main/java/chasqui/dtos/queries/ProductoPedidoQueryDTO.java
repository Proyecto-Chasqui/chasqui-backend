package chasqui.dtos.queries;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;

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