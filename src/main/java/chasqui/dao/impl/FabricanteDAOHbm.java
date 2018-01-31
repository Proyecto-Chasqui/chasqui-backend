package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.FabricanteDAO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Fabricante;

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
	
	

}
