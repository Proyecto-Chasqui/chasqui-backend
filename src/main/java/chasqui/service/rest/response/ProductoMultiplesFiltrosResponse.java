package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.model.Variante;

public class ProductoMultiplesFiltrosResponse implements Serializable {


	private static final long serialVersionUID = 6903673923986394143L;

	private Integer cantItems;
	private Integer pagina;
	private Long total; //total de variantes. TODO cambiar por totalDeVariantes tanto aca como en FEW.
	private Long totalDePaginas;
	private List<VariedadResponse> productos = new ArrayList<VariedadResponse>();

	public ProductoMultiplesFiltrosResponse() {
	}

	public ProductoMultiplesFiltrosResponse(List<Variante> vrs, Integer pagina, Integer cantidadDeItems, final String precio,
			Long totalDeVariantes, Long totalDePaginas) {
		this.setCantItems(cantidadDeItems);
		this.setPagina(pagina);
		this.setTotal(totalDeVariantes);
		this.setTotalDePaginas(totalDePaginas);
		for (Variante v : vrs) {
			productos.add(new VariedadResponse(v, v.getProducto()));

		}
	}

	public ProductoMultiplesFiltrosResponse(List<Variante> vrs) {
		for (Variante v : vrs) {
			productos.add(new VariedadResponse(v, v.getProducto()));

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

	public Long getTotalDePaginas() {
		return totalDePaginas;
	}

	public void setTotalDePaginas(Long totalDePaginas) {
		this.totalDePaginas = totalDePaginas;
	}

}