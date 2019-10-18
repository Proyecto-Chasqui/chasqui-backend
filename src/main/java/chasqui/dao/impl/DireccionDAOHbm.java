package chasqui.dao.impl;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.DireccionDAO;
import chasqui.model.Direccion;

public class DireccionDAOHbm extends HibernateDaoSupport implements DireccionDAO{

	@Override
	public Direccion obtenerDireccionPorId(final Integer id) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Direccion>() {

			@Override
			public Direccion doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Direccion.class);
				criteria.add(Restrictions.eq("id", id));
				return (Direccion) criteria.uniqueResult();
			}
		});
	}

}
