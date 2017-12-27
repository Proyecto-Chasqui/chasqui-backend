package chasqui.services.interfaces;

import java.util.List;

import com.vividsolutions.jts.io.ParseException;

import chasqui.model.Cliente;
import chasqui.model.GrupoCC;

public interface GeoService {

	void crearZonasDesdeGeoJson(String absolutePath);

	void crearZonasDesdeArchivoWKT(String absolutePath, Integer idVendedor);

	List<Cliente> obtenerClientesCercanos(String email) throws ParseException;

	List<GrupoCC> obtenerGCC_CercanosACliente(String email) throws ParseException;

}
