package chasqui.service.rest.response;


import chasqui.model.Zona;

public class ZonaGeoJsonResponse {
	
	private String type;
	private PropertiesZona properties;
	private ZonaGeometry geometry;
	private String status;
	
	public ZonaGeoJsonResponse(Zona zona){
		this.type = "Feature";
		this.properties = new PropertiesZona(zona);
		this.geometry = new ZonaGeometry(zona);
		this.status = "OK";
	}
	
	public ZonaGeoJsonResponse(){
		this.type = "Feature";
		this.properties = null;
		this.geometry = null;
		this.status = "ERROR";
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PropertiesZona getProperties() {
		return properties;
	}

	public void setProperties(PropertiesZona properties) {
		this.properties = properties;
	}

	public ZonaGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(ZonaGeometry geometry) {
		this.geometry = geometry;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
