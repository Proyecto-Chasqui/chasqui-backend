package chasqui.model;

/*
 * En el modelo de compra colectiva el usuario puede crear y dar de baja grupos,
 * puede hacer tanto pedidos individuales como colectivos
 * @see https://github.com/chasqui-ess/chasqui-docs/wiki/Compras-colectivas
 */
public class EstrategiaGCC implements IEstrategiaComercializacion {

	private Integer id;
	@Override
	public boolean permiteAltaGCC() {
		return true;
	}

	@Override
	public boolean permiteBajaGCC() {
		return true;
	}

	@Override
	public boolean permiteNuevoPedidoIndividual() {
		return true;
	}

	@Override
	public boolean permiteNuevoPedidoGrupal() {
		return true;
	}

	@Override
	public String getNombreEstrategia() {
		return "GCC"; //TODO externalizar
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
