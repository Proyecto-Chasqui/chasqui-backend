package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.TagDAO;
import chasqui.model.Tag;
import chasqui.model.TagEvento;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;
import chasqui.services.interfaces.TagService;

public class TagServiceImpl implements TagService{
	
	@Autowired
	private TagDAO tagDAO;
	
	@Override
	public void guardar(Tag tag) {
		tagDAO.guardar(tag);
		
	}

	@Override
	public void guardarTagTipoProducto(TagTipoProducto tag) {
		tagDAO.guardarTagTipoProducto(tag);
		
	}

	@Override
	public void guardarTagZonaDeCobertura(TagZonaDeCobertura tag) {
		tagDAO.guardarTagZonaDeCobertura(tag);
		
	}

	@Override
	public void guardarTagTipoOrganizacion(TagTipoOrganizacion tag) {
		tagDAO.guardarTagTipoOrganizacion(tag);
		
	}

	@Override
	public void guardarTagEvento(TagEvento tag) {
		tagDAO.guardarTagEvento(tag);
		
	}

	@Override
	public List<TagTipoProducto> obtenerTagsTipoProducto() {
		return tagDAO.obtenerTagsTipoProducto();
	}

	@Override
	public List<TagZonaDeCobertura> obtenerTagZonaDeCobertura() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TagTipoOrganizacion> obtenerTagTipoOrganizacion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TagEvento> obtenerTagsTagEvento() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eliminarTag(Tag tag) {
		tagDAO.eliminar(tag);
	}

}
