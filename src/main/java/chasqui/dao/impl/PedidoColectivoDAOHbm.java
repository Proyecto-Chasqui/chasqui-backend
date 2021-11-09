package chasqui.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.PedidoColectivoDAO;
import chasqui.model.GrupoCC;
import chasqui.model.PedidoColectivo;
import chasqui.model_lite.PedidoColectivoStats;
import chasqui.model_lite.PedidoColectivoStatsByEstado;
import chasqui.model_lite.PedidoStatsLite;
import chasqui.model_lite.ProductoPedidoLiteAgrupados;
import chasqui.view.composer.Constantes;

import org.apache.log4j.Logger;

public class PedidoColectivoDAOHbm extends HibernateDaoSupport implements PedidoColectivoDAO {
	private static final Logger logger = Logger.getLogger(PedidoColectivoDAOHbm.class);

	@Override
	public void guardar(PedidoColectivo p) {
		this.getHibernateTemplate().saveOrUpdate(p);
		this.getHibernateTemplate().flush();
	}

	@Override
	public PedidoColectivo obtenerPedidoColectivoPorID(final Integer id) {
		return this.getHibernateTemplate().execute(new HibernateCallback<PedidoColectivo>() {

			public PedidoColectivo doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PedidoColectivo.class);
				criteria.add(Restrictions.eq("id", id));
				return (PedidoColectivo) criteria.uniqueResult();
			}

		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public PedidoColectivoStatsByEstado calcularStatsPedidoColectivoActivo(final Integer grupoId) {
		return this.getHibernateTemplate().execute(new HibernateCallback<PedidoColectivoStatsByEstado>() {

			@Override
			public PedidoColectivoStatsByEstado doInHibernate(Session session) throws HibernateException, SQLException {

				 String queryStr = 
									  " SELECT   "
									+ "  count(DISTINCT PEDIDO.ID) as cantPedidos, "
									+ "  sum(pp.CANTIDAD*pp.PRECIO) as montoActual, "
									+ "  sum(pp.CANTIDAD*pp.INCENTIVO) as incentivoActual, "
									+ "  sum(pp.CANTIDAD*VARIANTE.PESO_GRAMOS) as pesoGramosActual, "
									+ "  PEDIDO.ESTADO as estadoPedido "
									+ " FROM PRODUCTO_PEDIDO as pp "
									+ " RIGHT JOIN VARIANTE on VARIANTE.ID = pp.ID_VARIANTE "
									+ " RIGHT JOIN PEDIDO on PEDIDO.ID = pp.ID_PEDIDO "
									+ " RIGHT JOIN PEDIDO_COLECTIVO ON PEDIDO_COLECTIVO.ID = PEDIDO.ID_PEDIDO_COLECTIVO "
									+ " WHERE   "
									+ "      pp.ID is not null"
									+ "  AND PEDIDO_COLECTIVO.ESTADO = :estadoPedido "
									+ "  AND PEDIDO_COLECTIVO.COLECTIVO = :idColectivo "
								+ " GROUP BY PEDIDO.ESTADO ";
									
									

				SQLQuery q = session.createSQLQuery(queryStr);

				q.setString("estadoPedido", Constantes.ESTADO_PEDIDO_ABIERTO);
				q.setInteger("idColectivo", grupoId);
				q.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

				Date now = new Date();
				
				HashMap<String, PedidoColectivoStats> map = new HashMap<>();

				boolean hasAbiertos = false;
				boolean hasConfirmados = false;

				List<HashMap<String, Object>> list = q.list();
				for (HashMap<String, Object> row : list) {
						String estadoPedido = (String) row.get("estadoPedido");
						if(estadoPedido != null) {
							hasAbiertos = hasAbiertos || estadoPedido.equals(Constantes.ESTADO_PEDIDO_ABIERTO);
							hasConfirmados = hasConfirmados || estadoPedido.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO);
							PedidoColectivoStats stats = new PedidoColectivoStats();
							stats.setCantPedidos(((BigInteger) row.get("cantPedidos")).intValue());
							stats.setMontoActual((Double) row.get("montoActual"));
							stats.setIncentivoActual((Double) row.get("incentivoActual"));
							stats.setPesoGramosActual(((BigDecimal) row.get("pesoGramosActual")).intValue());
							stats.setEstadoPedido(estadoPedido);
							stats.setSnapshotDate(now);
							map.put(estadoPedido, stats);
						}
				}

				if(!hasAbiertos) {
					map.put(Constantes.ESTADO_PEDIDO_ABIERTO, pedidoColectivoStatsVacio(Constantes.ESTADO_PEDIDO_ABIERTO));
				}

				if(!hasConfirmados) {
					map.put(Constantes.ESTADO_PEDIDO_CONFIRMADO, pedidoColectivoStatsVacio(Constantes.ESTADO_PEDIDO_CONFIRMADO));
				}

				PedidoColectivoStatsByEstado byEstado = new PedidoColectivoStatsByEstado();
				byEstado.setStats(map);
				byEstado.setSnapshotDate(now);
				return byEstado;
			}
		});
	}

