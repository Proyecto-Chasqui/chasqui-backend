package chasqui.service.rest.request;

public class CancelarSolicitudCreacionNodoRequest {	
	private Integer idSolicitud;
	private Integer idVendedor;
	
	public CancelarSolicitudCreacionNodoRequest() {
		
	}
	public Integer getIdSolicitud() {
		return idSolicitud;
	}
	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
	}
	public Integer getIdVendedor() {
		return idVendedor;
	}
	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
}
