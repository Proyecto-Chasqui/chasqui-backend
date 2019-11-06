package chasqui.service.rest.request;

import java.io.Serializable;
import java.util.List;

public class VendedoresConTagRequest  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6808069742911793298L;
	
	private List<Integer> idsTagsTipoOrganizacion;
	private List<Integer> idsTagsTipoProducto;
	private List<Integer> idsTagsZonaDeCobertura;
	private String nombre;
	
	
	public VendedoresConTagRequest() {
		
	}


	public List<Integer> getIdsTagsTipoOrganizacion() {
		return idsTagsTipoOrganizacion;
	}


	public List<Integer> getIdsTagsTipoProducto() {
		return idsTagsTipoProducto;
	}


	public List<Integer> getIdsTagsZonaDeCobertura() {
		return idsTagsZonaDeCobertura;
	}


	public String getNombre() {
		return nombre;
	}


	public void setIdsTagsTipoOrganizacion(List<Integer> idsTagsTipoOrganizacion) {
		this.idsTagsTipoOrganizacion = idsTagsTipoOrganizacion;
	}


	public void setIdsTagsTipoProducto(List<Integer> idsTagsTipoProducto) {
		this.idsTagsTipoProducto = idsTagsTipoProducto;
	}


	public void setIdsTagsZonaDeCobertura(List<Integer> idsTagsZonaDeCobertura) {
		this.idsTagsZonaDeCobertura = idsTagsZonaDeCobertura;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

}
