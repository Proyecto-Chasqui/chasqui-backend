package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.vividsolutions.jts.geom.Geometry;

import chasqui.dao.UsuarioDAO;
import chasqui.model.Categoria;
import chasqui.model.Cliente;
import chasqui.model.Fabricante;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.model.Producto;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.view.composer.Constantes;

@SuppressWarnings("unchecked")
public class UsuarioDAOHbm extends HibernateDaoSupport implements UsuarioDAO {

	public Usuario obtenerUsuarioPorID(final Integer id) {
		Usuario u = this.getHibernateTemplate().execute(new HibernateCallback<Usuario>() {

			public Usuario doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Usuario.class);
				criteria.add(Restrictions.eq("id", id));
				return (Usuario) criteria.uniqueResult();
			}

		});
		return u;
	}

	public Vendedor obtenerVendedorPorID(final Integer id) {
		Vendedor v = this.getHibernateTemplate().execute(new HibernateCallback<Vendedor>() {

			public Vendedor doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Vendedor.class);
				criteria.add(Restrictions.eq("id", id));
				return (Vendedor) criteria.uniqueResult();
			}

		});
		return v;
	}

	public void guardarUsuario(Usuario u) {
		this.getHibernateTemplate().saveOrUpdate(u);
		this.getHibernateTemplate().flush();
	}

	public Usuario obtenerUsuarioPorNombre(final String username) {
		Usuario u = this.getHibernateTemplate().execute(new HibernateCallback<Usuario>() {

			public Usuario doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Usuario.class);
				criteria.add(Restrictions.eq("username", username));
				return (Usuario) criteria.uniqueResult();
			}

		});
		return u;
	}

	public void inicializarListasDe(Vendedor vendedor) {
		HibernateTemplate ht = this.getHibernateTemplate();
		ht.refresh(vendedor);
		ht.initialize(vendedor.getFabricantes());
		ht.initialize(vendedor.getCategorias());
		ht.initialize(vendedor.getZonas());
		for (Categoria c : vendedor.getCategorias()) {
			ht.initialize(c.getProductos());
			for (Producto p : c.getProductos()) {
				ht.initialize(p.getVariantes());
			}
		}
		for (Fabricante f : vendedor.getFabricantes()) {
			ht.initialize(f.getProductos());
		}

	}

	public void merge(Vendedor usuario) {
		this.getHibernateTemplate().merge(usuario);
		this.getHibernateTemplate().flush();

	}

	public Usuario obtenerUsuarioPorEmail(final String email) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Usuario>() {

			@Override
			public Usuario doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria d = session.createCriteria(Usuario.class);
				d.add(Restrictions.eq("email", email));
				return (Usuario) d.uniqueResult();
			}
		});
	}


	public boolean existeUsuarioCon(String email) {
		DetachedCriteria d = DetachedCriteria.forClass(Usuario.class);
		d.add(Restrictions.eq("email", email));
		List<Usuario> u = (List<Usuario>) this.getHibernateTemplate().findByCriteria(d);
		return (u == null || !u.isEmpty());
	}

	
	//-------------Mara
	@Override
	public Cliente inicializarDirecciones(Cliente cliente){

		HibernateTemplate ht = this.getHibernateTemplate();
		ht.refresh(cliente);
		if (cliente != null) {
			ht.initialize(cliente.getDireccionesAlternativas());
		}
		return cliente;
	}
	
	@Override
	public Cliente inicializarPedidos(Cliente cliente){

		HibernateTemplate ht = this.getHibernateTemplate();
		ht.refresh(cliente);

		if (cliente != null) {
			ht.initialize(cliente.getPedidos());
		}
		return cliente;
	}
	
	@Override
	public Cliente inicializarHistorial(Cliente cliente) {

		HibernateTemplate ht = this.getHibernateTemplate();
		ht.refresh(cliente);

		if (cliente != null) {
			if (cliente.getHistorialPedidos() != null) {
				ht.initialize(cliente.getHistorialPedidos().getPedidos());
			}
		}
		return cliente;
	}
	@Override
	public Cliente inicializarColecciones(Cliente cliente) {

		HibernateTemplate ht = this.getHibernateTemplate();
		ht.refresh(cliente);
		ht.initialize(cliente);

		if (cliente != null) {
			ht.initialize(cliente.getDireccionesAlternativas());
			ht.initialize(cliente.getPedidos());
			if (cliente.getHistorialPedidos() != null) {
				ht.initialize(cliente.getHistorialPedidos().getPedidos());
			}
		}
		return cliente;
	}
	//------------ End Mara
	
	@Override
	public List<Cliente> obtenerClientesCercanos(final Geometry area, final String filterEmail) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Cliente>>() {
			
			@Override
			public List<Cliente> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Cliente.class, "cliente")
				.createAlias("cliente.direccionesAlternativas", "direccionesAlternativas");
				criteria.add(Restrictions.eq("direccionesAlternativas.predeterminada",true))
						.add(SpatialRestrictions.within("direccionesAlternativas.geoUbicacion", area))
						.add(Restrictions.not(Restrictions.eq("cliente.email",filterEmail)));
				return (List<Cliente>) criteria.list();
			}
		});	
	}
	
	
	@Override
	public void eliminarUsuario(Vendedor u) {
		this.getHibernateTemplate().delete(u);
		this.getHibernateTemplate().flush();
	}

	@Override
	public List<Notificacion> obtenerNotificacionesDe(final String mail, final Integer pagina) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Notificacion>>() {

			@Override
			public List<Notificacion> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Notificacion.class);
				Integer inicio = calcularInicio(pagina, 5);
				criteria.setFirstResult(inicio).setMaxResults(5).add(Restrictions.eq("usuarioDestino", mail))
						.addOrder(Order.desc("id"));
				return criteria.list();
			}
		});
	}

	private Integer calcularInicio(Integer pagina, Integer cantidadDeItems) {
		if (pagina == 1) {
			return 0;
		}
		return (pagina - 1) * cantidadDeItems;
	}

	@Override
	public List<Notificacion> obtenerNotificacionNoLeidas(final String mail) {
		return this.getHibernateTemplate().executeFind(new HibernateCallback<List<Notificacion>>() {

			@Override
			public List<Notificacion> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Notificacion.class);
				criteria.add(Restrictions.eq("usuarioDestino", mail)).add(Restrictions.eq("estado", Constantes.ESTADO_NOTIFICACION_NO_LEIDA))
						.addOrder(Order.desc("id"));
				return criteria.list();
			}
		});
	}

	@Override
	public Integer obtenerTotalNotificacionesDe(final String mail) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Integer>() {

			@Override
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Notificacion.class);
				criteria.add(Restrictions.eq("usuarioDestino", mail)).addOrder(Order.desc("id"))
						.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).intValue();
			}
		});
	}

	@Override
	public Notificacion obtenerNotificacion(final Integer id) {
		return this.getHibernateTemplate().execute(new HibernateCallback<Notificacion>() {

			@Override
			public Notificacion doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Notificacion.class);
				criteria.add(Restrictions.eq("id", id));
				return (Notificacion) criteria.uniqueResult();
			}
		});
	}

	@Override
	public void guardar(Notificacion n) {
		this.getHibernateTemplate().saveOrUpdate(n);
		this.getHibernateTemplate().flush();

	}

	/**
	 * ESTE METODO SOLO DEBE USARSE PARA LOS TEST
	 */
	@Override
	public <T> void deleteObject(T obj) {
		this.getHibernateTemplate().execute(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				Query deleteProductoPedido = session.createSQLQuery("DELETE FROM PRODUCTO_PEDIDO");
				Query deletePedido = session.createSQLQuery("DELETE FROM PEDIDO");
				deleteProductoPedido.executeUpdate();
				deletePedido.executeUpdate();
				return null;
			}
		});
		this.getHibernateTemplate().delete(obj);
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();

	}

	@Override
	public Usuario obtenerUsuarioRoot() {
		Usuario u = this.getHibernateTemplate().execute(new HibernateCallback<Usuario>() {

			public Usuario doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Usuario.class);
				criteria.add(Restrictions.eq("username", "root"));
				return (Usuario) criteria.uniqueResult();
			}

		});
		return u;
	}


}
