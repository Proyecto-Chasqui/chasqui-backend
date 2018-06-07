package chasqui.service.rest.request;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.json.simple.parser.ParseException;

public class ZonaRequest {
	private Integer id;
	private Integer idVendedor;
	private String nombre;
	private DateTime fechaCierre;
	private String mensaje;
	private Object coordenadas;
	
	public ZonaRequest(){
	}
	
	public ZonaRequest(Integer id, Integer idVendedor, String nombre, String fechacierre, String mensaje,Object coords) throws ParseException, java.text.ParseException {
		this.id = id;
		this.idVendedor = idVendedor;
		this.nombre = nombre;
		this.setFechaCierre(stringToDate(fechacierre));
		this.coordenadas = coords;
		this.mensaje = mensaje;
	}
	
	private DateTime stringToDate(String dateString) throws ParseException, java.text.ParseException{
		// Convierte un string con el formato "2015-04-29 17:00:11" a un objeto DateTime
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateResult = sdf.parse(dateString);
		return new DateTime(dateResult);
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

	public Object getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Object coordenadas) {
		this.coordenadas = coordenadas;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public DateTime getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(DateTime fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	
	
}
