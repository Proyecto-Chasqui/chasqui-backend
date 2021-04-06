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
  private boolean onlyPedidosActivos = false;
  private String estado;

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public Boolean isOnlyPedidosActivos() {
    return this.onlyPedidosActivos;
  }

  public Boolean getOnlyPedidosActivos() {
    return this.onlyPedidosActivos;
  }

  public void setOnlyPedidosActivos(Boolean onlyPedidosActivos) {
    this.onlyPedidosActivos = onlyPedidosActivos;
  }

  public Integer getIdVariante() {
    return this.idVariante;
  }

  public void setIdVariante(Integer idVariante) {
    this.idVariante = idVariante;
  }
}