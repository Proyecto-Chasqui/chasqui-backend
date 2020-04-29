package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.SolicitudPertenenciaNodoDAO;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.SolicitudPertenenciaNodo;

public class SolicitudPertenenciaNodoDAOHbm  extends HibernateDaoSupport implements SolicitudPertenenciaNodoDAO{

	@Override
	public void guardar(SolicitudPertenenciaNodo solicitud) {
		this.getSession().saveOrUpdate(solicitud);
		this.getSession().flush();	
		
	}

	@Override
	public SolicitudPertenenciaNodo obtenerSolicitudPertenenciaById(final Integer idSolicitud) {
		return this.getHibernateTemplate().execute(new HibernateCallback<SolicitudPertenenciaNodo>() {

			   @Override
			   public SolicitudPertenenciaNodo doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudPertenenciaNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.id", idSolicitud));

			    SolicitudPertenenciaNodo resultado = (SolicitudPertenenciaNodo) criteria.uniqueResult();
			    
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public SolicitudPertenenciaNodo obtenerSolicitudDe(final Integer idNodo, final Integer idCliente) {
		return this.getHibernateTemplate().execute(new HibernateCallback<SolicitudPertenenciaNodo>() {

			   @Override
			   public SolicitudPertenenciaNodo doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudPertenenciaNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("nodo.id", idNodo))
			    .add(Restrictions.eq("usuarioSolicitante.id", idCliente));

			    SolicitudPertenenciaNodo resultado = (SolicitudPertenenciaNodo) criteria.uniqueResult();
			   
			    return resultado; 
			   }
		});
		
	}

	@Override
	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeNodo(final Integer idNodo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudPertenenciaNodo>>() {

			   @Override
			   public List<SolicitudPertenenciaNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudPertenenciaNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("nodo.id", idNodo));

			    List<SolicitudPertenenciaNodo> resultado = (List<SolicitudPertenenciaNodo>) criteria.list();
			   
			    return resultado; 
			   }
		});
		
	}

	@Override
	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(final Integer idUsuario,
			final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudPertenenciaNodo>>() {

			   @Override
			   public List<SolicitudPertenenciaNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudPertenenciaNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("usuarioSolicitante.id", idUsuario))
			    .createAlias("nodo.vendedor", "vendedor")
			    .add(Restrictions.eq("vendedor.id", idVendedor));

			    List<SolicitudPertenenciaNodo> resultado = (List<SolicitudPertenenciaNodo>) criteria.list();
			   
			    return resultado; 
			   }
		});
	}

	@Override
	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePretenenciaDeVendedor(final Integer id) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudPertenenciaNodo>>() {

			   @Override
			   public List<SolicitudPertenenciaNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudPertenenciaNodo.class, "solicitud");
			    criteria.createAlias("solicitud.nodo", "nodo");
			    criteria.createAlias("nodo.vendedor", "vendedor");
			    criteria.add(Restrictions.eq("vendedor.id", id));

			    List<SolicitudPertenenciaNodo> resultado = (List<SolicitudPertenenciaNodo>) criteria.list();
			   
			    return resultado; 
			   }
		});
	}

	@Override
	public void eliminarSolicitudesDePertenencia(List<SolicitudPertenenciaNodo> solicitudesDePertenenciaDeVendedor) {
		for(SolicitudPertenenciaNodo solicitud : solicitudesDePertenenciaDeVendedor) {
			this.getSession().delete(solicitud);
			this.getSession().flush();	
		}
		
	}

}
