package chasqui.model_lite;

import org.joda.time.DateTime;

public class PedidoLite {

	private Integer id;
	private Integer idVendedor;
	private String estado;
	private String nombreVendedor;
	private ClienteLite cliente;
	private Boolean alterable;
	private DateTime fechaCreacion;
	private DateTime fechaDeVencimiento;
	private DateTime fechaModificacion;
	private Double montoMinimo;
	private Double montoActual;
	private Boolean perteneceAPedidoGrupal;
	private String comentario;
	private String tipoDeAjuste;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdVendedor() {
		return this.idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombreVendedor() {
		return this.nombreVendedor;
	}

	public void setNombreVendedor(String nombreVendedor) {
		this.nombreVendedor = nombreVendedor;
	}

	public ClienteLite getCliente() {
		return this.cliente;
	}

	public void setCliente(ClienteLite cliente) {
		this.cliente = cliente;
	}

	public Boolean getAlterable() {
		return this.alterable;
	}

	public void setAlterable(Boolean alterable) {
		this.alterable = alterable;
	}

	public DateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public DateTime getFechaDeVencimiento() {
		return this.fechaDeVencimiento;
	}

	public void setFechaDeVencimiento(DateTime fechaDeVencimiento) {
		this.fechaDeVencimiento = fechaDeVencimiento;
	}

	public DateTime getFechaModificacion() {
		return this.fechaModificacion;
	}

	public void setFechaModificacion(DateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Double getMontoMinimo() {
		return this.montoMinimo;
	}

	public void setMontoMinimo(Double montoMinimo) {
		this.montoMinimo = montoMinimo;
	}

	public Double getMontoActual() {
		return this.montoActual;
	}

	public void setMontoActual(Double montoActual) {
		this.montoActual = montoActual;
	}

	public Boolean getPerteneceAPedidoGrupal() {
		return this.perteneceAPedidoGrupal;
	}

	public void setPerteneceAPedidoGrupal(Boolean perteneceAPedidoGrupal) {
		this.perteneceAPedidoGrupal = perteneceAPedidoGrupal;
	}

	public String getComentario() {
		return this.comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getTipoDeAjuste() {
		return this.tipoDeAjuste;
	}

	public void setTipoDeAjuste(String tipoDeAjuste) {
		this.tipoDeAjuste = tipoDeAjuste;
	}

}
