package chasqui.service.rest.response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import chasqui.model.Zona;

public class ZonaResponse {
	
	private String nombre;
	private String fechaCierrePedidos;
	private String descripcion;
	
	public ZonaResponse(Zona zona){
		this.nombre = zona.getNombre();
		this.setFechaCierrePedidos(format(zona.getFechaCierrePedidos()));
		this.descripcion = zona.getDescripcion();
	}
	
	public String format(DateTime date){
		DateTime dt = date;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
		String dtStr = fmt.print(dt);
		return dtStr;
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

	public String getFechaCierrePedidos() {
		return fechaCierrePedidos;
	}

	public void setFechaCierrePedidos(String fechaCierrePedidos) {
		this.fechaCierrePedidos = fechaCierrePedidos;
	}
	
}
