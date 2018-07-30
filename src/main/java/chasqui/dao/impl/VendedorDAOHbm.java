package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.VendedorDAO;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;

@SuppressWarnings("unchecked")
public class VendedorDAOHbm  extends HibernateDaoSupport implements VendedorDAO{

	
	@Override
	public List<Vendedor> obtenerVendedores() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Vendedor.class);
		criteria.add(Restrictions.eq("isRoot", false));
		criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
		//criteria.add(Restrictions.isNotNull("fechaCierrePedido"));
		return this.getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public Vendedor obtenerVendedor(final String username) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("username", username));
				criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
				//criteria.add(Restrictions.isNotNull("fechaCierrePedido"));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}
	
	public Vendedor obtenerVendedorRoot(final String username) {
		Vendedor u = this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			public Vendedor doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("username", username))
				.add(Restrictions.eq("isRoot", true));
				return (Vendedor) criteria.uniqueResult();
			}

		});
		return u;
	}
	
	@Override
	public Vendedor obtenerVendedorPorNombreCorto(final String nombreCorto) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("nombreCorto", nombreCorto));
				criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}

	@Override
	public Vendedor obtenerVendedorPorURL(final String url) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("url", url));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}

	@Override
	public List<PuntoDeRetiro> obtenerPuntosDeRetiroDeVendedor(final Integer idVendedor) {
		return (List<PuntoDeRetiro>) this.getHibernateTemplate().execute(new HibernateCallback<List<PuntoDeRetiro>>() {

			@Override
			public List<PuntoDeRetiro> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class)
				.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("id", idVendedor));
				Vendedor v = (Vendedor) criteria.uniqueResult();
				return v.getPuntosDeRetiro();
			}
		});
	}

	@Override
	public Vendedor obtenerVendedorPorId(final Integer idVendedor) {
		return (Vendedor) this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			@Override
			public Vendedor doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("isRoot", false))
				.add(Restrictions.eq("id", idVendedor));
				criteria.add(Restrictions.isNotNull("montoMinimoPedido"));
				return (Vendedor) criteria.uniqueResult();
			}
		});
	}

	@Override
	public List<PreguntaDeConsumo> obtenerPreguntasDeConsumoIndividuales(Integer idVendedor) {
		Vendedor v= obtenerVendedorPorId(idVendedor);
		return v.getPreguntasDePedidosIndividuales();
	}

	@Override
	public List<PreguntaDeConsumo> obtenerPreguntasDeConsumoColectivas(Integer idVendedor) {
		return obtenerVendedorPorId(idVendedor).getPreguntasDePedidosColectivos();
	}
}
