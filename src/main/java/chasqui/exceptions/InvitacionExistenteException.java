package chasqui.exceptions;

public class InvitacionExistenteException extends RuntimeException {		

		public InvitacionExistenteException(Exception e){
			super(e);
		}
		
		public InvitacionExistenteException(Exception e , String mensaje){
			super(mensaje,e);
		}
		
		
		public InvitacionExistenteException(String msg){
			super(msg);
		}

		public InvitacionExistenteException() {
			// TODO Auto-generated constructor stub
		}
		
	}
