package chasqui.dao;

import java.util.List;

import chasqui.model.InvitacionAGCC;
import chasqui.model.Notificacion;

public interface NotificacionDAO {

	
	public void guardar(Notificacion n);

	List<Notificacion> obtenerNotificacionesPara(String emailCliente, String estado);

	public InvitacionAGCC obtenerNotificacionPorID(Integer idInvitacion);

	public void eliminar(Notificacion invitacion);

	public List<InvitacionAGCC> obtenerInvitacionPendientePorIDdeGrupo(String emailCliente,	Integer idGrupo);
}
