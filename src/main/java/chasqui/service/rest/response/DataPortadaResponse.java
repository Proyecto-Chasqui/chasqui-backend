package chasqui.service.rest.response;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.DataContacto;
import chasqui.model.DataMultimedia;
import chasqui.model.DataPortada;
import chasqui.model.Imagen;

public class DataPortadaResponse {
	
	private String urlLogo;
	private List<String> urlImagenesBanner;
	private List<String> urlImagenesPortada;
	private String textoPortada;
	private DataContactoResponse dataContacto;
	
	public DataPortadaResponse(DataMultimedia datamultimedia) {
		DataContacto dc = null;
		if(datamultimedia != null) {
			dc = datamultimedia.getDataContacto();
			DataPortada dp = datamultimedia.getDataPortada();
			urlImagenesBanner = new ArrayList<String>();
			urlImagenesPortada = new ArrayList<String>();
			if(dp.getLogo() != null) {
				urlLogo = dp.getLogo().getPath();
			}
			textoPortada = dp.getTextoBienvenida();
			for(Imagen img: dp.getImagenesDeBanner()) {
				urlImagenesBanner.add(img.getPath());
			}
			for(Imagen imgp: dp.getImagenesDePortada()) {
				urlImagenesPortada.add(imgp.getPath());
			}
		}
		this.setDataContacto(new DataContactoResponse(dc));
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

	public DataContactoResponse getDataContacto() {
		return dataContacto;
	}

	public void setDataContacto(DataContactoResponse dataContacto) {
		this.dataContacto = dataContacto;
	}

}
