package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
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

public class ProductoPedidoDAOHbm extends HibernateDaoSupport implements ProductoPedidoDAO {
	public List<ProductoPedido> obtener(ProductoPedidoQueryDTO query) {

    final Integer idVariante = query.getIdVariante();
    final boolean onlyPedidosActivos = query.getOnlyPedidosActivos();
    final String estado = query.getEstado();


    return this.getHibernateTemplate().execute(new HibernateCallback<List>() {

        @Override
        public List doInHibernate(Session session) throws HibernateException, SQLException {
          Criteria c = session.createCriteria(ProductoPedido.class, "productoPedido")
            .createAlias("productoPedido.pedido", "pedido", Criteria.LEFT_JOIN)
            .add(Restrictions.eq("idVariante", idVariante))
            .addOrder(Order.desc("pedido.fechaCreacion"));

          if (onlyPedidosActivos) {
            c.add(
              Restrictions.or(
                Restrictions.eq("pedido.estado", "ABIERTO"),
                Restrictions.isNull("pedido.estado")
              ));
          } else if(estado != null)  {
            c.add(Restrictions.eq("pedido.estado", estado));
          }


          return (List<ProductoPedido>) c.list();
        }
      });
    }
}
	