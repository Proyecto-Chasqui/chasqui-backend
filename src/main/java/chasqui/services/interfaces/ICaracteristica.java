package chasqui.services.interfaces;

public interface ICaracteristica {

	public String getPathImagen();

	public String getNombre();

	public String getDescripcion();

	public Integer getId();

	public Boolean getEliminada();

	public void setEliminada(Boolean eliminada);
	
	public void setNombre(String nombre);
	
	public void setDescripcion(String descripcion);
	
	public void setPathImagen(String path);
}
