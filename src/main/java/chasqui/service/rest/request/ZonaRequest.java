package chasqui.service.rest.request;

public class ZonaRequest {
	private Integer id;
	private String nombre;
	private String fechaCierre;
	private String mensaje;
	private Object coordenadas;
	
	public ZonaRequest(){
	}
	
	public ZonaRequest(Integer id, String nombre, String fechacierre, String mensaje,Object coords) {
		this.id = id;
		this.nombre = nombre;
		this.fechaCierre = fechacierre;
		this.coordenadas = coords;
		this.mensaje = mensaje;
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

	public String getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(String fechaCierre) {
		this.fechaCierre = fechaCierre;
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
	
	
}
