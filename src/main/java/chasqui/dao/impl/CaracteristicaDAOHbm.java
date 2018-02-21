package chasqui.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.CaracteristicaDAO;
import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.services.interfaces.ICaracteristica;

public class CaracteristicaDAOHbm extends HibernateDaoSupport implements CaracteristicaDAO{

	public void guardaCaracteristicasProducto(List<Caracteristica> list) {
		for(Caracteristica c : list){
			this.getHibernateTemplate().saveOrUpdate(c);
		}
		this.getHibernateTemplate().flush();
	}

	public void guardarCaracteristicaProductor(List<CaracteristicaProductor> list) {
		for(CaracteristicaProductor c : list){
			this.getHibernateTemplate().saveOrUpdate(c);
		}
		this.getHibernateTemplate().flush();
		
	}

	@SuppressWarnings("unchecked")
	public List<Caracteristica> buscarCaracteristicasProducto() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Caracteristica.class);
		criteria.add(Restrictions.eq("eliminada",false));
		criteria.addOrder(Order.asc("nombre"));
		List<Caracteristica> resultado = this.getHibernateTemplate().findByCriteria(criteria);
		if(resultado == null){
			resultado = new ArrayList<Caracteristica>();
		}
		return resultado;
		
	}

	@SuppressWarnings("unchecked")
	public List<CaracteristicaProductor> buscarCaracteristicasProductor() {
		DetachedCriteria criteria = DetachedCriteria.forClass(CaracteristicaProductor.class);
		criteria.add(Restrictions.eq("eliminada",false));
		criteria.addOrder(Order.asc("nombre"));
		List<CaracteristicaProductor> resultado = this.getHibernateTemplate().findByCriteria(criteria);
		if(resultado == null){
			resultado = new ArrayList<CaracteristicaProductor>();
		}
		return resultado;
	}

	public void eliminarCaracteristica(Caracteristica c) {
		this.getHibernateTemplate().delete(c);
		this.getHibernateTemplate().flush();
		
	}

	public void eliminarCaracteristicaProductor(CaracteristicaProductor c) {
		this.getHibernateTemplate().delete(c);
		this.getHibernateTemplate().flush();
		
	}

	
	public void actualizarCaracteristicaProductor(ICaracteristica c) {
		this.getHibernateTemplate().saveOrUpdate(c);
		this.getHibernateTemplate().flush();
		
	}

	@Override
	public void actualizar(ICaracteristica c) {
		this.getHibernateTemplate().saveOrUpdate(c);
		this.getHibernateTemplate().flush();
		
	}

	@Override
	public boolean existeCaracteristicaProductorConNombre(String nombre) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CaracteristicaProductor.class);
		criteria.add(Restrictions.eq("eliminada",false));
		criteria.add(Restrictions.eq("nombre",nombre));
		List<CaracteristicaProductor> resultado = this.getHibernateTemplate().findByCriteria(criteria);
		if(resultado == null){
			resultado = new ArrayList<CaracteristicaProductor>();
		}
		return resultado.size() >= 1;
	}

	@Override
	public boolean existeCaracteristicaProductoConNombre(String nombre) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Caracteristica.class);
		criteria.add(Restrictions.eq("eliminada",false));
		criteria.add(Restrictions.eq("nombre",nombre));
		List<Caracteristica> resultado = this.getHibernateTemplate().findByCriteria(criteria);
		if(resultado == null){
			resultado = new ArrayList<Caracteristica>();
		}
		return resultado.size() >= 1;
	}

	   
	
}
