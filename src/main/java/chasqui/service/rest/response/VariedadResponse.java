package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.model.Producto;
import chasqui.model.Variante;


public class VariedadResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5648828220930027872L;
	
	private Integer idProducto;
	private Integer idVariante;
	private Integer idCategoria;
	private Integer idFabricante;
	private String imagenPrincipal;
	private String codigoArticulo;
	private String nombreProducto;
	private String nombreVariedad;
	private String nombreFabricante;
	private String precioParteEntera;
	private String precioParteDecimal;
	private String descripcion;
	private Boolean destacado;
	private Double precio;
	private Double incentivo;
	private Integer stock;
	private List<CaracteristicaResponse>medallasProducto;
	private List<CaracteristicaResponse>medallasProductor;
	

	public Integer getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}
	public Integer getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}
	public Integer getIdFabricante() {
		return idFabricante;
	}
	public void setIdFabricante(Integer idFabricante) {
		this.idFabricante = idFabricante;
	}
	public String getImagenPrincipal() {
		return imagenPrincipal;
	}
	public void setImagenPrincipal(String imagenPrincipal) {
		this.imagenPrincipal = imagenPrincipal;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public String getNombreVariedad() {
		return nombreVariedad;
	}
	public void setNombreVariedad(String nombreVariedad) {
		this.nombreVariedad = nombreVariedad;
	}
	public String getNombreFabricante() {
		return nombreFabricante;
	}
	public void setNombreFabricante(String nombreFabricante) {
		this.nombreFabricante = nombreFabricante;
	}
	public String getPrecioParteEntera() {
		return precioParteEntera;
	}
	public void setPrecioParteEntera(String precioParteEntera) {
		this.precioParteEntera = precioParteEntera;
	}
	public String getPrecioParteDecimal() {
		return precioParteDecimal;
	}
	public void setPrecioParteDecimal(String precioParteDecimal) {
		this.precioParteDecimal = precioParteDecimal;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public List<CaracteristicaResponse> getMedallasProducto() {
		return medallasProducto;
	}
	public void setMedallasProducto(List<CaracteristicaResponse> medallasProducto) {
		this.medallasProducto = medallasProducto;
	}
	public List<CaracteristicaResponse> getMedallasProductor() {
		return medallasProductor;
	}
	public void setMedallasProductor(List<CaracteristicaResponse> medallasProductor) {
		this.medallasProductor = medallasProductor;
	}	
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Boolean getDestacado() {
		return destacado;
	}
	public void setDestacado(Boolean destacado) {
		this.destacado = destacado;
	}
	public Integer getIdVariante() {
		return idVariante;
	}
	public void setIdVariante(Integer idVariante) {
		this.idVariante = idVariante;
	}
	

	public String getCodigoArticulo() {
		return codigoArticulo;
	}
	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}
	public VariedadResponse(){}
	
	public VariedadResponse(Variante v, Producto p) {
		idProducto = p.getId();
		idVariante = v.getId();
		destacado = v.getDestacado();
		idCategoria = p.getCategoria().getId();
		idFabricante = p.getFabricante().getId();
		imagenPrincipal = v.obtenerImagenDePrevisualizacion();
		nombreProducto = p.getNombre();
		nombreVariedad = v.getNombre();
		nombreFabricante = p.getFabricante().getNombre();

		String precioStr = String.valueOf(v.getPrecio());
		precioParteEntera = precioStr.substring(0,precioStr.lastIndexOf("."));
		precioParteDecimal =  precioStr.substring(precioStr.lastIndexOf(".")+1);
		precio = v.getPrecio();
		incentivo = v.getIncentivo();
		stock = v.getStock();
		descripcion = v.getDescripcion();
		if(p.getCaracteristicas() != null && !p.getCaracteristicas().isEmpty()){
			medallasProducto = new ArrayList<CaracteristicaResponse>();
			for(Caracteristica c : p.getCaracteristicas()){
				medallasProducto.add(new CaracteristicaResponse(c));
			}			
		}
		if(p.getFabricante().getCaracteristicas() != null){
			medallasProductor = new ArrayList<CaracteristicaResponse>();
			for(CaracteristicaProductor cp : p.getFabricante().getCaracteristicas()) {
				medallasProductor.add(new CaracteristicaResponse(cp));
			}
		}
		codigoArticulo = v.getCodigo();
	}
	public Double getIncentivo() {
		return incentivo;
	}
	public void setIncentivo(Double incentivo) {
		this.incentivo = incentivo;
	}
	
}
