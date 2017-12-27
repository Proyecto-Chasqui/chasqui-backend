package chasqui.exceptions;

public class VendedorInexistenteException extends Exception {		

		public VendedorInexistenteException(Exception e){
			super(e);
		}
		
		public VendedorInexistenteException(Exception e , String mensaje){
			super(mensaje,e);
		}
		
		
		public VendedorInexistenteException(String msg){
			super("No existe el vendedor con id: "+ msg);
		}

		public VendedorInexistenteException() {
			// TODO Auto-generated constructor stub
		}
		
	}
