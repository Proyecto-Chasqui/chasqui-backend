package chasqui.dtos;

import chasqui.model.Direccion;
import chasqui.model.PuntoDeRetiro;

public class PuntoDeRetiroDTO {
	Integer id;
	String nombre;
	String mensaje;
	Direccion direccion;
	
	public PuntoDeRetiroDTO(PuntoDeRetiro pr){
		id = pr.getId();
		nombre = pr.getNombre();
		mensaje = pr.getDescripcion();
		direccion = pr.getDireccion();
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
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String descripcion) {
		this.mensaje = descripcion;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}
}
