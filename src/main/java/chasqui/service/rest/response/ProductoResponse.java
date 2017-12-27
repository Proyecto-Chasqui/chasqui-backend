package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chasqui.model.Variante;

public class ProductoResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7556219779540939060L;

	private Integer cantItems;
	private Integer pagina;
	private Long total;
	private List<VariedadResponse>productos = new ArrayList<VariedadResponse>();
	
	public ProductoResponse(){}

	public ProductoResponse (List<Variante> vrs,Integer pag,Integer items,final String precio, Long tot){
		cantItems = items;
		pagina = pag;
		total = tot;
		for(Variante v : vrs){
			productos.add(new VariedadResponse(v,v.getProducto()));				
			
		}
		
		Collections.sort(productos, new Comparator<VariedadResponse>(){

			@Override
			public int compare(VariedadResponse o1, VariedadResponse o2) {
				if(precio.equals("Up")){
					return o2.getPrecio().compareTo(o1.getPrecio());
				}
				return o1.getPrecio().compareTo(o2.getPrecio());
			}
			
		});
	}
	

	public ProductoResponse (List<Variante> vrs){
		for(Variante v : vrs){
			productos.add(new VariedadResponse(v,v.getProducto()));				
			
		}
	}
	


	public Integer getCantItems() {
		return cantItems;
	}


	public void setCantItems(Integer cantItems) {
		this.cantItems = cantItems;
	}


	public Integer getPagina() {
		return pagina;
	}


	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}


	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<VariedadResponse> getProductos() {
		return productos;
	}


	public void setProductos(List<VariedadResponse> productos) {
		this.productos = productos;
	}
	
	
	
	
	
	
	
	
}
