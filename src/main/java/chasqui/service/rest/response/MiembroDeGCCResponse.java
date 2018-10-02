package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.MiembroDeGCC;
import chasqui.model.Pedido;

public class MiembroDeGCCResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6612590818300050568L;

	private String avatar;// TODO mockear
	
	private String nickname; // TODO ver si ponemos nombre y apellido

	private String email;

	private String invitacion; // TODO estado pendiente o aceptado
	
	private String estadoPedido; // TODO estado pendiente o aceptado
	
	private PedidoResponse pedido;


	public MiembroDeGCCResponse(MiembroDeGCC miembro, Pedido pedido) {
		super();
		this.nickname = miembro.getNickname();
		this.email = miembro.getEmail();
		this.invitacion = miembro.getEstadoInvitacion();
		this.estadoPedido = miembro.getEstadoPedido();
		this.avatar = miembro.getAvatar();
		this.pedido = (pedido == null)? null: new PedidoResponse(pedido);
	}
	


	public PedidoResponse getPedido() {
		return pedido;
	}



	public void setPedido(PedidoResponse pedido) {
		this.pedido = pedido;
	}



	public String getEstadoPedido() {
		return estadoPedido;
	}


	public void setEstadoPedido(String estadoPedido) {
		this.estadoPedido = estadoPedido;
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

	public String getInvitacion() {
		return invitacion;
	}

	public void setInvitacion(String invitacion) {
		this.invitacion = invitacion;
	}

}
