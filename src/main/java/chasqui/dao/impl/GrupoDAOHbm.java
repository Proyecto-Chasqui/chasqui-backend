package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.vividsolutions.jts.geom.Geometry;

import chasqui.dao.GrupoDAO;
import chasqui.model.GrupoCC;
import chasqui.model_lite.MiembroGCCLite;
import chasqui.view.composer.Constantes;

public class GrupoDAOHbm extends HibernateDaoSupport implements GrupoDAO {

	
	public void altaGrupo(GrupoCC grupo) {
		this.getHibernateTemplate().saveOrUpdate(grupo);
		this.getHibernateTemplate().flush();
	}
	
	public List<GrupoCC> obtenerGruposDeVendedor(final Integer idVendedor) {
			  return (List<GrupoCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<GrupoCC>>() {

			   @Override
			   public List<GrupoCC> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(GrupoCC.class);
			    criteria.add(Restrictions.eq("vendedor.id", idVendedor));
			    criteria.add(Restrictions.eq("esNodo", false));

			    List<GrupoCC> resultado = (List<GrupoCC>) criteria.list();
			    
			    
			    return resultado; 
			   }

			  });
	}
	@Override
	public List<GrupoCC> obtenerGruposDeVendedorCon(final Integer idVendedor, Date d, Date h, final String estadoSeleccionado) {
		  return (List<GrupoCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<GrupoCC>>() {

		   @Override
		   public List<GrupoCC> doInHibernate(Session session) throws HibernateException, SQLException {
		    Criteria criteria = session.createCriteria(GrupoCC.class,"grupo")
		    .createAlias("grupo.pedidoActual", "pedidoColectivo");
		    criteria.add(Restrictions.eq("grupo.vendedor.id", idVendedor))
		    .add(Restrictions.eq("pedidoColectivo.estado" , estadoSeleccionado));
		    criteria.add(Restrictions.eq("esNodo", false));
		    List<GrupoCC> resultado = (List<GrupoCC>) criteria.list();
		    
		    
		    return resultado; 
		   }

		  });
}

	@Override
	public void eliminarGrupoCC(GrupoCC grupoCC) {
		this.getHibernateTemplate().delete(grupoCC);
		this.getHibernateTemplate().flush();
	}
	

	public void guardarGrupo(GrupoCC grupoCC) {
		this.getHibernateTemplate().saveOrUpdate(grupoCC);
		this.getHibernateTemplate().flush();
	}
	
	@Override
	public GrupoCC obtenerGrupoPorId(final Integer idGrupoCC) {
		return this.getHibernateTemplate().execute(new HibernateCallback<GrupoCC>() {

			@Override
			public GrupoCC doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(GrupoCC.class);
				criteria.add(Restrictions.eq("id", idGrupoCC));
			    criteria.add(Restrictions.eq("esNodo", false));
				return (GrupoCC) criteria.uniqueResult();
			}

		});
	}

	@Override
	public List<GrupoCC> obtenerGruposDelClienteParaVendedor(final String email,final Integer idVendedor) {
		  return (List<GrupoCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<GrupoCC>>() {

			   @Override
			   public List<GrupoCC> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(GrupoCC.class)
			    		.add(Restrictions.eq("vendedor.id", idVendedor))
			    		.add(Restrictions.eq("esNodo", false))
			    		.createCriteria("cache").add(Restrictions.eq("email", email))
			    		.add(Restrictions.eq("estadoInvitacion",Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA));
			    		
			    
			    List<GrupoCC> resultado = (List<GrupoCC>) criteria.list();
			    
			    return resultado; 
			   }

			  });
	}
	
	@Override
	public List<GrupoCC> obtenerGruposEnUnArea(final Geometry area) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<GrupoCC>>() {
			
			@Override
			public List<GrupoCC> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(GrupoCC.class, "gcc")
				.createAlias("gcc.domicilioEntrega", "domicilioEntrega");//TODO el domicilio se saca del ultimo pedido! 2017.09.21
				criteria.add(SpatialRestrictions.within("domicilioEntrega.geoUbicacion", area));
			    criteria.add(Restrictions.eq("esNodo", false));
				return (List<GrupoCC>)criteria.list();
			}
		});	
	}

	@Override
	public GrupoCC obtenerGrupoAbsolutoPorId(final Integer idGrupo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<GrupoCC>() {

			@Override
			public GrupoCC doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(GrupoCC.class);
				criteria.add(Restrictions.eq("id", idGrupo));
				return (GrupoCC) criteria.uniqueResult();
			}

		});
	}

	@Override 
	public List<MiembroGCCLite> obtenerMiembrosDeGrupo(final Integer idGrupo) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<MiembroGCCLite>>() {

			@Override
			public List<MiembroGCCLite> doInHibernate(Session session) throws HibernateException, SQLException {
			 Criteria criteria = session.createCriteria(MiembroGCCLite.class, "miembro")
			 			.createAlias("miembro.cliente", "cliente", CriteriaSpecification.LEFT_JOIN)
					 .add(Restrictions.eq("idGrupo", idGrupo));
		 
			 return criteria.list(); 
			}
		 });
	}
	

}
