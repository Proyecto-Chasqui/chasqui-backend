package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
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
				criteria.add(Restrictions.eq("vendedor.id", idVendedor))
				.add(Restrictions.eq("esNodo", true)); 
				return (List<Nodo>) criteria.list();
			}

		});

	}

	public Nodo obtenerNodoPorId(final Integer idNodo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Nodo>() {

			@Override
			public Nodo doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("id", idNodo))
				.add(Restrictions.eq("esNodo", true)); 
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
				criteria.add(Restrictions.eq("alias", alias))
				.add(Restrictions.eq("esNodo", true)); 
				return (Nodo) criteria.uniqueResult();
			}

		});
	}
	
	@Override
	public List<Nodo> obtenerNodosDelCliente(final Integer idVendedor, final String email) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Nodo>>() {

			@Override
			public List<Nodo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("vendedor.id", idVendedor))
				.add(Restrictions.eq("esNodo", true))
	    		.createCriteria("cache").add(Restrictions.eq("email", email))
	    		.add(Restrictions.eq("estadoInvitacion",Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA));
				
				return (List<Nodo>) criteria.list();
			}

		});

	}

	public List<Nodo> obtenerNodosAbiertosDelVendedor(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Nodo>>() {

			@Override
			public List<Nodo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Nodo.class);
				criteria.add(Restrictions.eq("vendedor.id", idVendedor))
				.add(Restrictions.eq("esNodo", true))
				.add(Restrictions.eq("tipo",Constantes.NODO_ABIERTO))
				.createCriteria("pedidoActual")
				.add(Restrictions.ne("estado", Constantes.ESTADO_PEDIDO_CANCELADO));
				
				return (List<Nodo>) criteria.list();
			}

		});
	}

	public List<Nodo> obtenerNodosDelVendedorCon(final Integer idVendedor, final Date d, final Date h, final String estadoNodo,
			final String nombreNodo, final String emailcoordinador, final String barrio, final String tipo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Nodo>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Nodo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Nodo.class);
				c.add(Restrictions.eq("vendedor.id", idVendedor))
				.add(Restrictions.eq("esNodo", true));
				
				if(!StringUtils.isEmpty(tipo)) {
					c.add(Restrictions.eq("tipo", tipo));
				}
				if (!StringUtils.isEmpty(nombreNodo)) {
					c.add(Restrictions.like("alias", "%"+nombreNodo+"%"));
				}
				if (d != null && h != null) {
					DateTime desde = new DateTime(d.getTime());
					DateTime hasta = new DateTime(h.getTime());
					c.add(Restrictions.between("fechaCreacion", desde.withHourOfDay(0), hasta.plusDays(1).withHourOfDay(0)));
				}else{
					if(d!=null){
						DateTime desde = new DateTime(d.getTime());
						c.add(Restrictions.ge("fechaCreacion", desde.withHourOfDay(0)));
					}else{
						if(h!=null){
							DateTime hasta = new DateTime(h.getTime());
							c.add(Restrictions.le("fechaCreacion", hasta.plusDays(1).withHourOfDay(0)));
						}
					}
				}
				if(!StringUtils.isEmpty(estadoNodo)) {
					boolean activo = estadoNodo.equals(Constantes.NODO_ACTIVO); 
					c.add(Restrictions.eq("activo",activo));
				}
				if(!StringUtils.isEmpty(emailcoordinador)) {
					c.add(Restrictions.like("emailAdministradorNodo", "%"+emailcoordinador+"%"));
				}
				if(!StringUtils.isEmpty(barrio)) {
					c.add(Restrictions.like("barrio", "%"+barrio+"%"));
				}
				
				return (List<Nodo>) c.list();
			}

		});
	}


}