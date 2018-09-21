package chasqui.service.rest.request;

import java.util.List;

public class ObtenerPedidosColectivosConEstadoRequest {
	private List<String> estados;
	private Integer idGrupo;

	public ObtenerPedidosColectivosConEstadoRequest() {
		
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}
}
