package chasqui.service.rest.request;

import java.io.Serializable;
public class GrupoRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6415079864680366002L;
	Integer idVendedor;
	String alias;
	String descripcion;
	
	public GrupoRequest(){}
	
	public GrupoRequest(Integer idVendedor, String aliasGrupo, String descGrupo){
		this.idVendedor = idVendedor;
		this.alias= aliasGrupo;
		this.descripcion = descGrupo;
	}
//	
//	public GrupoRequest(Integer idVendedor, String aliasGrupo){
//		this.idVendedor = idVendedor;
//		this.aliasGrupo = aliasGrupo;
//		
//	}
	
	public Integer getIdVendedor() {
		return idVendedor;
	}
	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}
		
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
