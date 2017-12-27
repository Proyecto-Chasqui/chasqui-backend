package chasqui.model;

public class EstrategiasDeComercializacion {
	
	private Integer id;
	
	private boolean nodos;
	
	private boolean gcc;
	
	private boolean compraIndividual;
	
	private boolean puntoDeEntrega;
	
	private boolean seleccionDeDireccionDelUsuario;
	
	private boolean nodosEnApp;
	
	private boolean gccEnApp;
	
	private boolean compraIndividualEnApp;
	
	private boolean puntoDeEntregaEnApp;
	
	public EstrategiasDeComercializacion(){
	}
	
	public void Inicializar(){
		this.compraIndividual = true;
		this.gcc = false;
		this.nodos = false;
		this.puntoDeEntrega = false;
		this.seleccionDeDireccionDelUsuario = true;
		this.compraIndividualEnApp = true;
		this.gccEnApp = false;
		this.nodosEnApp = false;
		this.puntoDeEntregaEnApp = false;
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

	public boolean isNodosEnApp() {
		return nodosEnApp;
	}

	public void setNodosEnApp(boolean permiteNodosEnApp) {
		this.nodosEnApp = permiteNodosEnApp;
	}

	public boolean isCompraIndividualEnApp() {
		return compraIndividualEnApp;
	}

	public void setCompraIndividualEnApp(boolean permiteCompraIndividualEnApp) {
		this.compraIndividualEnApp = permiteCompraIndividualEnApp;
	}

	public boolean isPuntoDeEntregaEnApp() {
		return puntoDeEntregaEnApp;
	}

	public void setPuntoDeEntregaEnApp(boolean permitePuntoDeEntregaEnApp) {
		this.puntoDeEntregaEnApp = permitePuntoDeEntregaEnApp;
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

	public boolean isGccEnApp() {
		return gccEnApp;
	}

	public void setGccEnApp(boolean gccEnApp) {
		this.gccEnApp = gccEnApp;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
