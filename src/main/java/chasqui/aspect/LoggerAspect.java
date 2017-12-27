package chasqui.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggerAspect {

	private static final Logger logger = Logger.getLogger(LoggerAspect.class);
	
	
//	@Around("@annotation(org.springframework.transaction.annotation.Transactional)")
//	@Around("@annotation(chasqui.aspect.Auditada)")
	@Around("execution(public * @Auditada *.*(..))")
	public Object logear(ProceedingJoinPoint pointcut)throws Throwable{
		try{
			return pointcut.proceed();			
		}catch(Throwable t){
			logger.info("Ha ocurrido un error ejecutando el metodo: " + pointcut.getSignature() + " Con parametros: "
					+ toStringObjects(pointcut.getArgs()));
			logger.error(t.getMessage(),t);
			throw t;
		}
	}
	
	private String toStringObjects(Object[] args){
		if(args != null){
			String params = "[";
			for(int i=0;i<args.length;i++){
				params += String.valueOf(args[i]) + ",";
			}
			return params += "]";
		}
		return "";
	}
	
	
}
