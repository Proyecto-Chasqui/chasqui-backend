package chasqui.service.rest.request;

public class EliminarGrupoRequest {
	
	private Integer idGrupo;
	private Integer idVendedor;
	
	public EliminarGrupoRequest() {

	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	
}
