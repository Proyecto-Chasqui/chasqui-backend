package chasqui.service.rest.request;

public abstract class ProductoRequest {

	private Integer pagina;
	private Integer cantItems;
	private String  precio;
	
	
	public Integer getPagina() {
		return pagina;
	}
	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}
	public Integer getCantItems() {
		return cantItems;
	}
	public void setCantItems(Integer cantItems) {
		this.cantItems = cantItems;
	}
	public String getPrecio() {
		return precio;
	}
	public void setPrecio(String precio) {
		this.precio = precio;
	}

	
	
	
	
	
	
}
