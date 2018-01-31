package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.CategoriaDAO;
import chasqui.model.Categoria;

@SuppressWarnings("unchecked")
public class CategoriaDAOHbm extends HibernateDaoSupport implements CategoriaDAO{

	
	public List<Categoria> obtenerCategoriasDe(final Integer idVendedor) {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<Categoria>>() {

			@Override
			public List<Categoria> doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "SELECT * FROM CATEGORIA WHERE ID_VENDEDOR = :vendedor ORDER BY nombre";
				Query hql = session.createSQLQuery(sql)
								   .addEntity(Categoria.class)
								   .setInteger("vendedor", idVendedor);
				List<Categoria> resultado = (List<Categoria>) hql.list();
				
				return resultado; 
			}
		});
	}
	

}
