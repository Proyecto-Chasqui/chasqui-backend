package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.CategoriaDAO;
import chasqui.exceptions.VendedorInexistenteException;
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
	//revisar de utilizar hibernate querys
	@Override
	public Categoria obtenerCategoriaConNombreDe(final String nombre, final Integer idVendedor) throws VendedorInexistenteException {
		return this.getHibernateTemplate().execute(new HibernateCallback<Categoria>() {

			@Override
			public Categoria doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(Categoria.class);
				criteria.add(Restrictions.eq("vendedor.id", idVendedor));
				criteria.add(Restrictions.like("nombre", nombre, MatchMode.EXACT));
				return (Categoria) criteria.uniqueResult();
			}
		});
	}
	

}
