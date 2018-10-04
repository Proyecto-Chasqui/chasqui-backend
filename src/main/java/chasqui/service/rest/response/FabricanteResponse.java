package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.Fabricante;

public class FabricanteResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -938795357704648003L;
	
	private Integer idProductor;
	private String nombreProductor;
	private String pathImagen;
	private String descripcionCorta;
	private String descripcionLarga;
	private DireccionProductorResponse direccion;
	private CaracteristicaResponse medalla;
	
	
	public FabricanteResponse(){}
	public FabricanteResponse(Fabricante f) {
		idProductor = f.getId();
		nombreProductor = f.getNombre();
		pathImagen = f.getPathImagen();
		descripcionCorta = f.getDescripcionCorta();
		descripcionLarga = f.getDescripcionLarga();
		if(f.getCaracteristica() != null){
			medalla = new CaracteristicaResponse(f.getCaracteristica());			
		}
		direccion = new DireccionProductorResponse(f.getCalle(),f.getAltura(),f.getPais(),f.getLocalidad(),f.getProvincia());
	}
	
	public Integer getIdProductor() {
		return idProductor;
	}
	public void setIdProductor(Integer idProductor) {
		this.idProductor = idProductor;
	}
	public String getNombreProductor() {
		return nombreProductor;
	}
	public void setNombreProductor(String nombreProductor) {
		this.nombreProductor = nombreProductor;
	}
	public String getPathImagen() {
		return pathImagen;
	}
	public void setPathImagen(String pathImagen) {
		this.pathImagen = pathImagen;
	}
	public DireccionProductorResponse getDireccion() {
		return direccion;
	}
	public void setDireccion(DireccionProductorResponse direccion) {
		this.direccion = direccion;
	}
	public CaracteristicaResponse getMedalla() {
		return medalla;
	}
	public void setMedalla(CaracteristicaResponse medalla) {
		this.medalla = medalla;
	}
	public String getDescripcionCorta() {
		return descripcionCorta;
	}
	public void setDescripcionCorta(String descripcionCorta) {
		this.descripcionCorta = descripcionCorta;
	}
	public String getDescripcionLarga() {
		return descripcionLarga;
	}
	public void setDescripcionLarga(String descripcionLarga) {
		this.descripcionLarga = descripcionLarga;
	}
	
	
	
	
	
	
	
	
	

}
