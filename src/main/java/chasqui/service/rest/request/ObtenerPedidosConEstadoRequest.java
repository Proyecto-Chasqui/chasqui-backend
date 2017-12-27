package chasqui.service.rest.request;

import java.util.List;

public class ObtenerPedidosConEstadoRequest {

	private Integer idVendedor;
	private List<String> estados;

	public ObtenerPedidosConEstadoRequest() {
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}


	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}

}
