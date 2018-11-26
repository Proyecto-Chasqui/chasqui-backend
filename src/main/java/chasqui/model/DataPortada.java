package chasqui.model;

import java.util.ArrayList;
import java.util.List;

public class DataPortada {
	
	private Integer id;
	private Imagen logo;
	private List<Imagen> imagenesDePortada = new ArrayList<Imagen>();
	private List<Imagen> imagenesDeBanner = new ArrayList<Imagen>();
	private String textoBienvenida = "";
	
	public DataPortada() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Imagen getLogo() {
		return logo;
	}

	public void setLogo(Imagen logo) {
		this.logo = logo;
	}

	public List<Imagen> getImagenesDePortada() {
		return imagenesDePortada;
	}

	public void setImagenesDePortada(List<Imagen> imagenesDePortada) {
		this.imagenesDePortada = imagenesDePortada;
	}

	public List<Imagen> getImagenesDeBanner() {
		return imagenesDeBanner;
	}

	public void setImagenesDeBanner(List<Imagen> imagenesDeBanner) {
		this.imagenesDeBanner = imagenesDeBanner;
	}

	public String getTextoBienvenida() {
		return textoBienvenida;
	}

	public void setTextoBienvenida(String textoBienvenida) {
		this.textoBienvenida = textoBienvenida;
	}
	
	
}
