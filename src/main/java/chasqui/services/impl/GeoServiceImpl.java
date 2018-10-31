package chasqui.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zkplus.spring.SpringUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import chasqui.dao.GrupoDAO;
import chasqui.dao.UsuarioDAO;
import chasqui.dao.VendedorDAO;
import chasqui.dao.ZonaDAO;
import chasqui.exceptions.ArchivoConFormatoIncorrectoException;
import chasqui.exceptions.ErrorDeParseoDeCoordenadasException;
import chasqui.exceptions.ErrorZona;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.service.rest.request.EliminarZonaRequest;
import chasqui.service.rest.request.ZonaRequest;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.utils.ErrorCodes;
import chasqui.utils.SphericalMercator;
import chasqui.utils.TokenGenerator;


public class GeoServiceImpl implements GeoService{
	
	@Autowired ZonaDAO zonaDAO;
	@Autowired VendedorDAO vendedorDAO;
	@Autowired UsuarioDAO usuarioDAO;
	@Autowired GrupoDAO grupoDAO;
	@Autowired UsuarioService usuarioService;
	GeometryFactory geometryFactory = new GeometryFactory();
	@Autowired
	TokenGenerator tokenGenerator;
	
	// Asume que cada poligono esta definido en una linea, y son todos poligonos.
	// El numeroZona es para todas las zonas la misma, solo es para la instanciacion de zonas, 
	// por la limitacion de WKT no se puede pasar el nombre de la zona en el archivo.
	@Override
	public void crearZonasDesdeArchivoWKT(String absolutePath, Integer idVendedor){
		try (BufferedReader file = new BufferedReader(new FileReader(absolutePath))) {
			String line;
			Integer numeroZona = 1;
			List<Zona> zonas = new ArrayList<Zona>();
			List<String>nombresDeZonasQueSeSolapan= new ArrayList<String>();
			while((line = file.readLine())!=null){
				 parsearAZona(line, ("zona-" + numeroZona.toString()), zonas,nombresDeZonasQueSeSolapan,idVendedor);
				 numeroZona ++;
			}
			guardarZonas(nombresDeZonasQueSeSolapan,zonas,idVendedor);
		} catch (IOException e) {
			new ArchivoConFormatoIncorrectoException("El archivo es incorrecto o no se encuentra en la ruta especificada: /n" + " Path: " + absolutePath);
		}
	}
	
	@Override
	public void crearGuardarZona(ZonaRequest request) throws Exception {
		
		try {
			validar(request);
			ArrayList<ArrayList<Double>> coordenadas = (ArrayList<ArrayList<Double>>) request.getCoordenadas();		
			Polygon poly = crearPolygon(coordenadas);
			Zona z;
			if(request.getId()!=null) {
				z = zonaDAO.obtenerZonaPorId(request.getId());
			}else {
				z = new Zona();
				z.setIdVendedor(request.getIdVendedor());
			}
			z.setGeoArea(poly);
			z.setNombre(request.getNombre());
			z.setFechaCierrePedidos(request.getFechaCierre());
			z.setDescripcion(request.getMensaje());
			if(!seSolapaCon(z,z.getId(),zonaDAO.obtenerZonas(request.getIdVendedor()))) {
				zonaDAO.guardar(z);				
			}else {
				throw new ErrorZona(new ErrorCodes().ez008);
			}
			request.setId(z.getId());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}			
		
		
	}

	private void validar(ZonaRequest request) {
		
		if(! tokenGenerator.tokenActivo(request.getToken())) {
			throw new ErrorZona("1");
		}
		
		if(request.getCoordenadas() == null) {
			throw new ErrorZona("2");
		}
		
		if(request.getFechaCierre().isBeforeNow()) {
			throw new ErrorZona("3");
		}
		
		if(request.getIdVendedor() == null) {
			throw new ErrorZona("4");
		}
		
		if(request.getMensaje().isEmpty()) {
			throw new ErrorZona("5");
		}
		
		if(request.getNombre().isEmpty()) {
			throw new ErrorZona("6");
		}
		//verifica que ante una zona nueva no exista el nombre en las zonas existentes.
		if( request.getId() == null && existeZonaConNombre(request) ) {
			throw new ErrorZona("7" + request.getNombre());
		}
		//verifica que en caso de cambio de nombre a una zona existente, ese cambio de nombre no este asignado a otra zona.
		if(!laZonaConIdTieneElNombre(request)) {
			if(existeZonaConNombre(request)){
				throw new ErrorZona("8" + request.getNombre());
			}
		}
		
	}

