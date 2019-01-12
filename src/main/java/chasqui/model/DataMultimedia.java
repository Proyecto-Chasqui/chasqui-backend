package chasqui.model;


/* 
 * Objeto Diseñado para agregar mas objetos de información a futuro
 * Por ejemplo dataQuienesSomos, dataContacto, dataGaleria, etc.
 */

public class DataMultimedia {
	
	private Integer id;
	private Integer idVendedor;
	private DataPortada dataPortada;
	private DataContacto dataContacto;
	
	public DataMultimedia() {
		
	}
	
	public DataMultimedia(Integer idVendedor) {
		this.idVendedor = idVendedor;
		this.dataPortada = new DataPortada();
		this.dataContacto = new DataContacto();
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public DataPortada getDataPortada() {
		return dataPortada;
	}

	public void setDataPortada(DataPortada dataPortada) {
		this.dataPortada = dataPortada;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DataContacto getDataContacto() {
		return dataContacto;
	}

	public void setDataContacto(DataContacto dataContacto) {
		this.dataContacto = dataContacto;
	}
	
}
