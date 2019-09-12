package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.NodoDAO;
import chasqui.model.Nodo;
import chasqui.view.composer.Constantes;

public class NodoDAOHbm extends HibernateDaoSupport implements NodoDAO {

	
	
//	public void altaSolicitudNodo(String alias) {
//		// TODO Auto-generated method stub
//
//	}
	@Override
	public void aprobarNodo(Integer id) {
		// TODO Auto-generated method stub
		Nodo nodo = this.obtenerNodoPorId(id);
		this.getHibernateTemplate().saveOrUpdate(nodo);
		this.getHibernateTemplate().flush();
	}

	public void guardarNodo(Nodo nodo) {
		this.getHibernateTemplate().saveOrUpdate(nodo);
		this.getHibernateTemplate().flush();
	}

	public List<Nodo> obtenerNodosDelVendedor(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Nodo>>() {

			@Override
			public List<Nodo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("vendedor.id", idVendedor)); 
				return (List<Nodo>) criteria.list();
			}

		});

	}

	public Nodo obtenerNodoPorId(final Integer idNodo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Nodo>() {

			@Override
			public Nodo doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("id", idNodo));
				return (Nodo) criteria.uniqueResult();
			}

		});
	}

	public void eliminarNodo(Integer idNodo) {
		this.getHibernateTemplate().delete(this.obtenerNodoPorId(idNodo));
		this.getHibernateTemplate().flush();

	}


	public Nodo obtenerNodoPorAlias(final String alias) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Nodo>() {

			@Override
			public Nodo doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("alias", alias));
				return (Nodo) criteria.uniqueResult();
			}

		});
	}
}