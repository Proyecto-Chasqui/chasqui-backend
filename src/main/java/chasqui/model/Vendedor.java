package chasqui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

public class Vendedor extends Usuario{

	
	private Integer montoMinimoPedido;
	private String nombre;
	private String msjCierrePedido;
	private Integer distanciaCompraColectiva;
	private String mapaZonas;
	private List<Categoria> categorias;
	private List<Zona> zonas;
	private List<Fabricante> fabricantes;
	private EstrategiasDeComercializacion estrategiasUtilizadas;
	@Deprecated
	private List<IEstrategiaComercializacion> estrategiasPermitidas;
	private List<PuntoDeRetiro> puntosDeRetiro;
	private String url;
	
	
	//GETs & SETs	
	
	//Por el momento las estrategias utilizadas estan siendo creadas con un modo default
	//hasta que sea configurable desde el panel de administracion.
	public Vendedor(String nombre,String username, String email, String pwd, String urlBase) {
		this.setEstrategiasUtilizadas(new EstrategiasDeComercializacion());
		this.setUsername(username);
		this.setEmail(email);
		this.setNombre(nombre);
		this.setPassword(pwd);
		this.setIsRoot(false);
		this.setUrl(urlBase);
		this.setMontoMinimoPedido(0);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Vendedor() {
		categorias = new ArrayList<Categoria>();
		fabricantes = new ArrayList<Fabricante>();
	}

	public Integer getMontoMinimoPedido() {
		return montoMinimoPedido;
	}

	public void setMontoMinimoPedido(Integer montoMinimoPedido) {
		this.montoMinimoPedido = montoMinimoPedido;
	}

//	public DateTime getFechaCierrePedido() {
//		return fechaCierrePedido;
//	}
//
//	public void setFechaCierrePedido(DateTime fechaCierrePedido) {
//		this.fechaCierrePedido = fechaCierrePedido;
//	}

	public String getMsjCierrePedido() {
		return msjCierrePedido;
	}

	public void setMsjCierrePedido(String msjCierrePedido) {
		this.msjCierrePedido = msjCierrePedido;
	}

	public Integer getDistanciaCompraColectiva() {
		return distanciaCompraColectiva;
	}

	public void setDistanciaCompraColectiva(Integer distanciaCompraColectiva) {
		this.distanciaCompraColectiva = distanciaCompraColectiva;
	}

	public String getMapaZonas() {
		return mapaZonas;
	}

	public void setMapaZonas(String mapaZonas) {
		this.mapaZonas = mapaZonas;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public List<Fabricante> getFabricantes() {
		return fabricantes;
	}

	public void setFabricantes(List<Fabricante> fabricantes) {
		this.fabricantes = fabricantes;
	}

	public List<IEstrategiaComercializacion> getEstrategiasPermitidas() {
		return estrategiasPermitidas;
	}

	public void setEstrategiasPermitidas(List<IEstrategiaComercializacion> estrategiasPermitidas) {
		this.estrategiasPermitidas = estrategiasPermitidas;
	}


	//METHODS

	public boolean contieneProductor(String nombreProductor){
		for(Fabricante f : fabricantes ){
			if( f.getNombre().equals(nombreProductor)){
				return true;
			}
		}
		return false;
	}

	public void agregarProductor(Fabricante f) {
		fabricantes.add(f);
	}
	
	public void eliminarProductor (Fabricante f) {
		fabricantes.remove(f);
	}
	
	
	public void agregarCategoria (Categoria c){
		categorias.add(c);
	}
	
	public void eliminarCategoria (Categoria c){
		categorias.remove(c);
	}
	
	public List<Producto> obtenerProductos(){
		List<Producto>p = new ArrayList<Producto>();
		for(Categoria c :categorias){
			p.addAll(c.getProductos());
		}
		return p;
	}
	
	
	public List<Producto> getProductos(){
		List<Producto>p = new ArrayList<Producto>();
		for(Categoria c :categorias){
			p.addAll(c.getProductos());
		}
		return p;
	}

	public boolean contieneCategoria(String value) {
		for(Categoria c : categorias){
			if(c.getNombre().equalsIgnoreCase(value)){
				return true;
			}
		}
		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String uRL) {
		url = uRL;
  }
	
	private Variante obtenerVarianteConId(Integer id){
		Variante v = null;
		for(Producto p : this.getProductos()){
			for(Variante va : p.getVariantes()){
				if(va.getId().equals(id)){
					v = va;
				}
			}
		}
		return v;
	}
	
	public void descontarStockYReserva(Pedido p) {
		for(ProductoPedido pp : p.getProductosEnPedido()){
			Variante v = this.obtenerVarianteConId(pp.getIdVariante());
			v.setStock(v.getStock() - pp.getCantidad());
			v.setCantidadReservada(v.getCantidadReservada() - pp.getCantidad());
		}
	}

	public List<Zona> getZonas() {
		return zonas;
	}

	public void setZonas(List<Zona> zonas) {
		this.zonas = zonas;
	}

	public Collection<? extends Producto> obtenerProductosDelFabricante(Integer fabricanteSeleccionadoId) {
		List<Producto>p = new ArrayList<Producto>();
		for(Fabricante f :fabricantes){
			if(f.getId() == fabricanteSeleccionadoId){
				p.addAll(f.getProductos());
			}
		}
		return p;
	}

	public EstrategiasDeComercializacion getEstrategiasUtilizadas() {
		if(this.estrategiasUtilizadas == null){
		   this.estrategiasUtilizadas = new EstrategiasDeComercializacion();
		   this.estrategiasUtilizadas.Inicializar();
		}
		return estrategiasUtilizadas;
	}

	public void setEstrategiasUtilizadas(EstrategiasDeComercializacion estrategias) {
		this.estrategiasUtilizadas = estrategias;
	}

	public List<PuntoDeRetiro> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setPuntosDeRetiro(List<PuntoDeRetiro> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}
	
	public void agregarPuntoDeRetiro(PuntoDeRetiro puntoderetiro) {
		//escanear que no haya repetidos
		this.puntosDeRetiro.add(puntoderetiro);
	}


}
