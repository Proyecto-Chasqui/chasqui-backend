package chasqui.dao.impl;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.PuntoDeRetiroDAO;
import chasqui.model.PuntoDeRetiro;

public class PuntoDeRetiroDAOHbm extends HibernateDaoSupport implements PuntoDeRetiroDAO{
	
	@Override
	public PuntoDeRetiro obtenerPuntoDeRetiro(final Integer id) {
		PuntoDeRetiro pr = this.getHibernateTemplate().execute(new HibernateCallback<PuntoDeRetiro>() {

			public PuntoDeRetiro doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PuntoDeRetiro.class);
				criteria.add(Restrictions.eq("id", id));
				return (PuntoDeRetiro) criteria.uniqueResult();
			}

		});
		return pr;
	}
	
	@Override
	public void guardar(PuntoDeRetiro pr) {
		this.getHibernateTemplate().saveOrUpdate(pr);
		
	}
	@Override
	public void eliminar(PuntoDeRetiro pr) {
	this.getHibernateTemplate().delete(pr);
		
	}
}
