package chasqui.exceptions;

public class GrupoCCInexistenteException extends Exception {

	public GrupoCCInexistenteException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GrupoCCInexistenteException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public GrupoCCInexistenteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public GrupoCCInexistenteException(Integer idGrupo) {
		super("No existe el grupo con id:" +idGrupo.toString());
		// TODO Auto-generated constructor stub
	}

	public GrupoCCInexistenteException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public GrupoCCInexistenteException(String string) {
		super(string); 
	}

}
