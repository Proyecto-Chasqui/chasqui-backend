package chasqui.utils;


public class PrecioRender
{
  public static String precioToString(Double precio) {
		long precioLong = precio.longValue();
		if(precio == precioLong) {
				return "$"+String.format("%d", precioLong);
		} else {
				return "$"+String.format("%s",precio);
		}
	}
}