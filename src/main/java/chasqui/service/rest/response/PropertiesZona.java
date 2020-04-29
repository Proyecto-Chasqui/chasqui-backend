package chasqui.service.rest.response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import chasqui.model.Zona;

public class PropertiesZona {
	
	private Integer id;
	private String nombreZona;
	private String fechaCierre;
	private String mensaje;
	
	public PropertiesZona(Zona zona) {
		this.id = zona.getId();
		this.nombreZona = zona.getNombre();
		this.fechaCierre = this.format(zona.getFechaCierrePedidos());
		this.mensaje = zona.getDescripcion();
	}
	
	
	public String format(DateTime date){
		DateTime dt = date;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		String dtStr = fmt.print(dt);
		return dtStr;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNombreZona() {
		return nombreZona;
	}


	public void setNombreZona(String nombre) {
		this.nombreZona = nombre;
	}


	public String getFechaCierre() {
		return fechaCierre;
	}


	public void setFechaCierre(String fechaCierrePedidos) {
		this.fechaCierre = fechaCierrePedidos;
	}


	public String getMensaje() {
		return mensaje;
	}


	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
}
