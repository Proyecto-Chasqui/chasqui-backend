package chasqui.dao;

import java.util.List;

import chasqui.model.Tag;
import chasqui.model.TagEvento;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;

public interface TagDAO {
	
	public void guardar(Tag tag);
	public void guardarTagTipoProducto(TagTipoProducto tag);
	public void guardarTagZonaDeCobertura(TagZonaDeCobertura tag);
	public void guardarTagTipoOrganizacion(TagTipoOrganizacion tag);
	public void guardarTagEvento(TagEvento tag);
	public void eliminar(Tag tag);
	public List<TagTipoProducto> obtenerTagsTipoProducto();
	public List<TagZonaDeCobertura> obtenerTagZonaDeCobertura();
	public List<TagTipoOrganizacion> obtenerTagTipoOrganizacion();
	public List<TagEvento> obtenerTagsTagEvento();
}
