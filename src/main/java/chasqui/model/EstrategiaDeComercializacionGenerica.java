package chasqui.model;

public class EstrategiaDeComercializacionGenerica {

	private boolean gcc;
	private boolean nodos;
	private boolean compraIndividual;
	private boolean puntoDeEntrega;
	private boolean seleccionDeDireccionDelUsuario;
	private boolean usaIncentivos;

	public EstrategiaDeComercializacionGenerica(){}
	
	public void Inicializar(){
		this.compraIndividual = true;
		this.gcc = false;
		this.nodos = false;
		this.puntoDeEntrega = false;
		this.seleccionDeDireccionDelUsuario = true;
		this.usaIncentivos = false;
	}
	
	public boolean isNodos() {
		return nodos;
	}

	public void setNodos(boolean permiteNodos) {
		this.nodos = permiteNodos;
	}

	public boolean isCompraIndividual() {
		return compraIndividual;
	}

	public void setCompraIndividual(boolean permiteCompraIndividual) {
		this.compraIndividual = permiteCompraIndividual;
	}

	public boolean isPuntoDeEntrega() {
		return puntoDeEntrega;
	}
	
	public void setPuntoDeEntrega(boolean permitePuntoDeEntrega) {
		this.puntoDeEntrega = permitePuntoDeEntrega;
	}

	public boolean isSeleccionDeDireccionDelUsuario() {
		return seleccionDeDireccionDelUsuario;
	}
	
	public void setSeleccionDeDireccionDelUsuario(boolean seleccionDeDireccionDelUsuario) {
		this.seleccionDeDireccionDelUsuario = seleccionDeDireccionDelUsuario;
	}

	public boolean isGcc() {
		return gcc;
	}
	
	public void setGcc(boolean gcc) {
		this.gcc = gcc;
	}

	public boolean isUsaIncentivos() {
		return usaIncentivos;
	}

	public void setUsaIncentivos(boolean usaIncentivos) {
		this.usaIncentivos = usaIncentivos;
	}
}