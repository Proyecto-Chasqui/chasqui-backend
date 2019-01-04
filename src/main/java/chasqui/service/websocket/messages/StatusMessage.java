package chasqui.service.websocket.messages;

public class StatusMessage {
	
	private Integer idUsuario;
	private String tipo;
	private Integer idPedido;
	private String accion;
	private Integer idVendedor;
	
	public StatusMessage (Integer UID, String tipo, Integer idPedido, String action, Integer idVendedor) {
		setIdUsuario(UID);
		setTipo(tipo);
		setIdPedido(idPedido);
		setAccion(action);
		setIdVendedor(idVendedor);
	}


	public Integer getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

}
