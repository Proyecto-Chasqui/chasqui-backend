package chasqui.service.rest.response;

import java.io.Serializable;

import chasqui.model.EstrategiaDeComercializacionGenerica;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Vendedor;

public class VendedorResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642765076419773900L;
	private Integer id;
	private String nombre;
	private String nombreCorto;
	private String imagen;
	private String urlMapa;
	private Integer tiempoDeVencimiento;
	private Integer montoMinimo;
	private EstrategiaDeComercializacionGenerica few;
	private EstrategiaDeComercializacionGenerica app;
	
	
	public VendedorResponse(){}
	
	public VendedorResponse(Vendedor v){
		id = v.getId();
		nombre = v.getNombre();
		nombreCorto = v.getNombreCorto();
		imagen = v.getImagenPerfil();
		tiempoDeVencimiento = v.getTiempoVencimientoPedidos();
		montoMinimo = v.getMontoMinimoPedido();
		EstrategiasDeComercializacion estrategias = v.getEstrategiasUtilizadas();
		this.setFew(new EstrategiaDeComercializacionGenerica());
		this.setApp(new EstrategiaDeComercializacionGenerica());
		this.inicializarEstrategias(estrategias);
		if(v.getMapaZonas() != null ) {
			this.setUrlMapa(v.getMapaZonas());
		}else {
			this.setUrlMapa("");
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
	
	public String getNombreCorto() {
		return nombreCorto;
	}

	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
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

	public String getUrlMapa() {
		return urlMapa;
	}

	public void setUrlMapa(String urlMapa) {
		this.urlMapa = urlMapa;
	}

	public Integer getTiempoDeVencimiento() {
		return tiempoDeVencimiento;
	}

	public void setTiempoDeVencimiento(Integer tiempoDeVencimiento) {
		this.tiempoDeVencimiento = tiempoDeVencimiento;
	}

	public Integer getMontoMinimo() {
		return montoMinimo;
	}

	public void setMontoMinimo(Integer montoMinimo) {
		this.montoMinimo = montoMinimo;
	}

	
}
