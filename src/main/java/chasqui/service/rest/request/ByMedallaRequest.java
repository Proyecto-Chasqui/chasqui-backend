package chasqui.service.rest.request;

public class ByMedallaRequest extends ProductoRequest{

	
	private Integer idMedalla;
	private Integer idVendedor;

	public Integer getIdMedalla() {
		return idMedalla;
	}

	public void setIdMedalla(Integer idMedalla) {
		this.idMedalla = idMedalla;
	}
	
	
	
	
	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	@Override
	public String toString(){
		return "ByMedallaRequest [ idMedalla: "+ idMedalla + " pagina: "+ this.getPagina()+
				" precio: "+this.getPrecio()+ "cantidad de items: "+this.getCantItems()+" ]";
	}
	
	
	
}
