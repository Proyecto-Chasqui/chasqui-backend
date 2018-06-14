package chasqui.service.rest.request;

public class EliminarZonaRequest {
	private Integer id;
	private Integer idVendedor;
	public EliminarZonaRequest() {	
	}
	
	public EliminarZonaRequest(Integer id) {	
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
}
