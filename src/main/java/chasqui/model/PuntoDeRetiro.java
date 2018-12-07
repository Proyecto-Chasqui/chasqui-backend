package chasqui.model;

import com.vividsolutions.jts.geom.Point;


/**
 * 
 * @author David
 *
 */
public class PuntoDeRetiro{
	private Integer id;
	private String idExterno;
	private String nombre;
	private String descripcion;
	private Boolean disponible;
	private Direccion direccion;
	
	public PuntoDeRetiro(){
		
	}
	
	public PuntoDeRetiro(Direccion dir){
		this.direccion = dir;
	}
	
	public PuntoDeRetiro(PuntoDeRetiro p) {
		this.idExterno=p.getIdExterno();
		this.nombre = p.getNombre();
		this.descripcion = p.getDescripcion();
		this.disponible = p.getDisponible();
		this.direccion = p.getDireccion();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdExterno() {
		return idExterno;
	}

	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getDisponible() {
		return disponible;
	}

	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}

	public String getCalle() {
		return this.direccion.getCalle();
	}
	
	public void setCalle(String calle){
		this.direccion.setCalle(calle);
	}

	public String getCalleAdyacente1() {
		return this.direccion.getCalleAdyacente1();
	}
	
	public void setCalleAdyacente1(String calle){
		this.direccion.setCalleAdyacente1(calle);
	}

	public String getCalleAdyacente2() {
		return this.direccion.getCalleAdyacente2();
	}
	
	public void setCalleAdyacente2(String calle){
		this.direccion.setCalleAdyacente2(calle);
	}

	public Integer getAltura() {
		return this.direccion.getAltura();
	}
	
	public void setAltura(Integer altura){
		this.direccion.setAltura(altura);
	}

	public String getLocalidad() {
		return this.direccion.getLocalidad();
	}
	
	public void setLocalidad(String localidad){
		this.direccion.setLocalidad(localidad);
	}

	public String getCodigoPostal() {
		return this.direccion.getCodigoPostal();
	}
	
	public void setCodigoPostal(String codigoPostal){
		this.direccion.setCodigoPostal(codigoPostal);
	}

	public String getDepartamento() {
		return this.direccion.getDepartamento();
	}
	
	public void setDepartamento(String depto){
		this.direccion.setDepartamento(depto);
	}

	public String getLatitud() {
		return this.direccion.getLatitud();
	}
	
	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	public void setLatitud(String lat){
		this.direccion.setLatitud(lat);
	}

	public String getLongitud() {
		return this.direccion.getLocalidad();
	}
	
	public void setLongitud(String lng){
		this.direccion.setLongitud(lng);
	}

	public Point getGeoUbicacion() {
		return this.direccion.getGeoUbicacion();
	}
	
	public void setGeoUbicacion(Point geoUbicacion){
		this.direccion.setGeoUbicacion(geoUbicacion);
	}
	
	//solo para ZK
	public boolean noDisponible(){
		boolean ret = false;
		if(! disponible){
			ret = true;
		}
		return ret;
	}
	
}
