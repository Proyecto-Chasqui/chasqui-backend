package chasqui.dao.impl;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import chasqui.dao.SolicitudCreacionNodoDAO;
import chasqui.model.SolicitudCreacionNodo;

public class SolicitudCreacionNodoDAOHbm  extends HibernateDaoSupport implements SolicitudCreacionNodoDAO{

	@Override
	public void guardar(SolicitudCreacionNodo solicitud) {
		this.getSession().saveOrUpdate(solicitud);
		this.getSession().flush();		
	}

}
