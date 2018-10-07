package chasqui.service.rest.request;

public class EliminarZonaRequest {
	private Integer id;
	private Integer idVendedor;
	private String token;
	
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
