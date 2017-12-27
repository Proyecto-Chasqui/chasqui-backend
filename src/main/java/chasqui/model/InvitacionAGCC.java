package chasqui.model;

import chasqui.view.composer.Constantes;

public class InvitacionAGCC extends Notificacion{



	private Integer id;
	private Integer idGrupo;
	
//	public InvitacionAGCC(String userOrigen, String userDestino, String msj, Integer idGrupo) {
//		super(userOrigen, userDestino, msj, Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
//		this.idGrupo = idGrupo;
//	}
//	public void InvitacionAGG(){
//		
//	}

	public InvitacionAGCC() {
		super();
	}

	public InvitacionAGCC(String userOrigen, String userDestino, String msj, Integer idGrupo) {
		super(userOrigen, userDestino, msj, Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
		this.idGrupo = idGrupo;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	
}
