package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.TagDAO;
import chasqui.model.Fabricante;
import chasqui.model.Tag;
import chasqui.model.TagEvento;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;

public class TagDAOHbm extends HibernateDaoSupport implements TagDAO{

	@Override
	public void guardar(Tag tag) {
		this.getHibernateTemplate().saveOrUpdate(tag);
		this.getHibernateTemplate().flush();
		
	}

	@Override
	public void guardarTagTipoProducto(TagTipoProducto tag) {
		this.getHibernateTemplate().saveOrUpdate(tag);
		this.getHibernateTemplate().flush();		
	}

	@Override
	public void guardarTagZonaDeCobertura(TagZonaDeCobertura tag) {
		this.getHibernateTemplate().saveOrUpdate(tag);
		this.getHibernateTemplate().flush();
	}

	@Override
	public void guardarTagTipoOrganizacion(TagTipoOrganizacion tag) {
		this.getHibernateTemplate().saveOrUpdate(tag);
		this.getHibernateTemplate().flush();
	}

	@Override
	public void guardarTagEvento(TagEvento tag) {
		this.getHibernateTemplate().saveOrUpdate(tag);
		this.getHibernateTemplate().flush();
	}

	@Override
	public List<TagTipoProducto> obtenerTagsTipoProducto() {
		return this.getHibernateTemplate().execute(new HibernateCallback<List<TagTipoProducto>>() {

			@Override
			public List<TagTipoProducto> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TagTipoProducto.class);
				return (List<TagTipoProducto>) c.list();
			}
		});
	}

	@Override
	public List<TagZonaDeCobertura> obtenerTagZonaDeCobertura() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TagTipoOrganizacion> obtenerTagTipoOrganizacion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TagEvento> obtenerTagsTagEvento() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eliminar(Tag tag) {
		this.getHibernateTemplate().delete(tag);
		this.getHibernateTemplate().flush();		
	}

}
