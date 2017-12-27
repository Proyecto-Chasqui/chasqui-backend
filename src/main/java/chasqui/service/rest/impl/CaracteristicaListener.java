package chasqui.service.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.model.Caracteristica;
import chasqui.model.CaracteristicaProductor;
import chasqui.service.rest.response.CaracteristicaResponse;
import chasqui.service.rest.response.ChasquiError;
import chasqui.services.interfaces.CaracteristicaService;
import chasqui.services.interfaces.ICaracteristica;

@Service
@Path("/medalla")
public class CaracteristicaListener {
	
	
	@Autowired 
	CaracteristicaService caracteristicaService;
	
	
	
	@GET
	@Path("/all")
	@Produces("application/json")
	public Response obtenerTodasLasMedallas(){
		try{
			
			return Response.ok(
					toResponse(caracteristicaService.buscarCaracteristicasProducto(),
							caracteristicaService.buscarCaracteristicasProductor()),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("producto/all")
	@Produces("application/json")
	public Response obtenerMedallasProducto(){
		try{

			return Response.ok(
					toResponse(caracteristicaService.buscarCaracteristicasProducto(),new ArrayList<CaracteristicaProductor>()),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@GET
	@Path("productor/all")
	@Produces("application/json")
	public Response obtenerMedallasProductor(){
		try{
			return Response.ok(
					toResponse(new ArrayList<Caracteristica>(),caracteristicaService.buscarCaracteristicasProductor()),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	private List<CaracteristicaResponse> toResponse(List<Caracteristica> buscarCaracteristicasProducto,
			List<CaracteristicaProductor> buscarCaracteristicasProductor) {
		List<CaracteristicaResponse>response = new ArrayList<CaracteristicaResponse>();
		List<ICaracteristica> icss = new ArrayList<ICaracteristica>();
		icss.addAll(buscarCaracteristicasProductor);
		icss.addAll(buscarCaracteristicasProducto);
		for(ICaracteristica c :icss){
			response.add(new CaracteristicaResponse(c));
		}		
		return response;
	}

}