	private PedidoColectivoStats pedidoColectivoStatsVacio(String estado) {
		PedidoColectivoStats stats = new PedidoColectivoStats();
		stats.setCantPedidos(0);
		stats.setMontoActual(0d);
		stats.setIncentivoActual(0d);
		stats.setPesoGramosActual(0);
		stats.setEstadoPedido(estado);
		stats.setSnapshotDate(new Date());
		return stats;
	}

	@Override
	public List<PedidoStatsLite> calcularPedidosStatsLite(final Integer grupoId) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<PedidoStatsLite>>() {

			@Override
			public List<PedidoStatsLite> doInHibernate(Session session) throws HibernateException, SQLException {

				 String queryStr = 
							 " SELECT  "
							+ "  PEDIDO.ID as id,"
							+ "  PEDIDO.ESTADO as estado,"
							+ "  count(distinct pp.ID_VARIANTE) as cantProductos,"
							+ "  sum(distinct pp.CANTIDAD) as cantItems,"
							+ "  sum(pp.CANTIDAD*pp.precio) as montoActual,"
							+ "  sum(pp.CANTIDAD*pp.INCENTIVO) as incentivoActual,"
							+ "  sum(pp.CANTIDAD*VARIANTE.PESO_GRAMOS) as pesoGramosActual"
							+ " FROM PRODUCTO_PEDIDO as pp"
							+ " RIGHT JOIN VARIANTE on VARIANTE.ID = pp.ID_VARIANTE"
							+ " RIGHT JOIN PEDIDO on PEDIDO.ID = pp.ID_PEDIDO"
							+ " RIGHT JOIN PEDIDO_COLECTIVO ON PEDIDO_COLECTIVO.ID = PEDIDO.ID_PEDIDO_COLECTIVO"
							+ " WHERE  "
							+ "     pp.ID is not null"
							+ " AND PEDIDO_COLECTIVO.ESTADO = :estadoPedidoColectivo "
							+ " AND PEDIDO_COLECTIVO.COLECTIVO = :idColectivo "
							+ " GROUP BY PEDIDO.ID";
 
				SQLQuery q = session.createSQLQuery(queryStr);

				q.setString("estadoPedidoColectivo", Constantes.ESTADO_PEDIDO_ABIERTO);
				q.setInteger("idColectivo", grupoId);
				q.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

				Date now = new Date();
				
				List<PedidoStatsLite> out = new ArrayList<>();

				List<HashMap<String, Object>> list = q.list();
				for (HashMap<String, Object> row : list) {
						Integer id = (Integer) row.get("id");
						if(id != null) {
							PedidoStatsLite stats = new PedidoStatsLite();
							stats.setId(id);
							stats.setEstado((String) row.get("estado"));
							stats.setMontoActual((Double) row.get("montoActual"));
							stats.setIncentivoActual((Double) row.get("incentivoActual"));
							stats.setPesoGramosActual(((BigDecimal) row.get("pesoGramosActual")).intValue());
							stats.setCantidadProductos(((BigInteger) row.get("cantProductos")).intValue());
							stats.setCantidadItems(((BigDecimal)row.get("cantItems")).intValue());
							stats.setSnapshotDate(now);
							out.add(stats);
						}
				}
				return out;
			}
		});
	
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductoPedidoLiteAgrupados> productosPedidoColectivoActivo(final Integer grupoId) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<ProductoPedidoLiteAgrupados>>() {

			@Override
			public List<ProductoPedidoLiteAgrupados> doInHibernate(Session session) throws HibernateException, SQLException {

				 String queryStr = 
								  " SELECT  "
								+ "   sum(pp.CANTIDAD) as CANT,"
								+ "   sum(pp.CANTIDAD*pp.precio) as TOTAL,"
								+ "   pp.ID_VARIANTE,"
								+ "   pp.PRECIO,"
								+ "   pp.INCENTIVO,"
								+ "   pp.IMAGEN,"
								+ "   VARIANTE.PESO_GRAMOS,"
								+ "   PEDIDO.ID_PEDIDO_COLECTIVO,"
								+ "   PRODUCTO.NOMBRE "
								+ " FROM PRODUCTO_PEDIDO as pp"
								+ " RIGHT JOIN VARIANTE on VARIANTE.ID = pp.ID_VARIANTE"
								+ " RIGHT JOIN PRODUCTO on PRODUCTO.ID = VARIANTE.ID_PRODUCTO "
								+ " RIGHT JOIN PEDIDO on PEDIDO.ID = pp.ID_PEDIDO"
								+ " RIGHT JOIN PEDIDO_COLECTIVO ON PEDIDO_COLECTIVO.ID = PEDIDO.ID_PEDIDO_COLECTIVO"
								+ " WHERE  "
								+ "    PEDIDO_COLECTIVO.ESTADO = :estadoPedidoColectivo "
								+ " AND PEDIDO.ESTADO = :estadoPedido "
								+ " AND PEDIDO_COLECTIVO.COLECTIVO = :idColectivo "
								+ " GROUP BY pp.ID_VARIANTE"
								+ " ORDER BY TOTAL desc";

				SQLQuery q = session.createSQLQuery(queryStr);

				q.setString("estadoPedidoColectivo", "ABIERTO");
				q.setString("estadoPedido", "CONFIRMADO");
				q.setInteger("idColectivo", grupoId);
				q.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

				List<ProductoPedidoLiteAgrupados> out = new ArrayList<>();

				List<HashMap<String, Object>> list = q.list();
				for (HashMap<String, Object> row : list) {
						ProductoPedidoLiteAgrupados productoGrupo = new ProductoPedidoLiteAgrupados();
						BigDecimal cant = (BigDecimal) row.get("CANT");
						if(cant != null) {
							productoGrupo.setNombre((String) row.get("NOMBRE"));
							productoGrupo.setCantidad(cant.intValue());
							productoGrupo.setPrecio((Double) row.get("PRECIO"));
							productoGrupo.setIncentivo((Double) row.get("INCENTIVO"));
							productoGrupo.setPesoGramosUnidad(((Integer) row.get("PESO_GRAMOS")));
							productoGrupo.setIdVariante((Integer) row.get("ID_VARIANTE"));
							productoGrupo.setIdPedidoColectivo((Integer) row.get("ID_PEDIDO_COLECTIVO"));
							productoGrupo.setImagen((String) row.get("IMAGEN"));

						}
						out.add(productoGrupo);
				}
				return out;
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PedidoColectivo> obtenerPedidosColectivosDeConEstado(final Integer idUsuario, final Integer idGrupo,
			final List<String> estados) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PedidoColectivo.class, "pedido");
				criteria.createAlias("pedido.colectivo", "colectivo");
				criteria.add(Restrictions.eq("colectivo.id", idGrupo)).add(Restrictions.in("pedido.estado", estados));
				return (List<PedidoColectivo>) criteria.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedor(final Integer vendedorid,
			final Date d, final Date h, final String estadoSeleccionado, final Integer zonaId, final Integer idPuntoRetiro,
			final String emailAdmin) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(PedidoColectivo.class, "pedidoColectivo")
						.createAlias("pedidoColectivo.colectivo", "grupoCC").createAlias("grupoCC.vendedor", "vendedor")
						.add(Restrictions.eq("vendedor.id", vendedorid)).addOrder(Order.desc("pedidoColectivo.id"));
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedidoColectivo.estado", estadoSeleccionado));
				}
				if (d != null && h != null) {
					DateTime desde = new DateTime(d.getTime());
					DateTime hasta = new DateTime(h.getTime());
					c.add(Restrictions.between("pedidoColectivo.fechaCreacion", desde.withHourOfDay(0),
							hasta.plusDays(1).withHourOfDay(0)));
				} else {
					if (d != null) {
						DateTime desde = new DateTime(d.getTime());
						c.add(Restrictions.ge("pedidoColectivo.fechaCreacion", desde.withHourOfDay(0)));
					} else {
						if (h != null) {
							DateTime hasta = new DateTime(h.getTime());
							c.add(Restrictions.le("pedidoColectivo.fechaCreacion", hasta.plusDays(1).withHourOfDay(0)));
						}
					}
				}
				if (idPuntoRetiro != null) {
					c.add(Restrictions.eq("puntoDeRetiro.id", idPuntoRetiro));
				}
				if (zonaId != null) {
					c.createAlias("pedidoColectivo.zona", "zona");
					c.add(Restrictions.eq("zona.id", zonaId));
				}
				if (emailAdmin != null) {
					if (!emailAdmin.equals("")) {
						c.createAlias("grupoCC.administrador", "administrador");
						c.add(Restrictions.like("administrador.email", "%" + emailAdmin + "%"));
					}
				}
				return c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorConPRPorNombre(
			final Integer vendedorid, final Date d, final Date h, final String estadoSeleccionado, final Integer zonaId,
			final String puntoRetiro, final String emailAdmin) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(PedidoColectivo.class, "pedidoColectivo")
						.createAlias("pedidoColectivo.colectivo", "grupoCC").createAlias("grupoCC.vendedor", "vendedor")
						.add(Restrictions.eq("vendedor.id", vendedorid)).addOrder(Order.desc("pedidoColectivo.id"));
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedidoColectivo.estado", estadoSeleccionado));
				}
				if (d != null && h != null) {
					DateTime desde = new DateTime(d.getTime());
					DateTime hasta = new DateTime(h.getTime());
					c.add(Restrictions.between("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0),
							hasta.plusDays(1).withHourOfDay(0)));
				} else {
					if (d != null) {
						DateTime desde = new DateTime(d.getTime());
						c.add(Restrictions.ge("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0)));
					} else {
						if (h != null) {
							DateTime hasta = new DateTime(h.getTime());
							c.add(Restrictions.le("pedidoColectivo.fechaModificacion", hasta.plusDays(1).withHourOfDay(0)));
						}
					}
				}
				if (puntoRetiro != null && !puntoRetiro.equals("")) {
					c.createAlias("pedidoColectivo.puntoDeRetiro", "puntoDeRetiro");
					c.add(Restrictions.eq("puntoDeRetiro.nombre", puntoRetiro));
				}
				if (zonaId != null) {
					c.createAlias("pedidoColectivo.zona", "zona");
					c.add(Restrictions.eq("zona.id", zonaId));
				}
				if (emailAdmin != null) {
					if (!emailAdmin.equals("")) {
						c.createAlias("grupoCC.administrador", "administrador");
						c.add(Restrictions.like("administrador.email", "%" + emailAdmin + "%"));
					}
				}
				return c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends PedidoColectivo> obtenerPedidosColectivosDeVendedorDeGrupo(Integer vendedorid,
			final Integer grupoID, final Date d, final Date h, final String estadoSeleccionado, final Integer zonaId,
			final Integer idPuntoRetiro) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(GrupoCC.class, "grupo").add(Restrictions.eq("grupo.id", grupoID));

				GrupoCC grupo = (GrupoCC) c.uniqueResult();
				List<PedidoColectivo> pedidos = grupo.getHistorial().getPedidosGrupales();
				List<PedidoColectivo> pedidosRet = new ArrayList<PedidoColectivo>();
				if (!pedidos.isEmpty()) {
					Criteria pedidosColectivos = session.createCriteria(PedidoColectivo.class, "pedidoColectivo")
							.addOrder(Order.desc("pedidoColectivo.id"));
					if (zonaId != null) {
						pedidosColectivos.createAlias("pedidoColectivo.zona", "zona");
						pedidosColectivos.add(Restrictions.eq("zona.id", zonaId));
					}
					Disjunction r = Restrictions.disjunction();
					for (PedidoColectivo pedido : pedidos) {
						r.add(Restrictions.eq("pedidoColectivo.id", pedido.getId()));
					}
					pedidosColectivos.add(r);
					if (!StringUtils.isEmpty(estadoSeleccionado)) {
						pedidosColectivos.add(Restrictions.eq("pedidoColectivo.estado", estadoSeleccionado));
					}
					if (d != null && h != null) {
						DateTime desde = new DateTime(d.getTime());
						DateTime hasta = new DateTime(h.getTime());
						pedidosColectivos.add(Restrictions.between("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0),
								hasta.plusDays(1).withHourOfDay(0)));
					} else {
						if (d != null) {
							DateTime desde = new DateTime(d.getTime());
							pedidosColectivos.add(Restrictions.ge("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0)));
						} else {
							if (h != null) {
								DateTime hasta = new DateTime(h.getTime());
								pedidosColectivos
										.add(Restrictions.le("pedidoColectivo.fechaModificacion", hasta.plusDays(1).withHourOfDay(0)));
							}
						}
					}
					if (idPuntoRetiro != null) {
						pedidosColectivos.add(Restrictions.eq("puntoDeRetiro.id", idPuntoRetiro));
					}

					pedidosRet = pedidosColectivos.list();
				}
				return pedidosRet;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoColectivo> obtenerPedidosColectivosDeGrupo(final Integer grupoid) {

		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(GrupoCC.class, "grupo").add(Restrictions.eq("grupo.id", grupoid));

				GrupoCC grupo = (GrupoCC) c.uniqueResult();
				List<PedidoColectivo> pedidoRet = new ArrayList<PedidoColectivo>();
				List<PedidoColectivo> pedidos = grupo.getHistorial().getPedidosGrupales();
				if (!pedidos.isEmpty()) {
					Criteria pedidosColectivos = session.createCriteria(PedidoColectivo.class, "pedidoColectivo")
							.addOrder(Order.desc("pedidoColectivo.id"));

					Disjunction r = Restrictions.disjunction();
					for (PedidoColectivo pedido : pedidos) {
						r.add(Restrictions.eq("pedidoColectivo.id", pedido.getId()));
					}

					pedidosColectivos.add(r);

					pedidoRet = (List<PedidoColectivo>) pedidosColectivos.list();
				}
				return pedidoRet;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoColectivo> obtenerPedidosColectivosDeNodosDeVendedorConPRConNombre(final Integer vendedorid,
			final Date d, final Date h, final String estadoSeleccionado, final Integer zonaId, final String puntoRetiro,
			final String queryNodo) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoColectivo>>() {

			@Override
			public List<PedidoColectivo> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(PedidoColectivo.class, "pedidoColectivo")
						.createAlias("pedidoColectivo.colectivo", "grupoCC").createAlias("grupoCC.vendedor", "vendedor")
						.add(Restrictions.eq("vendedor.id", vendedorid)).add(Restrictions.eq("grupoCC.esNodo", true))
						.addOrder(Order.desc("pedidoColectivo.id"));
				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedidoColectivo.estado", estadoSeleccionado));
				}
				if (d != null && h != null) {
					DateTime desde = new DateTime(d.getTime());
					DateTime hasta = new DateTime(h.getTime());
					c.add(Restrictions.between("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0),
							hasta.plusDays(1).withHourOfDay(0)));
				} else {
					if (d != null) {
						DateTime desde = new DateTime(d.getTime());
						c.add(Restrictions.ge("pedidoColectivo.fechaModificacion", desde.withHourOfDay(0)));
					} else {
						if (h != null) {
							DateTime hasta = new DateTime(h.getTime());
							c.add(Restrictions.le("pedidoColectivo.fechaModificacion", hasta.plusDays(1).withHourOfDay(0)));
						}
					}
				}
				if (puntoRetiro != null && !puntoRetiro.equals("")) {
					c.createAlias("pedidoColectivo.puntoDeRetiro", "puntoDeRetiro");
					c.add(Restrictions.eq("puntoDeRetiro.nombre", puntoRetiro));
				}
				if (zonaId != null) {
					c.createAlias("pedidoColectivo.zona", "zona");
					c.add(Restrictions.eq("zona.id", zonaId));
				}
				if (queryNodo != null) {
					if (!queryNodo.equals("")) {
						c.createAlias("grupoCC.administrador", "administrador");
						Criterion email = Restrictions.like("administrador.email", "%" + queryNodo + "%");
						Criterion nombre = Restrictions.like("administrador.nombre", "%" + queryNodo + "%");
						Criterion apellido = Restrictions.like("administrador.apellido", "%" + queryNodo + "%");
						Criterion aliasNodo = Restrictions.like("grupoCC.alias", "%" + queryNodo + "%");
						c.add(Restrictions.or(Restrictions.or(Restrictions.or(email, nombre), apellido), aliasNodo));
					}
				}
				return c.list();
			}
		});
	}

	@Override
	public void eliminar(PedidoColectivo p) {
		this.getHibernateTemplate().delete(p);
		this.getHibernateTemplate().flush();

	}

}
