package chasqui.exceptions;

public class UsuarioNoPerteneceAlGrupoDeCompras extends Exception {

	public UsuarioNoPerteneceAlGrupoDeCompras(Exception e) {
		super(e);
	}

	public UsuarioNoPerteneceAlGrupoDeCompras(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public UsuarioNoPerteneceAlGrupoDeCompras(String msg) {
		super(msg);
	}

	public UsuarioNoPerteneceAlGrupoDeCompras() {
		// TODO Auto-generated constructor stub
	}

	
}
