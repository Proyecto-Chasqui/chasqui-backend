package chasqui.services.interfaces;

public interface ICaracteristica {

	public String getPathImagen();

	public String getNombre();

	public String getDescripcion();

	public Integer getId();

	public Boolean getEliminada();

	public void setEliminada(Boolean eliminada);
}
