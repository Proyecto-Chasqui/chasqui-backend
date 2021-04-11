package chasqui.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;

public interface ProductoPedidoDAO {
	public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query);
}
	