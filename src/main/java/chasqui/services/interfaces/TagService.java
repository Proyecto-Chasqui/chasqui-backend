package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.model.Tag;
import chasqui.model.TagEvento;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;

public interface TagService {
	@Transactional
	public void guardar(Tag tag);
	@Transactional
	public void guardarTagTipoProducto(TagTipoProducto tag);
	@Transactional
	public void guardarTagZonaDeCobertura(TagZonaDeCobertura tag);
	@Transactional
	public void guardarTagTipoOrganizacion(TagTipoOrganizacion tag);
	@Transactional
	public void guardarTagEvento(TagEvento tag);
	@Transactional
	public void eliminarTag(Tag tag);
	
	public List<TagTipoProducto> obtenerTagsTipoProducto();
	public List<TagZonaDeCobertura> obtenerTagZonaDeCobertura();
	public List<TagTipoOrganizacion> obtenerTagTipoOrganizacion();
	public List<TagEvento> obtenerTagsTagEvento();
}
