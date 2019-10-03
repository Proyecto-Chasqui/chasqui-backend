package chasqui.service.rest.response;

import com.vividsolutions.jts.geom.Coordinate;

import chasqui.model.Zona;

public class ZonaGeometry {
	
	private String type;
	private Coordinate[] coordinates;
	
	public ZonaGeometry(Zona zona) {
		this.type = "Polygon";
		if(zona.getGeoArea() != null) {
			this.coordinates = zona.getGeoArea().getCoordinates();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinate[] coordenadas) {
		this.coordinates = coordenadas;
	}
	
}
