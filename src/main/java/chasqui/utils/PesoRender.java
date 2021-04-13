package chasqui.utils;


public class PesoRender
{
  public static String pesoConUnidad(Integer pesoEnGramos) {
		if(pesoEnGramos >= 1000) {
			Float pesoKg = pesoEnGramos/1000f;
			return String.format(pesoKg==Math.round(pesoKg) ? "%.0f" : "%s", pesoKg) + "kg";
		} else {
			return pesoEnGramos.toString() + "g";
		}
  }
}