package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Vendedor;

public class VendedorResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642765076419773900L;
	private Integer id;
	private String nombre;
	private String imagen;
	private EstrategiasDeComercializacion estrategia;
	
	
	public VendedorResponse(){}
	
	public VendedorResponse(Vendedor v){
		id = v.getId();
		nombre = v.getNombre();
		imagen = v.getImagenPerfil();
		setEstrategia(v.getEstrategiasUtilizadas());
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public EstrategiasDeComercializacion getEstrategia() {
		return estrategia;
	}
	public void setEstrategia(EstrategiasDeComercializacion estrategia) {
		this.estrategia = estrategia;
	}

	
	
	
	
	
	
	
}
