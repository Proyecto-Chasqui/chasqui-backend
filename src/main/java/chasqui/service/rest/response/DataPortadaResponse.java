package chasqui.service.rest.response;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.DataPortada;
import chasqui.model.Imagen;

public class DataPortadaResponse {
	
	private String urlLogo;
	private List<String> urlImagenesBanner;
	private List<String> urlImagenesPortada;
	private String textoPortada;
	
	public DataPortadaResponse(DataPortada data) {
		urlImagenesBanner = new ArrayList<String>();
		urlImagenesPortada = new ArrayList<String>();
		urlLogo = data.getLogo().getPath();
		textoPortada = data.getTextoBienvenida();
		for(Imagen img: data.getImagenesDeBanner()) {
			urlImagenesBanner.add(img.getPath());
		}
		for(Imagen imgp: data.getImagenesDePortada()) {
			urlImagenesPortada.add(imgp.getPath());
		}
	}

	public String getUrlLogo() {
		return urlLogo;
	}

	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}

	public List<String> getUrlImagenesBanner() {
		return urlImagenesBanner;
	}

	public void setUrlImagenesBanner(List<String> urlImagenesBanner) {
		this.urlImagenesBanner = urlImagenesBanner;
	}

	public List<String> getUrlImagenesPortada() {
		return urlImagenesPortada;
	}

	public void setUrlImagenesPortada(List<String> urlImagenesPortada) {
		this.urlImagenesPortada = urlImagenesPortada;
	}

	public String getTextoPortada() {
		return textoPortada;
	}

	public void setTextoPortada(String textoPortada) {
		this.textoPortada = textoPortada;
	}
}
