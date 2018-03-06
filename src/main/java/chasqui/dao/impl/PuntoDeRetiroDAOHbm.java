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
	public PuntoDeRetiro obtenerPuntoDeRetiro(final Integer Id) {
		return (PuntoDeRetiro) this.getHibernateTemplate().executeFind(new HibernateCallback<PuntoDeRetiro>() {

			@Override
			public PuntoDeRetiro doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(PuntoDeRetiro.class);
				c.createAlias("puntoDeRetiro", "pr")
				 .add(Restrictions.eq("pr.id", Id));
				return (PuntoDeRetiro) c.uniqueResult();
			}
		});
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
