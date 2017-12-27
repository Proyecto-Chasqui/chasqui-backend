package chasqui.test.builders;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import chasqui.model.Direccion;
import chasqui.model.Zona;

public class DireccionBuilder {
	
	private final GeometryFactory geometryFactory = new GeometryFactory();
	
	private String alias = "CASA";
	private String calle = "calle";
	private Integer altura = 1234;
	private String codigoPostal = "1234";
	private String departamento = "4D";
	private Boolean predeterminada = true;
	private Point geoUbicacion = geometryFactory.createPoint(new Coordinate(15,15));
	private String latitud = "15";
	private String longitud = "15";
	private String localidad = "Bernal";
	private Zona zona = null;
	
	public static DireccionBuilder unaDireccion(){
		return new DireccionBuilder();
	}
	
	public Direccion build(){
		Direccion d = new Direccion();
		d.setAlias(alias);
		d.setCalle(calle);
		d.setAltura(altura);
		d.setCodigoPostal(codigoPostal);
		d.setDepartamento(departamento);
		d.setPredeterminada(predeterminada);
		d.setGeoUbicacion(geoUbicacion);
		d.setLatitud(latitud);
		d.setLongitud(longitud);
		d.setLocalidad(localidad);
		//d.setZona(zona);
		return d;
	}
	
	public DireccionBuilder conZona(Zona pZona){
		this.zona = pZona;
		return this;
	}
	
	public DireccionBuilder conLocalidad(String pLocalidad){
		this.localidad = pLocalidad;
		return this;
	}
	
	public DireccionBuilder conLongitud(String pLong){
		this.longitud = pLong;
		return this;
	}
	
	public DireccionBuilder conLatitud(String pLat){
		this.latitud = pLat;
		return this;
	}
	
	public DireccionBuilder conGeoUbicacion(Point pGeoUbicacion){
		this.geoUbicacion = pGeoUbicacion;
		return this;
	}
	
	public DireccionBuilder conPredeterminada(boolean bool){
		this.predeterminada = bool;
		return this;
	}
	
	public DireccionBuilder conDepartamento(String pDepartamento){
		this.departamento = pDepartamento;
		return this;
	}
	
	public DireccionBuilder conCodigoPostal(String pCodigoPostal){
		this.codigoPostal = pCodigoPostal;
		return this;
	}
	
	public DireccionBuilder conAltura(String pAltura){
		this.alias =pAltura;
		return this;
	}
	
	public DireccionBuilder conCalle(String pCalle){
		this.calle = pCalle;
		return this;
	}
	
	public DireccionBuilder conAlias(String pAlias){
		this.alias =pAlias;
		return this;
	}
	
}
