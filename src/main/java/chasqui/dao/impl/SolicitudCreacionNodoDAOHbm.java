package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.SolicitudCreacionNodoDAO;
import chasqui.model.SolicitudCreacionNodo;

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
			    .add(Restrictions.eq("usuarioSolicitante.id", idCliente)); 

			    List<SolicitudCreacionNodo> resultado = (List<SolicitudCreacionNodo>) criteria.list();
			    
			    
			    return resultado; 
			   }
		});
	}
	
	

}
