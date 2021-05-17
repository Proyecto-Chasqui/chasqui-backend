package chasqui.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.ProductoPedidoDAO;
import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.model.ProductoPedido;
import chasqui.model_lite.ProductoPedidoLite;
import chasqui.services.interfaces.ProductoPedidoService;

public class ProductoPedidoServiceImpl implements ProductoPedidoService {

  @Autowired
  private ProductoPedidoDAO productoPedidoDAO;

  @Override
  public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query) {
    return productoPedidoDAO.obtener(query);
  }

  @Override
  public List<ProductoPedidoLite> obtenerLite(ProductoPedidoQueryDTO query) {
    return productoPedidoDAO.obtenerLite(query);
  }

}