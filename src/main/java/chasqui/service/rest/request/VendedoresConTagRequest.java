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
	private boolean usaEstrategiaNodos;
	private boolean usaEstrategiaGrupos;
	private boolean usaEstrategiaIndividual;
	private boolean entregaADomicilio;
	private boolean usaPuntoDeRetiro;
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


	public boolean isUsaEstrategiaNodos() {
		return usaEstrategiaNodos;
	}


	public boolean isUsaEstrategiaGrupos() {
		return usaEstrategiaGrupos;
	}


	public boolean isUsaEstrategiaIndividual() {
		return usaEstrategiaIndividual;
	}


	public boolean isEntregaADomicilio() {
		return entregaADomicilio;
	}


	public boolean isUsaPuntoDeRetiro() {
		return usaPuntoDeRetiro;
	}


	public void setUsaEstrategiaNodos(boolean usaEstrategiaNodos) {
		this.usaEstrategiaNodos = usaEstrategiaNodos;
	}


	public void setUsaEstrategiaGrupos(boolean usaEstrategiaGrupos) {
		this.usaEstrategiaGrupos = usaEstrategiaGrupos;
	}


	public void setUsaEstrategiaIndividual(boolean usaEstrategiaIndividual) {
		this.usaEstrategiaIndividual = usaEstrategiaIndividual;
	}


	public void setEntregaADomicilio(boolean entregaADomicilio) {
		this.entregaADomicilio = entregaADomicilio;
	}


	public void setUsaPuntoDeRetiro(boolean usaPuntoDeRetiro) {
		this.usaPuntoDeRetiro = usaPuntoDeRetiro;
	}
	

}
