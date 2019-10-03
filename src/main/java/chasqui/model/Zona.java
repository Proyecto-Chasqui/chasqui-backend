package chasqui.model;

import java.util.Date;

import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class Zona {

	private Integer id;
	private Integer idVendedor; //vendedor
	private String nombre;
	private DateTime fechaCierrePedidos;
	private String descripcion;
	private Polygon geoArea;
	
	//GETs & SETs
	
	public Zona(String zona, DateTime fechaCierre, String msg) {
		this.nombre = zona;
		this.fechaCierrePedidos = fechaCierre;
		this.descripcion = msg;
	}
	
	public Zona(){}

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
	
	public DateTime getFechaCierrePedidos() {
		return fechaCierrePedidos;
	}
	
	public void setFechaCierrePedidos(DateTime fechaCierre) {
		this.fechaCierrePedidos = fechaCierre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	public Polygon getGeoArea() {
		return geoArea;
	}

	public void setGeoArea(Polygon geoArea) {
		this.geoArea = geoArea;
	}

	public void editar(String msg, Date fechaCierrePedidos, String nombreZona) {
		descripcion = msg;
		this.fechaCierrePedidos = new DateTime(fechaCierrePedidos.getTime());
		nombre = nombreZona;
	}
	
	public void editarConGeo(String msg, Date fechaCierrePedidos, String nombreZona, Polygon geoarea) {
		descripcion = msg;
		this.fechaCierrePedidos = new DateTime(fechaCierrePedidos.getTime());
		nombre = nombreZona;
		this.geoArea = geoarea;
	}


	
	public String toString(){
		return nombre;
	}
	

	
}
