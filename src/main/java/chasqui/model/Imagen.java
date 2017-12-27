package chasqui.model;

public class Imagen {
	
	private Integer id;
	private String path;
	private String nombre;
	private Boolean preview;
	private String absolutePath;

	//GETs & SETs
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public Boolean getPreview() {
		return preview;
	}

	public void setPreview(Boolean preview) {
		this.preview = preview;
	}
	
	
	
		
	

}
