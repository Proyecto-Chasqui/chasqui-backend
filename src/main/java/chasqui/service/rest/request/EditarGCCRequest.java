package chasqui.service.rest.request;

import java.io.Serializable;

public class EditarGCCRequest implements Serializable {

	private static final long serialVersionUID = 8861515048341865794L;

	private String alias;
	private String descripcion;

	public EditarGCCRequest() {
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
