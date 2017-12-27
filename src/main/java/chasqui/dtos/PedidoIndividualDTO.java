package chasqui.dtos;

import java.util.ArrayList;
import java.util.List;

public class PedidoIndividualDTO {
	
	private String usuarioCreador;

	private List<VarianteDTO> variantes;

	 //public VarianteDTO(String nombreProducto, String nombreVariante, Integer cantidad, Double precio) {
	
	public PedidoIndividualDTO(String user){
		this.usuarioCreador = user;
		this.variantes = new ArrayList<VarianteDTO>();
		
	}
	
	public List<VarianteDTO> getVariantes() {
		return variantes;
	}

	public void addVariante(Double precio, String nombreProducto, String nombreVariante, Integer cantidad) {
		
		VarianteDTO nuevaVariante = new VarianteDTO(nombreProducto, nombreVariante, cantidad, precio);
		this.variantes.add(nuevaVariante);
	
	}

	public Double getMontoTotal() {
		Double monto = 0.0;
		for(VarianteDTO v : this.getVariantes()){
		 monto = monto + v.getPrecio();
		}
		return monto;
	}

	public String getUsuarioCreador() {
		return usuarioCreador;
	}
	
}
