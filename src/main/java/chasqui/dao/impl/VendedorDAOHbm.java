package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.VendedorDAO;
import chasqui.model.Vendedor;

@SuppressWarnings("unchecked")
public class VendedorDAOHbm  extends HibernateDaoSupport implements VendedorDAO{

	
	@Override
	public List<Vendedor> obtenerVendedores() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Vendedor.class);
		criteria.add(Restrictions.eq("isRoot", false));
		criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
		//criteria.add(Restrictions.isNotNull("fechaCierrePedido"));
		return this.getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public Vendedor obtenerVendedor(final String username) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("username", username));
				criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
				//criteria.add(Restrictions.isNotNull("fechaCierrePedido"));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}

	@Override
	public Vendedor obtenerVendedorPorURL(final String url) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("url", url));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}
}
