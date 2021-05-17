package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.ProductoPedidoDAO;
import chasqui.dtos.queries.ProductoPedidoQueryDTO;
import chasqui.model.Caracteristica;
import chasqui.model.Producto;
import chasqui.model.ProductoPedido;
import chasqui.model.Variante;
import chasqui.model_lite.ProductoPedidoLite;

public class ProductoPedidoDAOHbm extends HibernateDaoSupport implements ProductoPedidoDAO {
  public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query) {

    final Integer idVariante = query.getIdVariante();
    final String estado = query.getEstado();
    final Integer idPedido = query.getIdPedido();

    return this.getHibernateTemplate().execute(new HibernateCallback<List<ProductoPedido>>() {

      @Override
      public List<ProductoPedido> doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria c = session.createCriteria(ProductoPedido.class, "productoPedido")
            .createAlias("productoPedido.pedido", "pedido", CriteriaSpecification.LEFT_JOIN)
            .addOrder(Order.desc("pedido.fechaCreacion"));

        if (idVariante != null) {
          c.add(Restrictions.eq("idVariante", idVariante));
        }

        if (estado != null) {
          c.add(Restrictions.eq("pedido.estado", estado));
        }

        if (idPedido != null) {
          c.add(Restrictions.eq("pedido.id", idPedido));
        }
        return c.list();
      }
    });
  }

  public List<ProductoPedidoLite> obtenerLite(ProductoPedidoQueryDTO query) {

    final Integer idVariante = query.getIdVariante();
    final String estado = query.getEstado();
    final Integer idPedido = query.getIdPedido();

    return this.getHibernateTemplate().execute(new HibernateCallback<List<ProductoPedidoLite>>() {

      @Override
      public List<ProductoPedidoLite> doInHibernate(Session session) throws HibernateException, SQLException {
        Criteria c = session.createCriteria(ProductoPedidoLite.class, "productoPedido");

        if (idVariante != null) {
          c.add(Restrictions.eq("idVariante", idVariante));
        }

        // if (estado != null) {
        // c.add(Restrictions.eq("pedido.estado", estado));
        // }

        if (idPedido != null) {
          c.add(Restrictions.eq("idPedido", idPedido));
        }
        return c.list();
      }
    });
  }
}
