package chasqui.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.access.prepost.PreAuthorize;

import chasqui.dao.MiembroDeGCCDAO;
import chasqui.model.GrupoCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Usuario;

public class MiembroDeGCCDAOHbm extends HibernateDaoSupport implements MiembroDeGCCDAO {

	@Override
	public void actualizarMiembroDeGCC(MiembroDeGCC miembro) {
		this.getHibernateTemplate().saveOrUpdate(miembro);
		this.getHibernateTemplate().flush();
	}

	@Override
	public List<MiembroDeGCC> obtenerMiembrosDeGCCParaCliente(final Integer idCliente) {

		return (List<MiembroDeGCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<MiembroDeGCC>>() {

			@Override
			public List<MiembroDeGCC> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteriaMiembro = session.createCriteria(MiembroDeGCC.class);
				criteriaMiembro.add(Restrictions.eq("idCliente", idCliente));

				return criteriaMiembro.list();
			}

		});

	}

	@Override
	public List<MiembroDeGCC> obtenerMiembrosDeGCCParaClientePorMail(final String emailCliente) {

		return (List<MiembroDeGCC>) this.getHibernateTemplate().execute(new HibernateCallback<List<MiembroDeGCC>>() {

			@Override
			public List<MiembroDeGCC> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteriaMiembro = session.createCriteria(MiembroDeGCC.class);
				criteriaMiembro.add(Restrictions.eq("email", emailCliente));

				return criteriaMiembro.list();
			}

		});

	}

}
