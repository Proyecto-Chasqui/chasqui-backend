package chasqui.model;


/*
 * En el modelo de compra individual no se permite la conformación de grupos
 * @see https://github.com/chasqui-ess/chasqui-docs/wiki/Compra-individual
 */
public class EstrategiaCompraIndividual implements IEstrategiaComercializacion {

	private Integer id;
	@Override
	public boolean permiteAltaGCC() {
		return false;
	}

	@Override
	public boolean permiteBajaGCC() {
		return false;
	}

	@Override
	public boolean permiteNuevoPedidoIndividual() {
		return true;
	}

	@Override
	public boolean permiteNuevoPedidoGrupal() {
		return false;
	}

	@Override
	public String getNombreEstrategia() {
		return "Compra individual"; //TODO externalizar
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

}
