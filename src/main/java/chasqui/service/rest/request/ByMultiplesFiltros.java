package chasqui.service.rest.request;

public class ByMultiplesFiltros extends ProductoRequest{
	
	private Integer idVendedor;
	private Integer idCategoria;
	private Integer idMedalla;
	private Integer idProductor;
	private Integer idMedallaProductor;
	private Integer numeroDeOrden;
	private String query;
	
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	
	public Integer getIdMedalla() {
		return idMedalla;
	}
	public void setIdMedalla(Integer idMedalla) {
		this.idMedalla = idMedalla;
	}
	public Integer getIdProductor() {
		return idProductor;
	}
	public void setIdProductor(Integer idProductor) {
		this.idProductor = idProductor;
	}
	
	public Integer getIdVendedor() {
		return idVendedor;
	}
	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
	public Integer getIdMedallaProductor() {
		return idMedallaProductor;
	}
	public void setIdMedallaProductor(Integer idSelloProductor) {
		this.idMedallaProductor = idSelloProductor;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	@Override
	public String toString(){
		return "ByMultiplesFiltros [ idVendedor: " + this.getIdVendedor()
								   + "idCategoria: "+ this.getIdCategoria()
								   + " idMedalla: " + this.getIdMedalla()
								   + "idMedallaProductor: " + this.getIdMedallaProductor()
								   + " idProductor: " + this.getIdProductor()
								   + "query" + this.getQuery()
								   + " pagina: "+ this.getPagina()
								   + " precio: "+this.getPrecio()
								   + " cantidad de items: "+this.getCantItems()
								   + " numero de orden: " +this.getNumeroDeOrden()
								   +" ]";
	}
	public Integer getNumeroDeOrden() {
		return numeroDeOrden;
	}
	public void setNumeroDeOrden(Integer numeroDeOrden) {
		this.numeroDeOrden = numeroDeOrden;
	}



}
