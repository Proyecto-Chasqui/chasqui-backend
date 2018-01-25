package chasqui.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.PedidoDAO;
import chasqui.model.Pedido;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;

@Aspect
public class DateableAspect {
	@Autowired
	private PedidoDAO pedidoDao;
	
	/**
	 * Aspecto generico para todo metodo que tenga @Datable
	 * Si dentro de los argumentos hay un Pedido le modifica el timestamp. 
	 * @param pointcut
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(chasqui.aspect.Dateable)")
	public Object actualizarTimeStampEnPedidoSiEstaEnLosArgumentos(ProceedingJoinPoint pointcut) throws Throwable{
			Object[] args = pointcut.getArgs();
			for(Object arg : args){
				Pedido pedido;
				if(arg.getClass().equals(Pedido.class)){
					pedido = (Pedido) arg;
					setearTimeStamp(pedido);
				}			
			}
			return pointcut.proceed();			
	}
	
	/**
	 * Este pointcut setea el timestamp en metodos que tengan email y idPedido como parametros
	 * deja proceder al metodo capturado y luego modifica en la BD el timestamp del pedido
	 * @param pointcut
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(chasqui.aspect.Dateable)&& execution(* *(String,Integer))")
	public Object actualizarTimeStampEnBD(ProceedingJoinPoint pointcut) throws Throwable{
		pointcut.proceed();
		Object[] args = pointcut.getArgs();
		if(args.length == 2){
			String email=null;
			Integer id=null;
			for(Object arg : args){
				if(arg.getClass().equals(String.class)){
					email = (String) arg;
				}else{
					if(arg.getClass().equals(Integer.class)){
						id = (Integer) arg;
					}
				}			
			}
			setearTimeStampEnBD(id);
		}
		return pointcut;
	}
	
	/**
	 * Este pointcut setea el timestamp en metodos que tengan algun parametro con un request asociado a un pedido
	 * deja proceder al metodo capturado y luego modifica en la BD el timestamp del pedido.
	 * @param pointcut
	 * @return
	 * @throws Throwable
	 */
	
	@Around("@annotation(chasqui.aspect.Dateable)")
	public Object actualizarTimeStampEnPedidoConRequest(ProceedingJoinPoint pointcut) throws Throwable{
		pointcut.proceed();
		Object[] args = pointcut.getArgs();
		for(Object arg : args){
			if(arg.getClass().equals(AgregarQuitarProductoAPedidoRequest.class)){
				AgregarQuitarProductoAPedidoRequest request = (AgregarQuitarProductoAPedidoRequest) arg;
				setearTimeStampEnBD(request.getIdPedido());
			}
			
			if(arg.getClass().equals(ConfirmarPedidoRequest.class)){
				ConfirmarPedidoRequest request = (ConfirmarPedidoRequest) arg;
				setearTimeStampEnBD(request.getIdPedido());
			}
		}
		return pointcut;			
	}
	
	/*
	 * Metodos auxiliares
	 */

	private void setearTimeStampEnBD(Integer id) {
		Pedido pedido = this.pedidoDao.obtenerPedidoPorId(id);
		this.setearTimeStamp(pedido);
		this.pedidoDao.guardar(pedido);
	}
	
	private void setearTimeStamp(Pedido p) {
		if(p.getFechaCreacion().equals(null)){
			p.setFechaCreacion(new DateTime());
		}
		p.setFechaModificacion(new DateTime());		
	}
	
}
