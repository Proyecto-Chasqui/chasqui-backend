package chasqui.service.rest.response;

import java.util.List;

import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;

public class TagsResponse {
	private List<TagTipoOrganizacion> tagsTipoOrganizacion;
	private List<TagTipoProducto> tagsTipoProducto;
	private List<TagZonaDeCobertura> tagsZonaDeCobertura;
	
	public TagsResponse(List<TagTipoOrganizacion> tagsTipoOrganizacion, List<TagTipoProducto> tagsTipoProducto, List<TagZonaDeCobertura> tagsZonaDeCobertura ) {
		this.setTagsTipoOrganizacion(tagsTipoOrganizacion);
		this.setTagsTipoProducto(tagsTipoProducto);
		this.setTagsZonaDeCobertura(tagsZonaDeCobertura);
	}
	
	public List<TagTipoOrganizacion> getTagsTipoOrganizacion() {
		return tagsTipoOrganizacion;
	}
	public List<TagTipoProducto> getTagsTipoProducto() {
		return tagsTipoProducto;
	}
	public List<TagZonaDeCobertura> getTagsZonaDeCobertura() {
		return tagsZonaDeCobertura;
	}
	public void setTagsTipoOrganizacion(List<TagTipoOrganizacion> tagsTipoOrganizacion) {
		this.tagsTipoOrganizacion = tagsTipoOrganizacion;
	}
	public void setTagsTipoProducto(List<TagTipoProducto> tagsTipoProducto) {
		this.tagsTipoProducto = tagsTipoProducto;
	}
	public void setTagsZonaDeCobertura(List<TagZonaDeCobertura> tagsZonaDeCobertura) {
		this.tagsZonaDeCobertura = tagsZonaDeCobertura;
	}
	
}
