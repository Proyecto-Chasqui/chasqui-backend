package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.PedidoDAO;
import chasqui.dtos.queries.PedidoQueryDTO;
import chasqui.model.Pedido;
import chasqui.model.ProductoPedido;
import chasqui.model.Zona;
import chasqui.model_lite.ClienteLite;
import chasqui.model_lite.GrupoCCLite;
import chasqui.model_lite.PedidoLite;
import chasqui.model_lite.UsuarioLite;
import chasqui.view.composer.Constantes;

public class PedidoDAOHbm extends HibernateDaoSupport implements PedidoDAO {
	public static final Logger logger = Logger.getLogger(PedidoDAOHbm.class);

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
				Criteria criteria = session.createCriteria(Pedido.class, "pedido").createAlias("pedido.direccionEntrega",
						"direccionEntrega");
				criteria.add(Restrictions.eq("pedido.alterable", true)).add(Restrictions.eq("pedido.idVendedor", idVendedor))
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
				Criteria c = session.createCriteria(Pedido.class).add(Restrictions.eq("idVendedor", idVendedor))
						.addOrder(Order.desc("id"));
				return (List<Pedido>) c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoLite> obtenerPedidosLite(final PedidoQueryDTO query) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<PedidoLite>>() {

			final String emailCliente = query.getEmailCliente();
			final Integer idColectivo = query.getIdColectivo();
			final Integer idVendedor = query.getIdVendedor();

			@Override
			public List<PedidoLite> doInHibernate(Session session) throws HibernateException, SQLException {

				String queryStr =  " SELECT  " 
													+" pedido.ID as pedido_id, "
													+" pedido.ID_VENDEDOR as ID_VENDEDOR, "
													+" pedido.CLIENTE as pedido_cliente, "
													+" pedido.ALTERABLE as pedido_alterable, "
													+" pedido.ESTADO as pedido_estado, "
													+" pedido.FECHA_CREACION as pedido_fecha_creacion, "
													+" pedido.FECHA_MODIFICACION as pedido_fecha_modificacion, "
													+" pedido.FECHA_VENCIMIENTO as pedido_fecha_vencimiento, "
													+" pedido.MONTO_MINIMO as pedido_monto_minimo, "
													+" pedido.MONTO_ACTUAL as pedido_monto_actual, "
													+" pedido.NOMBRE_VENDEDOR as pedido_nombre_vendedor, "
													+" pedido.PERTENECE_A_GRUPAL as pedido_pertenece_a_grupal, "
													+" pedido.COMENTARIO as pedido_comentario, "
													+" pedido.TIPO_AJUSTE as pedido_tipo_de_ajuste, "
													+" cliente.ID as cliente_id, "
													+" cliente.NOMBRE as cliente_nombre, "
													+" cliente.APELLIDO as cliente_apellido, "
													+" cliente.TELEFONO_FIJO as cliente_telefono_fijo, "
													+" cliente.TELEFONO_MOVIL as cliente_telefono_movil, "
													+" cliente.ESTADO as cliente_estado, "
													+" usuario.ID as usuario_id, "
													+" usuario.USERNAME as usuario_username, "
													+" usuario.IMAGEN_DE_PERFIL as usuario_imagen_de_perfil, "
													+" usuario.EMAIL as usuario_email, "
													+" usuario.ENABLED as usuario_enabled, "
													+" usuario.ROOT as usuario_root, "
													+" grupo.ID as grupo_id, "
													+" grupo.ALIAS as grupo_alias, "
													+" grupo.DESCRIPCION as grupo_descripcion, "
													+" grupo.PEDIDOS_HABILITADOS as usuario_pedidos_habilitados, "
													+" grupo.FECHA_DE_CREACION as usuario_fechade_creacion, "
													+" grupo.ES_NODO as usuario_es_nodo "
												+ " FROM PEDIDO pedido "
												+ " INNER JOIN CLIENTE cliente ON cliente.ID = pedido.CLIENTE "
												+ " INNER JOIN USUARIO usuario ON usuario.ID = cliente.ID "
												+ " INNER JOIN PEDIDO_COLECTIVO colectivo ON colectivo.ID = pedido.ID_PEDIDO_COLECTIVO  " 
												+ " INNER JOIN GRUPOCC grupo ON grupo.ID = colectivo.COLECTIVO  " 
												+ " WHERE  "
												+ "   pedido.PERTENECE_A_GRUPAL = 1 " 
												+ " AND colectivo.ESTADO = :estadoPedidoColectivo ";
												

				if (emailCliente != null) {
					queryStr += " AND usuario.email = :emailCliente ";
				}
				
				if (idColectivo != null) {
					queryStr += " AND colectivo.COLECTIVO = :idColectivo ";
				}

				if (idVendedor != null) {
					queryStr += " AND pedido.ID_VENDEDOR = :idVendedor ";
				}

				SQLQuery q = session.createSQLQuery(queryStr);

				q.setString("estadoPedidoColectivo", "ABIERTO");
				
				q.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

				if (emailCliente != null) {
					q.setString("emailCliente", emailCliente);
				}

				if(idColectivo != null) {
					q.setInteger("idColectivo", idColectivo);
				}

				if(idVendedor != null) {
					q.setInteger("idVendedor", idVendedor);
				}

				List<PedidoLite> out = new ArrayList<>();
				
				List<HashMap<String, Object>> list = q.list();
				for (HashMap<String, Object> row : list) {
					logger.info(row);

					Integer idVendedor = (Integer) row.get("ID_VENDEDOR");

					UsuarioLite usuario = new UsuarioLite();
					usuario.setId((Integer) row.get("usuario_id"));
					usuario.setUsername((String) row.get("usuario_username"));
					usuario.setImagenPerfil((String) row.get("usuario_imagen_de_perfil"));
					usuario.setEmail((String) row.get("usuario_email"));
					usuario.setEnabled((Boolean) row.get("usuario_enabled"));
					usuario.setRoot((Boolean) row.get("usuario_root"));
					
					ClienteLite cliente = new ClienteLite();
					cliente.setId((Integer) row.get("cliente_id"));
					cliente.setNombre((String) row.get("cliente_nombre"));
					cliente.setApellido((String) row.get("cliente_apellido"));
					cliente.setTelefonoFijo((String) row.get("cliente_telefono_fijo"));
					cliente.setTelefonoMovil((String) row.get("cliente_telefono_movil"));
					cliente.setEstado((String) row.get("cliente_estado"));
					cliente.setEmail((String) row.get("cliente_email"));
					cliente.setEmail(usuario.getEmail());
					cliente.setImagenPerfil(usuario.getImagenPerfil());

					GrupoCCLite grupo = new GrupoCCLite();
					grupo.setId((Integer) row.get("grupo_id"));
					grupo.setAlias((String) row.get("grupo_alias"));
					grupo.setDescripcion((String) row.get("grupo_descripcion"));
					grupo.setPedidosHabilitados((Boolean) row.get("usuario_pedidos_habilitados"));
					grupo.setFechaCreacion( new DateTime(row.get("usuario_fecha_de_creacion")));
					grupo.setEsNodo((Boolean) row.get("usuario_es_nodo"));
					grupo.setIdVendedor(idVendedor);

					PedidoLite pedido = new PedidoLite();
					pedido.setId((Integer) row.get("pedido_id"));
					pedido.setIdVendedor(idVendedor);
					pedido.setAlterable((boolean) row.get("pedido_alterable"));
					pedido.setEstado((String) row.get("pedido_estado"));
					pedido.setFechaCreacion(new DateTime(row.get("pedido_fecha_creacion")));
					pedido.setFechaModificacion( new DateTime(row.get("pedido_fecha_modificacion")));
					pedido.setFechaDeVencimiento( new DateTime(row.get("pedido_fecha_vencimiento")));
					pedido.setMontoMinimo((Double) row.get("pedido_monto_minimo"));
					pedido.setMontoActual((Double) row.get("pedido_monto_actual"));
					pedido.setNombreVendedor((String) row.get("pedido_nombre_vendedor"));
					pedido.setPerteneceAPedidoGrupal((Boolean) row.get("pedido_pertenece_a_grupal"));
					pedido.setComentario((String) row.get("pedido_comentario"));
					pedido.setTipoDeAjuste((String) row.get("pedido_tipo_de_ajuste"));
					pedido.setCliente(cliente);
					pedido.setGrupo(grupo);

					out.add(pedido);
				}

				return out;
			}
		});
	}

	@Override
	public PedidoLite obtenerPedidoLiteActivo (Integer idColectivo, String emailCliente) {
		PedidoQueryDTO query = new PedidoQueryDTO();
		query.setIdColectivo(idColectivo);
		query.setEmailCliente(emailCliente);
		List<PedidoLite> pedidos = this.obtenerPedidosLite(query);
		if(!pedidos.isEmpty()) {
			return pedidos.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosIndividuales(final Integer idVendedor) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class, "pedido")
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false)).addOrder(Order.desc("id"));
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
						.createAlias("pedido.direccionEntrega", "direccionEntrega").add(Restrictions.eq("idVendedor", idVendedor))
						.add(SpatialRestrictions.within("direccionEntrega.geoUbicacion", zona.getGeoArea())).setMaxResults(100)
						.addOrder(Order.desc("id"));
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
						.add(Restrictions.eq("pedido.idVendedor", idVendedor)).setMaxResults(100).addOrder(Order.desc("pedido.id"))
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
						// .add(Restrictions.eq("perteneceAPedidoGrupal", false))// TODO sacar
						.add(Restrictions.eq("idVendedor", idVendedor)).add(Restrictions.lt("fechaDeVencimiento", new DateTime()));
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
	public List<Pedido> obtenerPedidosDeConEstado(final Integer idUsuario, final Integer idVendedor,
			final List<String> estados) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class, "pedido");

				criteria.add(Restrictions.eq("pedido.cliente.id", idUsuario))
						.add(Restrictions.eq("pedido.idVendedor", idVendedor)).add(Restrictions.in("estado", estados));
				return (List<Pedido>) criteria.list();
			}
		});
	}

	@Override
	public List<Pedido> obtenerPedidosIndividualesDeVendedor(final Integer idVendedor) {

		return this.getHibernateTemplate().execute(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Pedido.class);
				criteria.add(Restrictions.eq("perteneceAPedidoGrupal", false)).add(Restrictions.eq("idVendedor", idVendedor))
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
				Criteria c = session.createCriteria(Pedido.class, "pedido")
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false)).addOrder(Order.desc("pedido.id"));

				if (zonaId != null) {
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
				} else {
					if (desde != null) {
						DateTime d = new DateTime(desde.getTime());
						c.add(Restrictions.ge("pedido.fechaCreacion", d.withHourOfDay(0)));
					} else {
						if (hasta != null) {
							DateTime h = new DateTime(hasta.getTime());
							c.add(Restrictions.le("pedido.fechaCreacion", h.plusDays(1).withHourOfDay(0)));
						}
					}
				}

				if (idPuntoRetiro != null) {
					c.add(Restrictions.eq("puntoDeRetiro.id", idPuntoRetiro));
				}

				if (email != null) {
					c.createAlias("pedido.cliente", "cliente");
					c.add(Restrictions.like("cliente.email", "%" + email + "%"));
				}

				return (List<Pedido>) c.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pedido> obtenerPedidosIndividualesDeVendedorPRPorNombre(final Integer idVendedor, final Date desde,
			final Date hasta, final String estadoSeleccionado, final Integer zonaId, final String nombrePuntoRetiro,
			final String email) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Pedido>>() {

			@Override
			public List<Pedido> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Pedido.class, "pedido")
						.add(Restrictions.eq("pedido.idVendedor", idVendedor))
						.add(Restrictions.eq("pedido.perteneceAPedidoGrupal", false)).addOrder(Order.desc("pedido.id"));

				if (zonaId != null) {
					c.createAlias("pedido.zona", "zona");
					c.add(Restrictions.eq("zona.id", zonaId));
				}

				if (!StringUtils.isEmpty(estadoSeleccionado)) {
					c.add(Restrictions.eq("pedido.estado", estadoSeleccionado));
				}
				if (desde != null && hasta != null) {
					DateTime d = new DateTime(desde.getTime());
					DateTime h = new DateTime(hasta.getTime());
					c.add(Restrictions.between("pedido.fechaModificacion", d.withHourOfDay(0), h.plusDays(1).withHourOfDay(0)));
				} else {
					if (desde != null) {
						DateTime d = new DateTime(desde.getTime());
						c.add(Restrictions.ge("pedido.fechaModificacion", d.withHourOfDay(0)));
					} else {
						if (hasta != null) {
							DateTime h = new DateTime(hasta.getTime());
							c.add(Restrictions.le("pedido.fechaModificacion", h.plusDays(1).withHourOfDay(0)));
						}
					}
				}

				if (nombrePuntoRetiro != null && !nombrePuntoRetiro.equals("")) {
					c.createAlias("pedido.puntoDeRetiro", "puntoDeRetiro");
					c.add(Restrictions.eq("puntoDeRetiro.nombre", nombrePuntoRetiro));
				}

				if (email != null && !email.equals("")) {
					c.createAlias("pedido.cliente", "cliente");
					c.add(Restrictions.like("cliente.email", "%" + email + "%"));
				}

				return (List<Pedido>) c.list();
			}
		});
	}

	@Override
	public void eliminar(Pedido p) {
		this.getHibernateTemplate().delete(p);
		this.getHibernateTemplate().flush();
	}

	@Override
	public void eliminarProductosPedidos(List<ProductoPedido> productoPedido) {
		for (ProductoPedido pp : productoPedido) {
			this.getHibernateTemplate().delete(pp);
		}
		this.getHibernateTemplate().flush();
	}

}