	private boolean laZonaConIdTieneElNombre(ZonaRequest request) {
		boolean ret=false;
		if(request.getId() != null) {
			ret = zonaDAO.obtenerZonaPorId(request.getId()).getNombre().equals(request.getNombre());
		}
		return ret;
	}

	private boolean existeZonaConNombre(ZonaRequest request) {
		return zonaDAO.obtenerZonaPorNombre(request.getNombre(), request.getIdVendedor()) != null;
	}

	private Polygon crearPolygon(ArrayList<ArrayList<Double>> coordinates) throws ParseException {
		String zonaEnformatoWKT = parsearAWkt("Polygon",coordinates);
		Polygon geoArea = (Polygon) new WKTReader().read(zonaEnformatoWKT);
		return geoArea;
	}

	//Asume que el archivo geoJson tiene un campo descripcion (tentativo) con el nombre del vendedor.
	//Basado en la respuesta de http://umap.openstreetmap.fr/es/ al exportar.
	@Override
	public void crearZonasDesdeGeoJson(String absolutePath){
		JSONParser parser = new JSONParser();
		try {			
			JSONObject obj = (JSONObject) parser.parse(new FileReader(absolutePath));
            JSONArray jsonObjects = (JSONArray) obj.get("features");
            Object[] jsons = jsonObjects.toArray();
            Map<String,String> zonasEnWKT = new HashMap<String,String>();
            String nombreVendedor = null;
            Integer idVendedor = null;
            for(int i = 0; i<jsons.length; i++){
            	JSONObject jsonval = (JSONObject) jsons[i];     	
            	ArrayList<ArrayList<ArrayList<Double>>> coordenadas = (ArrayList<ArrayList<ArrayList<Double>>>) ((JSONObject) jsonval.get("geometry")).get("coordinates");
            	String nombreZona = (String) ((JSONObject) jsonval.get("properties")).get("name");
            	String tipo = (String) ((JSONObject) jsonval.get("geometry")).get("type");
            	if(nombreVendedor == null){
            		nombreVendedor = (String) ((JSONObject) jsonval.get("properties")).get("description");
            		idVendedor = vendedorDAO.obtenerVendedor(nombreVendedor).getId();
            	};
            	
            	String zonaEnformatoWKT = parsearAWkt(tipo,coordenadas.get(0));
            	zonasEnWKT.put(nombreZona, zonaEnformatoWKT);
            }
           crearZonas(zonasEnWKT,idVendedor);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//primero latitud luego longitud
	//LONGITUD alto
	//LATITUD largo
	//el email es para discrimiar al cliente que consulta, si no el cliente que consulta
	//tambien es parte de la respuesta. (analizar)
	@Override
	public List<Cliente> obtenerClientesCercanos(String email) throws ParseException{
	   Geometry area = crearAreaSobreCliente(email);
	   return usuarioDAO.obtenerClientesCercanos(area,email);
	}
	
	@Override
	public List<GrupoCC> obtenerGCC_CercanosACliente(String email) throws ParseException{		
		Geometry area = crearAreaSobreCliente(email);
		return grupoDAO.obtenerGruposEnUnArea(area);
	}
	
	public Zona calcularZonaDePertenencia(Point punto,Integer idVendedor) {
		List<Zona> zonas = zonaDAO.obtenerZonas(idVendedor);
		
		return null;
		
	}
	
	/*-------------------------------------------
	 *  		OPERACIONES AUXILIARES
	 *-------------------------------------------*/
	private Geometry crearAreaSobreCliente(String email) throws ParseException{
		Cliente cliente = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(email);
		usuarioDAO.inicializarDirecciones(cliente);
		Direccion dir = cliente.obtenerDireccionPredeterminada();
		return crearArea(dir.getLatitud(),dir.getLongitud(),2.0);
	}
	
	private Geometry crearArea(String latitud, String longitud,Double radiokm) throws ParseException {
		Geometry area;
		Point point = new GeometryFactory().createPoint(new Coordinate(Double.parseDouble(latitud),Double.parseDouble(longitud)));
		double radio = kmsARadio(radiokm);
		area = createCircle(point,radio,32);
		return area;
	}
	
	private double kmsARadio(double km){
		double radio1enkm = 6.949682915284921;
		double radio = 1.0;
		return (radio*km)/radio1enkm;
	} 
	
	private static Geometry createCircle(Point p0, double radius, int nSides){
	  GeometricShapeFactory shape = new GeometricShapeFactory(new GeometryFactory());
	  shape.setCentre(p0.getCoordinate());
	  shape.setSize(radius / 16);
	  shape.setNumPoints(nSides);
	  return shape.createCircle();
	}
	

	//map<nombreZona,Poligono WKT>
	private void crearZonas(Map<String, String> zonasEnWKT, Integer idVendedor){
		List<Zona> zonas = new ArrayList<Zona>();
		List<String>nombresDeZonasQueSeSolapan= new ArrayList<String>();
		Set<String> nombresDeZonas = zonasEnWKT.keySet();
		for(String nombreZona:nombresDeZonas){
			String zonaEnWKT = zonasEnWKT.get(nombreZona);
			parsearAZona(zonaEnWKT, nombreZona, zonas,nombresDeZonasQueSeSolapan,idVendedor);
		}
		guardarZonas(nombresDeZonasQueSeSolapan,zonas,idVendedor);		
	}	

	private String parsearAWkt(String tipo, ArrayList<ArrayList<Double>> arrayList) {
		String wktformat = "";
		wktformat = wktformat + tipo.toUpperCase();
		wktformat = wktformat + "(( ";
		int i = 0;
		int sizecoordenadas = arrayList.size();
		for(ArrayList<Double> coord : arrayList){
			if(sizecoordenadas-1 > i){
			wktformat = wktformat + coord.get(0).toString() + " ";
			wktformat = wktformat + coord.get(1).toString() + ", ";
			}else{
				wktformat = wktformat + coord.get(0).toString() + " ";
				wktformat = wktformat + coord.get(1).toString() + "))";
			}
			i++;
		}
		return wktformat;
	}

	private String mensajeDeErrorZonasSolapadas(List<String> zonasQueSeSolapan) {
		String msj = "Las siguientes zonas se solapan con una o mas zonas: /n ";
		
		for(String nombre : zonasQueSeSolapan){
			msj = msj + nombre +"/n";
		}
		return msj;
	}
	//Asume que la lineaEnWKT es para crear un Polygon
	private void parsearAZona(String lineaEnWKT, String nombreZona,List<Zona> zonas,List<String> nombreZonasSolapadas,Integer idVendeor){
		try {
			Polygon geoArea = (Polygon) new WKTReader().read(lineaEnWKT);
			new DateTime();			
			Zona zona = new Zona(nombreZona,DateTime.now(),"");
			zona.setGeoArea(geoArea);
			zona.setIdVendedor(idVendeor);
			validarQueNoSeSolapa(zona,zonas,nombreZona,nombreZonasSolapadas);
		} catch (ParseException e) {
			new ErrorDeParseoDeCoordenadasException("La coordenada es invalida no corresponde a un poligono, revise si la primer coordenada es tambien la ultima:/n " + lineaEnWKT);
		}
	}
	
	private void validarQueNoSeSolapa(Zona zona, List<Zona> zonas, String nombreZona, List<String> nombreZonasSolapadas) {
		if(!seSolapa(zona, zonas)){
			zonas.add(zona);
		}else{
			nombreZonasSolapadas.add(nombreZona);
		}
	}

	private Boolean seSolapa(Zona zona, List<Zona>zonas){
		Boolean seSolapa = false;
		for(Zona vzona : zonas){
			if(!seSolapa){
				seSolapa = zona.getGeoArea().overlaps(vzona.getGeoArea());
			}
		}
		return seSolapa;
	}
	
	private Boolean seSolapaCon(Zona zona, Integer idZona, List<Zona>zonas) throws Exception{
		Boolean seSolapa = false;
		Double area = 0.0;
		Double tolerancia = (double) 4000;
		for(Zona vzona : zonas){
			if(!seSolapa && vzona.getGeoArea() != null && vzona.getId() != idZona){
				Geometry geom = zona.getGeoArea().intersection(vzona.getGeoArea());
				double areacalculada = 0.0;
				for(int i=0; i<geom.getNumGeometries();i++) {
					areacalculada = areacalculada + GeoServiceImpl.getArea(geom.getGeometryN(i),true);
				}
				if(area < areacalculada) {
					area = areacalculada;
				}
				if(area > tolerancia) {
					seSolapa = true;
				}
			}
		}
		
		return seSolapa;
	}
	
	public static double getArea(Geometry geom, Boolean inMeters) throws Exception
	{
		double retArea = 0.0;
		if (inMeters) {
			if (geom instanceof Polygon)
			{
				Polygon poly = (Polygon) geom;
				double area = Math.abs(getSignedArea(poly.getExteriorRing().getCoordinateSequence()));
				
				for (int i = 0; i < poly.getNumInteriorRing(); i++) {
					LineString hole =	poly.getInteriorRingN(i);
					area -= Math.abs(getSignedArea(hole.getCoordinateSequence()));
				}
				retArea = area;
			}
			else if (geom instanceof LineString)
			{
				LineString ring = (LineString)geom;
				retArea = getSignedArea(ring.getCoordinateSequence());
			}
		} else {
			retArea = geom.getArea();
		}
		return retArea;
	}
	
	private static double getSignedArea(CoordinateSequence ring)
	{
		int n = ring.size();
		if (n < 3)
			return 0.0;
		Coordinate p0 = new Coordinate();
		Coordinate p1 = new Coordinate();
		Coordinate p2 = new Coordinate();
		getMercatorCoordinate(ring, 0, p1);
		getMercatorCoordinate(ring, 1, p2);
		double x0 = p1.x;
		p2.x -= x0;
		double sum = 0.0;
		for (int i = 1; i < n - 1; i++) {
			p0.y = p1.y;
			p1.x = p2.x;
			p1.y = p2.y;
			getMercatorCoordinate(ring, i + 1, p2);
			p2.x -= x0;
			sum += p1.x * (p0.y - p2.y);
		}
		return sum / 2.0;
	}
	
	private static void getMercatorCoordinate(CoordinateSequence seq, int index, Coordinate coord)
	{
		seq.getCoordinate(index, coord);
		coord.x = SphericalMercator.lonToX(coord.x);
		coord.y = SphericalMercator.latToY(coord.y);
	}

	
	private void guardarZonas(List<String> nombresDeZonasQueSeSolapan, List<Zona> zonas, Integer idVendedor) {
		if(!nombresDeZonasQueSeSolapan.isEmpty()){				
			new ErrorDeParseoDeCoordenadasException(this.mensajeDeErrorZonasSolapadas(nombresDeZonasQueSeSolapan)); 
		}else{
			//TODO:validar si hay zonas con el mismo nombre antes de guardar
			//o generar en el servicio y/o DAO la query que lo haga por zona
			for(Zona zona: zonas){
				zonaDAO.guardar(zona);
			}
		}
		
	}

	@Override
	public void eliminarZona(EliminarZonaRequest request) {
		Vendedor usuario = this.usuarioDAO.obtenerVendedorPorID(request.getIdVendedor());
		Zona zona = this.zonaDAO.obtenerZonaPorId(request.getId());
		usuarioService.inicializarListasDe(usuario);
		usuario.eliminarZona(zona);
		usuarioService.guardarUsuario(usuario);
	}	
	
}


