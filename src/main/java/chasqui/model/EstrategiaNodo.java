package chasqui.model;

/*
 * En el modelo de compra por nodos, todos los pedidos son grupales y 
 * el usuario final no puede crear o dar de baja sus grupos
 * @see https://github.com/chasqui-ess/chasqui-docs/wiki/Compras-por-nodos-de-consumo
 */
public class EstrategiaNodo implements IEstrategiaComercializacion {

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
		return false;
	}

	@Override
	public boolean permiteNuevoPedidoGrupal() {
		return true;
	}

	@Override
	public String getNombreEstrategia() {
		return "Nodos"; //TODO externalizar
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
