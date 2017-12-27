package chasqui.service.rest.response;

import java.io.Serializable;

public class DireccionProductorResponse implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -192126565902969430L;
	private String pais;
	private String provincia;
	private String localidad;
	private String calle;
	private Integer altura;
	
	
	public DireccionProductorResponse(){}
	
	
	
	
	public DireccionProductorResponse(String calle2, Integer altura2, String pais2, String localidad2,
			String provincia2) {
		pais = pais2;
		altura=altura2;
		calle=calle2;
		localidad=localidad2;
		provincia=provincia2;
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
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
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

	
	
	
	
	
	
	
	
}
