package chasqui.model;

import chasqui.view.composer.Constantes;

public class MiembroDeGCC {

	private Integer id;

	private String avatar;// TODO mockear

	private String nickname; // TODO ver si ponemos nombre y apellido

	private String email;

	private String estadoInvitacion; 

	private String estadoPedido; 

	private Integer idPedido; //TODO borrar

	private Integer idCliente; 

	public MiembroDeGCC() {
	};

	/*
	 * Este constructor debe usarse cuando se invita un un cliente que no está
	 * registrado en Chasqui
	 */
	public MiembroDeGCC(String emailCliente) {
		this.email = emailCliente;
		this.estadoInvitacion = Constantes.ESTADO_NOTIFICACION_NO_LEIDA;
		this.estadoPedido = Constantes.ESTADO_PEDIDO_INEXISTENTE;
	};


	/*
	 * Este constructor debe usarse cuando se invita un un cliente que SI está
	 * registrado en Chasqui
	 */
	public MiembroDeGCC(Cliente cte) {
		this(cte.getEmail());
		this.nickname=cte.getUsername();
		this.idCliente=cte.getId();
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEstadoInvitacion() {
		return estadoInvitacion;
	}

	public void setEstadoInvitacion(String invitacion) {
		this.estadoInvitacion = invitacion;
	}

	public String getEstadoPedido() {
		return estadoPedido;
	}

	public void setEstadoPedido(String estadoPedido) {
		this.estadoPedido = estadoPedido;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}


	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public void aceptarInvitacion(Integer id){
		this.estadoInvitacion = Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA;
		this.setIdCliente(id);
	}
	
	public void rechazarInvitacion(){
		this.estadoInvitacion = Constantes.ESTADO_NOTIFICACION_LEIDA_RECHAZADA;
	}

	public boolean tieneInvitacionRechazada() {
		return this.estadoInvitacion.equals(Constantes.ESTADO_NOTIFICACION_LEIDA_RECHAZADA);
	}
	
	public void abrirPedido(){
		this.setEstadoPedido(Constantes.ESTADO_PEDIDO_ABIERTO);
	}

	public boolean tieneInvitacionPendiente() {
		return this.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
	}
}
