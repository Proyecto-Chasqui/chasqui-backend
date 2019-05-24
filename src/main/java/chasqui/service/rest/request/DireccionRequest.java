package chasqui.service.rest.request;

import java.io.Serializable;


public class DireccionRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4033188450893024938L;
	private String calle;
	private String calleAdyacente1;
	private String calleAdyacente2;
	private Integer altura;
	private String localidad;
	private String departamento;
	private String alias;
	private String codigoPostal;
	private String latitud;
	private String longitud;
	private Boolean predeterminada;
	private String comentario;
	private String pais;
	private String provincia;
	
	
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public Integer getAltura() {
		return altura;
	}
	public void setAltura(Integer altura) {
		this.altura = altura;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	public String getLatitud() {
		return latitud;
	}
	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}
	public String getLongitud() {
		return longitud;
	}
	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Boolean getPredeterminada() {
		return predeterminada;
	}
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}
	
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public String getCalleAdyacente1() {
		return calleAdyacente1;
	}
	public void setCalleAdyacente1(String calleAdyacente1) {
		this.calleAdyacente1 = calleAdyacente1;
	}
	public String getCalleAdyacente2() {
		return calleAdyacente2;
	}
	public void setCalleAdyacente2(String calleAdyacente2) {
		this.calleAdyacente2 = calleAdyacente2;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	
}
