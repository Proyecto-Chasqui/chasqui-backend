package chasqui.model;

public class DataContacto {
	
	private Integer id;
	private Direccion direccion;
	private String telefono;
	private String celular;
	private String email;
	private String url;
	
	public DataContacto() {
		
	}
	
	public DataContacto initializeDataContacto(Direccion dir, String tel, String email, String celular, String url) {
		DataContacto data= new DataContacto();
		data.setDireccion(dir);
		data.setTelefono(tel);
		data.setEmail(email);
		data.setUrl(url);
		data.setCelular(celular);
		return data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}


}
