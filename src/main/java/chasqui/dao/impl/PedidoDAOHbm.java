package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.PedidoDAO;
import chasqui.model.Pedido;
import chasqui.model.Zona;
import chasqui.view.composer.Constantes;

public class PedidoDAOHbm extends HibernateDaoSupport implements PedidoDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosProximosAVencer(final Integer cantidadDeDias, final Integer idVendedor,
			final DateTime fechaCierrePedido) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class);
				criteria.add(Restrictions.eq("alterable", true)).add(Restrictions.eq("idVendedor", idVendedor))
						.add(Restrictions.eq("fechaDeVencimiento", fechaCierrePedido))
						.add(Restrictions.eq("estado", Constantes.ESTADO_PEDIDO_ABIERTO))
						.add(Restrictions.eq("perteneceAPedidoGrupal", false))
						.add(Restrictions.le("fechaDeVencimiento", new DateTime().plusDays(cantidadDeDias)));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosProximosAVencerEnDeterminadaZona(final Integer cantidadDeDias,
			final Integer idVendedor, final DateTime fechaCierrePedido, final Zona zona) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class, "pedido")
						.createAlias("pedido.direccionEntrega", "direccionEntrega");
				criteria.add(Restrictions.eq("pedido.alterable", true))
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.eq("pedido.fechaDeVencimiento", fechaCierrePedido))
						.add(Restrictions.eq("pedido.estado", Constantes.ESTADO_PEDIDO_ABIERTO))
						.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false))
						.add(Restrictions.le("pedido.fechaDeVencimiento", new DateTime().plusDays(cantidadDeDias)))
						.add(SpatialRestrictions.within("direccionEntrega.geoUbicacion", zona.getGeoArea()));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidos(final Integer idVendedor) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class)
						.add(Restrictions.eq("idVendedor", idVendedor))
						.addOrder(Order.desc("id"));
				return (List<Pedido>) c.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosIndividuales(final Integer idVendedor) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class,"pedido")
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false))
						.addOrder(Order.desc("id"));
				return (List<Pedido>) c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosEnDeterminadaZona(final Integer idVendedor, final Zona zona) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class, "pedido")
						.createAlias("pedido.direccionEntrega", "direccionEntrega")
						.add(Restrictions.eq("idVendedor", idVendedor))
						.add(SpatialRestrictions.within("direccionEntrega.geoUbicacion", zona.getGeoArea()))
						.setMaxResults(100).addOrder(Order.desc("id"));
				return (List<Pedido>) c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pedido obtenerPedidoPorId(final Integer idPedido) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Pedido>() {

			@Override
			public Pedido doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class).add(Restrictions.eq("id", idPedido));
				return (Pedido) c.uniqueResult();
			}
		});
	}

	@Override
	public void guardar(Pedido p) {
		this.getHibernateTemplate().saveOrUpdate(p);
		this.getHibernateTemplate().flush();

	}

	@Override
	public Integer obtenerTotalPaginasDePedidosParaVendedor(final Integer id) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Integer>() {

			@Override
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class);
				c.add(Restrictions.eq("idVendedor", id)).setProjection(Projections.rowCount());
				return ((Long) c.uniqueResult()).intValue();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidos(final Integer idVendedor, final Date desde, final Date hasta,
			final String estadoSeleccionado) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class).add(Restrictions.eq("idVendedor", idVendedor))
						.setMaxResults(100).addOrder(Order.desc("id"));
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("estado", estadoSeleccionado));
				}
				if (desde != null && hasta != null) {
					DateTime d = new DateTime(desde.getTime());
					DateTime h = new DateTime(hasta.getTime());
					c.add(Restrictions.between("fechaCreacion", d, h));
				}

				return (List<Pedido>) c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosEnDeterminadaZona(final Integer idVendedor, final Date desde, final Date hasta,
			final String estadoSeleccionado, final Zona zona) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {

				Criteria c = session.createCriteria(Pedido.class, "pedido")
						.createAlias("pedido.direccionEntrega", "direccionEntrega")
						.add(Restrictions.eq("pedido.idVendedor", idVendedor)).setMaxResults(100)
						.addOrder(Order.desc("pedido.id"))
						.add(SpatialRestrictions.within("direccionEntrega.geoUbicacion", zona.getGeoArea()));
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedido.estado", estadoSeleccionado));
				}
				if (desde != null && hasta != null) {
					DateTime d = new DateTime(desde.getTime());
					DateTime h = new DateTime(hasta.getTime());
					c.add(Restrictions.between("pedido.fechaCreacion", d, h));
				}

				return (List<Pedido>) c.list();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> obtenerPedidosAbiertosConFechaVencida(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class);
				criteria.add(Restrictions.eq("alterable", true))
						.add(Restrictions.eq("estado", Constantes.ESTADO_PEDIDO_ABIERTO))
						//.add(Restrictions.eq("perteneceAPedidoGrupal", false))// TODO sacar
						.add(Restrictions.eq("idVendedor", idVendedor))
						.add(Restrictions.lt("fechaDeVencimiento", new DateTime()));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> obtenerPedidosVencidos() {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class);
				criteria.add(Restrictions.eq("alterable", true))
						.add(Restrictions.eq("estado", Constantes.ESTADO_PEDIDO_VENCIDO))
						.add(Restrictions.eq("perteneceAPedidoGrupal", false));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> obtenerPedidosDeConEstado(final Integer idUsuario,final Integer idVendedor, final List<String> estados) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class, "pedido");
				
				criteria.add(Restrictions.eq("pedido.cliente.id", idUsuario))
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.in("estado", estados));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@Override
	public List<Pedido> obtenerPedidosIndividualesDeVendedor(final Integer idVendedor) {
		
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public
			List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class);
				criteria.add(Restrictions.eq("perteneceAPedidoGrupal", false))
						.add(Restrictions.eq("idVendedor", idVendedor))
						.addOrder(Order.desc("id"));
				return (List<Pedido>) criteria.list();
			}
		});

	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosIndividualesDeVendedor(final Integer idVendedor, final Date desde, final Date hasta,
			final String estadoSeleccionado, final Integer zonaId, final Integer idPuntoRetiro, final String email) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class,"pedido")
				.add(Restrictions.eq("pedido.idVendedor", idVendedor))
				.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false))
				.addOrder(Order.desc("pedido.id"));
				
				if(zonaId!=null){
					c.createAlias("pedido.zona", "zona");
					c.add(Restrictions.eq("zona.id", zonaId));
				}
				
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedido.estado", estadoSeleccionado));
				}
				if (desde != null && hasta != null) {
					DateTime d = new DateTime(desde.getTime());
					DateTime h = new DateTime(hasta.getTime());
					c.add(Restrictions.between("pedido.fechaCreacion", d.withHourOfDay(0), h.plusDays(1).withHourOfDay(0)));
				}else{
					if(desde!=null){
						DateTime d = new DateTime(desde.getTime());
						c.add(Restrictions.ge("pedido.fechaCreacion", d.withHourOfDay(0)));
					}else{
						if(hasta!=null){
							DateTime h = new DateTime(hasta.getTime());
							c.add(Restrictions.le("pedido.fechaCreacion", h.plusDays(1).withHourOfDay(0)));
						}
					}
				}
				
				if(idPuntoRetiro!=null) {
					c.add(Restrictions.eq("puntoDeRetiro.id",idPuntoRetiro));
				}
				
				if(email!=null) {
					c.createAlias("pedido.cliente", "cliente");
					c.add(Restrictions.like("cliente.email", "%"+email+"%"));
				}
				

				return (List<Pedido>) c.list();
			}
		});
	}



}
