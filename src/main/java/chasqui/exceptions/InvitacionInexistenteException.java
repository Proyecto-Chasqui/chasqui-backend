package chasqui.exceptions;

public class InvitacionInexistenteException extends RuntimeException {		

		public InvitacionInexistenteException(Exception e){
			super(e);
		}
		
		public InvitacionInexistenteException(Exception e , String mensaje){
			super(mensaje,e);
		}
		
		
		public InvitacionInexistenteException(String msg){
			super(msg);
		}

		public InvitacionInexistenteException() {
			// TODO Auto-generated constructor stub
		}
		
	}
