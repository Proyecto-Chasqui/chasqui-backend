package chasqui.services.interfaces;

import java.util.List;

import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.model.ProductoPedido;
import chasqui.model_lite.ProductoPedidoLite;

public interface ProductoPedidoService {
  public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query);
  public List<ProductoPedidoLite> obtenerLite(ProductoPedidoQueryDTO query);
}