package chasqui.model_lite;

import org.joda.time.DateTime;

public class GrupoCCLite {

	private Integer id;
	private String alias;
	private String descripcion;
	private DateTime fechaCreacion;
	private Boolean esNodo = false;
	private Boolean pedidosHabilitados;
	private Integer idVendedor;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public DateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Boolean getEsNodo() {
		return this.esNodo;
	}

	public void setEsNodo(boolean esNodo) {
		this.esNodo = esNodo;
	}

	public Boolean getPedidosHabilitados() {
		return pedidosHabilitados;
	}

	public void setPedidosHabilitados(Boolean pedidosHabilitados) {
		this.pedidosHabilitados = pedidosHabilitados;
	}

	public Integer getIdVendedor() {
		return this.idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

}