package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.PedidoColectivoDAO;
import chasqui.model.GrupoCC;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.Usuario;

public class PedidoColectivoDAOHbm extends HibernateDaoSupport implements PedidoColectivoDAO{

	@Override
	public void guardar(PedidoColectivo p) {
		this.getHibernateTemplate().saveOrUpdate(p);
		this.getHibernateTemplate().flush();
	}
	@Override
	public PedidoColectivo obtenerPedidoColectivoPorID(final Integer id){
		return this.getHibernateTemplate().execute(new HibernateCallback<PedidoColectivo>() {

			public PedidoColectivo doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PedidoColectivo.class);
				criteria.add(Restrictions.eq("id", id));
				return (PedidoColectivo) criteria.uniqueResult();
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
				Criteria c = session.createCriteria(GrupoCC.class, "grupo")
				.add(Restrictions.eq("grupo.id", grupoID));
				
				GrupoCC grupo = (GrupoCC) c.uniqueResult();
				List<PedidoColectivo> pedidos = grupo.getHistorial().getPedidosGrupales();
				List<PedidoColectivo> pedidosRet = new ArrayList<PedidoColectivo>();
				if(!pedidos.isEmpty()){
					Criteria pedidosColectivos = session.createCriteria(PedidoColectivo.class,"pedidoColectivo")
							.addOrder(Order.desc("pedidoColectivo.id"));
					if(zonaId!=null){
						pedidosColectivos.createAlias("pedidoColectivo.zona", "zona");
						pedidosColectivos.add(Restrictions.eq("zona.id", zonaId));
					}
					Disjunction r = Restrictions.disjunction();
					for(PedidoColectivo pedido :pedidos){
						r.add(Restrictions.eq("pedidoColectivo.id", pedido.getId()));
					}
					pedidosColectivos.add(r);				
					if (!StringUtils.isEmpty(estadoSeleccionado)) {
						pedidosColectivos.add(Restrictions.eq("pedidoColectivo.estado", estadoSeleccionado));
					}
					if (d != null && h != null) {
						DateTime desde = new DateTime(d.getTime());
						DateTime hasta = new DateTime(h.getTime());
						pedidosColectivos.add(Restrictions.between("pedidoColectivo.fechaCreacion", desde.withHourOfDay(0), hasta.plusDays(1).withHourOfDay(0)));
					}else{
						if(d!=null){
							DateTime desde = new DateTime(d.getTime());
							pedidosColectivos.add(Restrictions.ge("pedidoColectivo.fechaCreacion", desde.withHourOfDay(0)));
						}else{
							if(h!=null){
								DateTime hasta = new DateTime(h.getTime());
								pedidosColectivos.add(Restrictions.le("pedidoColectivo.fechaCreacion", hasta.plusDays(1).withHourOfDay(0)));
							}
						}
					}
					if(idPuntoRetiro!=null) {
						pedidosColectivos.add(Restrictions.eq("puntoDeRetiro.id",idPuntoRetiro));
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
				Criteria c = session.createCriteria(GrupoCC.class, "grupo")
				.add(Restrictions.eq("grupo.id", grupoid));
				
				GrupoCC grupo = (GrupoCC) c.uniqueResult();
				List<PedidoColectivo> pedidoRet = new ArrayList<PedidoColectivo>();
				List<PedidoColectivo> pedidos = grupo.getHistorial().getPedidosGrupales();
				if(!pedidos.isEmpty()){
					Criteria pedidosColectivos = session.createCriteria(PedidoColectivo.class,"pedidoColectivo")
							.addOrder(Order.desc("pedidoColectivo.id"));
				
					Disjunction r = Restrictions.disjunction();
					for(PedidoColectivo pedido :pedidos){
						r.add(Restrictions.eq("pedidoColectivo.id", pedido.getId()));
					}
				
					pedidosColectivos.add(r);
				
					pedidoRet = (List<PedidoColectivo>) pedidosColectivos.list();
				}
				return pedidoRet;
			}
		});
	}

}
