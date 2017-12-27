package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.NotificacionDAO;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Notificacion;

public class NotificacionDAOHbm  extends HibernateDaoSupport implements NotificacionDAO{

	@Override
	public void guardar(Notificacion n) {
		this.getSession().saveOrUpdate(n);
		this.getSession().flush();
		
	}

	@Override
	public List<Notificacion> obtenerNotificacionesPara(final String emailCliente, final String estado) {
		return (List<Notificacion>) this.getHibernateTemplate().execute(new HibernateCallback<List<Notificacion>>() {

			   @Override
			   public List<Notificacion> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(Notificacion.class);
			    
			    criteria
			            .add(Restrictions.eq("usuarioDestino", emailCliente))
			    		.add(Restrictions.eq("estado", estado));     
			    
			    
			    return (List<Notificacion>) criteria.list();
			   }

			  });
	}

	@Override
	public InvitacionAGCC obtenerNotificacionPorID(final Integer idInvitacion) {
		return (InvitacionAGCC) this.getHibernateTemplate().execute(new HibernateCallback<InvitacionAGCC>() {

			   @Override
			   public InvitacionAGCC doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(InvitacionAGCC.class);
			    
			    criteria.add(Restrictions.eq("id", idInvitacion));     
			    
			    return  (InvitacionAGCC) criteria.uniqueResult(); 
			   }

			  });
	}

	@Override
	public void eliminar(Notificacion notificacion) {
		this.getHibernateTemplate().delete(notificacion);
		this.getHibernateTemplate().flush();	
	}

	@Override
	public List<InvitacionAGCC> obtenerInvitacionPendientePorIDdeGrupo(final String emailCliente, final Integer idGrupo) {
		return (List<InvitacionAGCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<InvitacionAGCC>>() {

			   @Override
			   public List<InvitacionAGCC> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(InvitacionAGCC.class);
			    
			    criteria
			    .add(Restrictions.eq("usuarioDestino",emailCliente))
			    .add(Restrictions.eq("idGrupo", idGrupo));     
			    
			    return  (List<InvitacionAGCC>) criteria.list(); 
			   }

			  });
	}

}
