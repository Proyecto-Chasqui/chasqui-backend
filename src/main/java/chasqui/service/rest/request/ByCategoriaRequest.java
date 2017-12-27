package chasqui.service.rest.request;

public class ByCategoriaRequest extends ProductoRequest {

	
	private Integer idCategoria;
	
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	
	@Override
	public String toString(){
		return "ByCategoriaRequest [ idCategoria: "+ idCategoria + " pagina: "+ this.getPagina()+
				" precio: "+this.getPrecio()+ "cantidad de items: "+this.getCantItems()+" ]";
	}
}
