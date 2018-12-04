package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Direccion;

public class DireccionResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2706752008400147353L;
	private String calle;
	private String calleAdyacente1;
	private String calleAdyacente2;
	private Integer altura;
	private String localidad;
	private String codigoPostal;
	private String latitud;
	private String longitud;
	private Boolean predeterminada;
	private String departamento;
	private String alias;
	private Integer idDireccion;
	private String comentario;
	
	
	
	public DireccionResponse(){}
	public DireccionResponse(Direccion d){
		this.altura = d.getAltura();
		this.calle = d.getCalle();
		this.localidad = d.getLocalidad();
		this.codigoPostal = d.getCodigoPostal();
		this.latitud = d.getLatitud();
		this.longitud = d.getLongitud();
		this.alias = d.getAlias();
		this.calleAdyacente1 = d.getCalleAdyacente1();
		this.calleAdyacente2 = d.getCalleAdyacente2();
		this.comentario = d.getComentario();
		this.departamento = d.getDepartamento();
		this.predeterminada = d.getPredeterminada();
		this.idDireccion = d.getId();
	}
	
	public DireccionResponse direccionResponseNoID(Direccion d){
		DireccionResponse dr = new DireccionResponse(d);
		dr.setIdDireccion(null);
		return dr;		
	}
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
	public Boolean getPredeterminada() {
		return predeterminada;
	}
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
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
	public Integer getIdDireccion() {
		return idDireccion;
	}
	public void setIdDireccion(Integer idDireccion) {
		this.idDireccion = idDireccion;
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
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	
	
	

	
	
	
	
}
