package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.dtos.PuntoDeRetiroDTO;
import chasqui.model.EstrategiaDeComercializacionGenerica;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;

public class VendedorResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642765076419773900L;
	private Integer id;
	private String nombre;
	private String imagen;
	private EstrategiaDeComercializacionGenerica few;
	private EstrategiaDeComercializacionGenerica app;
	private List<PuntoDeRetiroDTO> puntosDeRetiro; 
	
	
	public VendedorResponse(){}
	
	public VendedorResponse(Vendedor v){
		id = v.getId();
		nombre = v.getNombre();
		imagen = v.getImagenPerfil();
		EstrategiasDeComercializacion estrategias = v.getEstrategiasUtilizadas();
		this.setFew(new EstrategiaDeComercializacionGenerica());
		this.setApp(new EstrategiaDeComercializacionGenerica());
		this.inicializarPuntosDeRetiro(v);
		this.inicializarEstrategias(estrategias);
	}
	
	private void inicializarPuntosDeRetiro(Vendedor v){
	  this.puntosDeRetiro = new ArrayList<PuntoDeRetiroDTO>();
	  List<PuntoDeRetiro> puntosDeRetiro = v.getPuntosDeRetiro();
	  for(PuntoDeRetiro p : puntosDeRetiro){
		  PuntoDeRetiroDTO prdto = new PuntoDeRetiroDTO();
		  prdto.setDisponible(p.getDisponible());
		  prdto.setId(p.getId());
		  prdto.setNombre(p.getNombre());
		  this.puntosDeRetiro.add(prdto);
	  }
	}
	
	
	private void inicializarEstrategias(EstrategiasDeComercializacion estrategia) {
		this.getFew().setCompraIndividual(estrategia.isCompraIndividual());
		this.getFew().setGcc(estrategia.isGcc());
		this.getFew().setNodos(estrategia.isNodos());
		this.getFew().setPuntoDeEntrega(estrategia.isPuntoDeEntrega());
		this.getFew().setSeleccionDeDireccionDelUsuario(estrategia.isSeleccionDeDireccionDelUsuario());
		
		this.getApp().setCompraIndividual(estrategia.isCompraIndividualEnApp());
		this.getApp().setGcc(estrategia.isGccEnApp());
		this.getApp().setNodos(estrategia.isNodosEnApp());
		this.getApp().setPuntoDeEntrega(estrategia.isPuntoDeEntregaEnApp());
		this.getApp().setSeleccionDeDireccionDelUsuario(estrategia.isSeleccionDeDireccionDelUsuario());
		
	}

	public EstrategiaDeComercializacionGenerica getFew() {
		return few;
	}

	public void setFew(EstrategiaDeComercializacionGenerica few) {
		this.few = few;
	}

	public EstrategiaDeComercializacionGenerica getApp() {
		return app;
	}

	public void setApp(EstrategiaDeComercializacionGenerica app) {
		this.app = app;
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

	public List<PuntoDeRetiroDTO> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setPuntosDeRetiro(List<PuntoDeRetiroDTO> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}
	
}
