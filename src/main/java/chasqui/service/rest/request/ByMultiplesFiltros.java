package chasqui.service.rest.request;

import java.util.List;

public class ByMultiplesFiltros extends ProductoRequest{
	
	private Integer idVendedor;
	private Integer idCategoria;
	private List<Integer> idsSellosProducto;
	private Integer idProductor;
	private List<Integer> idsSellosProductor;
	private Integer numeroDeOrden;
	private String query;
	
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
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
								   + "idsSellosProductos: [" + this.getIdsSellosProducto() + "]"
								   + "idsSellosProductor: [" + this.getIdsSellosProductor() +"]"
								   + "idProductor: " + this.getIdProductor()
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
	public List<Integer> getIdsSellosProductor() {
		return idsSellosProductor;
	}
	public void setIdsSellosProductor(List<Integer> idsSellosProductor) {
		this.idsSellosProductor = idsSellosProductor;
	}
	public List<Integer> getIdsSellosProducto() {
		return idsSellosProducto;
	}
	public void setIdsSellosProducto(List<Integer> idsSellosProducto) {
		this.idsSellosProducto = idsSellosProducto;
	}



}
