package chasqui.dao.impl;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.SolicitudPertenenciaNodoDAO;
import chasqui.model.SolicitudPertenenciaNodo;

public class SolicitudPertenenciaNodoDAOHbm  extends HibernateDaoSupport implements SolicitudPertenenciaNodoDAO{

	@Override
	public void guardar(SolicitudPertenenciaNodo solicitud) {
		this.getSession().saveOrUpdate(solicitud);
		this.getSession().flush();
		
		
	}

}
