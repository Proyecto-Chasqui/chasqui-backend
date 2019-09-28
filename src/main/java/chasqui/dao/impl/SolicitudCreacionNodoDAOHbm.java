package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.SolicitudCreacionNodoDAO;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.view.composer.Constantes;

public class SolicitudCreacionNodoDAOHbm extends HibernateDaoSupport implements SolicitudCreacionNodoDAO{

	@Override
	public void guardar(SolicitudCreacionNodo solicitud) {
		this.getSession().saveOrUpdate(solicitud);
		this.getSession().flush();		
	}
	
	@Override
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(final Integer idCliente, final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudCreacionNodo>>() {

			   @Override
			   public List<SolicitudCreacionNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.idVendedor", idVendedor))
			    .add(Restrictions.eq("solicitud.usuarioSolicitante.id", idCliente)); 

			    List<SolicitudCreacionNodo> resultado = (List<SolicitudCreacionNodo>) criteria.list();
			    
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public SolicitudCreacionNodo obtenerSolitudCreacionNodoEnGestion(final Integer idCliente, final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<SolicitudCreacionNodo>() {

			   @Override
			   public SolicitudCreacionNodo doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.idVendedor", idVendedor))
			    .add(Restrictions.eq("solicitud.usuarioSolicitante.id", idCliente))
			    .add(Restrictions.eq("solicitud.estado", Constantes.SOLICITUD_NODO_EN_GESTION)); 

			    SolicitudCreacionNodo resultado = (SolicitudCreacionNodo) criteria.uniqueResult();
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public SolicitudCreacionNodo obtenerSolitudCreacionNodo(final Integer idSolicitud, final Integer idCliente, final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<SolicitudCreacionNodo>() {

			   @Override
			   public SolicitudCreacionNodo doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.idVendedor", idVendedor))
			    .add(Restrictions.eq("solicitud.usuarioSolicitante.id", idCliente))
			    .add(Restrictions.eq("solicitud.id", idSolicitud)); 

			    SolicitudCreacionNodo resultado = (SolicitudCreacionNodo) criteria.uniqueResult();
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionEnGestionDe(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudCreacionNodo>>() {

			   @Override
			   public List<SolicitudCreacionNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.idVendedor", idVendedor))
			    .add(Restrictions.eq("solicitud.estado", Constantes.SOLICITUD_NODO_EN_GESTION)); 

			    List<SolicitudCreacionNodo> resultado = (List<SolicitudCreacionNodo>) criteria.list();
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudCreacionNodo>>() {

			   @Override
			   public List<SolicitudCreacionNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    criteria.add(Restrictions.eq("solicitud.idVendedor", idVendedor));

			    List<SolicitudCreacionNodo> resultado = (List<SolicitudCreacionNodo>) criteria.list();
			    
			    return resultado; 
			   }
		});
	}

	@Override
	public Collection<? extends SolicitudCreacionNodo> obtenerSolicitudesDeCreacionNodosDelVendedorCon( final Integer idVendedor,
			final Date d,final Date h,final String estado,final String nombreCoordinador,final String email,final String barrio) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<SolicitudCreacionNodo>>() {

			   @Override
			   public List<SolicitudCreacionNodo> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria c = session.createCriteria(SolicitudCreacionNodo.class, "solicitud");
			    c.createAlias("solicitud.usuarioSolicitante", "usuarioSolicitante");
			    c.add(Restrictions.eq("solicitud.idVendedor", idVendedor));
			    
				if(!StringUtils.isEmpty(estado)) {
					c.add(Restrictions.eq("solicitud.estado", estado));
				}
				
				if(!StringUtils.isEmpty(nombreCoordinador)) {
					Criterion crit = Restrictions.like("usuarioSolicitante.nombre", "%"+nombreCoordinador+"%");
					Criterion crit2= Restrictions.like("usuarioSolicitante.apellido", "%"+nombreCoordinador+"%");
					c.add(Restrictions.or(crit,crit2));
				}

				if(!StringUtils.isEmpty(email)) {
					c.add(Restrictions.like("usuarioSolicitante.email", "%"+email+"%"));
				}
				
				if(!StringUtils.isEmpty(barrio)) {
					c.add(Restrictions.like("solicitud.barrio", "%"+barrio+"%"));
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

			    List<SolicitudCreacionNodo> resultado = (List<SolicitudCreacionNodo>) c.list();
			    
			    return resultado; 
			   }
		});
	}
	
	

}
