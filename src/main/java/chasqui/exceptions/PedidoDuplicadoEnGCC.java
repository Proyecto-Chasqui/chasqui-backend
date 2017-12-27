package chasqui.exceptions;

public class PedidoDuplicadoEnGCC extends RuntimeException {


	public PedidoDuplicadoEnGCC(Exception e){
		super(e);
	}
	
	public PedidoDuplicadoEnGCC(Exception e , String mensaje){
		super(mensaje,e);
	}
	
	
	public PedidoDuplicadoEnGCC(String msg){
		super(msg);
	}

	public PedidoDuplicadoEnGCC() {
		// TODO Auto-generated constructor stub
	}

	
}
