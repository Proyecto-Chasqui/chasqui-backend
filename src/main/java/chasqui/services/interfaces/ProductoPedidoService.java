package chasqui.services.interfaces;

import java.util.List;

import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.model.ProductoPedido;

public interface ProductoPedidoService {
  public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query);
}