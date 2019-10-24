package chasqui.model;

import org.joda.time.DateTime;

public class Tag {
	
	private Integer id;
	private String nombre;
	private String descripcion;
	private DateTime fechaCreacion;
	private DateTime fechaModificacion;
	
	public Tag() {
		
	}
	
	public Tag(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fechaCreacion = new DateTime();
		this.fechaModificacion = new DateTime();
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(DateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
}
