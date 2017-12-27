package chasqui.exceptions;

/**
 * Esta excepción representa situaciones donde las configuraciones del vendedor
 * (fechas de cierre, monto mínimo, etc) no son coherentes o estan incompletas
 * 
 * @author huenu
 *
 */
public class ConfiguracionDeVendedorException extends Exception {

	public ConfiguracionDeVendedorException(Exception e) {
		super(e);
	}

	public ConfiguracionDeVendedorException(Exception e, String mensaje) {
		super(mensaje, e);
	}

	public ConfiguracionDeVendedorException(String mensaje) {
		super(mensaje);
	}

}
