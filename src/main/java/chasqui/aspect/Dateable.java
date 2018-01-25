package chasqui.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Vincula el aspecto que se encarga de actualizar el timestamps
 * 
 * @param fechaDeCreacion
 * @param fechaDeModificacion
 * @author David
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Dateable {
	
}
