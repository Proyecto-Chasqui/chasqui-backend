package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.FabricanteDAO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Fabricante;
import chasqui.model.Variante;

@SuppressWarnings("unchecked")
public class FabricanteDAOHbm extends HibernateDaoSupport implements FabricanteDAO{

	@Override
	public List<Fabricante> obtenerProductoresDe(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Fabricante>>() {

			@Override
			public List<Fabricante> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "SELECT * FROM PRODUCTOR WHERE ID_VENDEDOR = :vendedor ORDER BY nombre";
				Query hql = session.createSQLQuery(sql)
								   .addEntity(Fabricante.class)
								   .setInteger("vendedor", idVendedor);
				List<Fabricante> resultado = (List<Fabricante>) hql.list();
				
				return resultado; 
			}
		});
	}

	@Override
	public List<Fabricante> obtenerProductoresDeConNombre(final Integer idVendedor, final String nombreProductor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Fabricante>>() {

			@Override
			public List<Fabricante> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(Fabricante.class, "fabricante");
				c.add(Restrictions.eq("fabricante.idVendedor",idVendedor));
				c.add(Restrictions.like("fabricante.nombre", "%"+nombreProductor+"%"));
				return (List<Fabricante>) c.list();
			}
		});
	}
	
	

}
