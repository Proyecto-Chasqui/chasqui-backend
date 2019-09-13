package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.SolicitudCreacionNodoException;
import chasqui.exceptions.SolicitudCreacionNodoEnGestionExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.service.rest.request.ActualizarDomicilioRequest;
import chasqui.service.rest.request.CancelarSolicitudCreacionNodoRequest;
import chasqui.service.rest.request.EditarSolicitudCreacionNodoRequest;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.NodoSolicitudCreacionRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.NodoResponse;
import chasqui.service.rest.response.SolicitudCreacionNodoResponse;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

@Service
@Path("/nodo")
public class NodoListener {

	@Autowired
	NodoService nodoService;
	@Autowired
	VendedorService vendedorService;
	@Autowired
	UsuarioService usuarioService;
	
	@GET
	@Path("/all/{idVendedor : \\d+ }")
	@Produces("application/json")
	//TODO Deberian usarse el emailUsuario y el token en alguna parte, donde?
	public Response obtenerNodosDelVendedor(@PathParam("idVendedor")final Integer idVendedor){
		try{
			return Response.ok(toResponse(nodoService.obtenerNodosDelVendedor(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build(); 
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/alta")
	@Produces("application/json")
	public Response solicitudDeCreacionNodo(@Multipart(value = "solicitudCreacionNodoRequest", type = "application/json") final String solicitudCreacionNodoRequest) {
		NodoSolicitudCreacionRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toNodoCreacionRequest(solicitudCreacionNodoRequest);
			this.crearSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();			
		} catch (ConfiguracionDeVendedorException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();	
		} catch (SolicitudCreacionNodoEnGestionExistenteException e) {
			return Response.status(406).entity(new ChasquiError("Ya posee una solicitud de creación de nodo, solo se permite gestionar una por vez")).build();
		} catch (NodoYaExistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(406).entity(new ChasquiError("error interno")).build();
		}
	}

	private void crearSolicitudDeCreacionDeNodo(NodoSolicitudCreacionRequest request, String emailAdministrador) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException, UsuarioInexistenteException, SolicitudCreacionNodoEnGestionExistenteException, NodoYaExistenteException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		nodoService.crearSolicitudDeCreacionNodo(request.getIdVendedor(),
				cliente,
				request.getNombreNodo(),
				cliente.obtenerDireccionConId(request.getIdDomicilio()),
				request.getTipoNodo(),
				request.getBarrio(),
				request.getDescripcion());
		
	}

	private NodoSolicitudCreacionRequest toNodoCreacionRequest(String solicitudCreacionNodoRequest) throws JsonParseException, JsonMappingException, IOException {
		NodoSolicitudCreacionRequest request = new NodoSolicitudCreacionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(solicitudCreacionNodoRequest, NodoSolicitudCreacionRequest.class);
		return request;
	}
	
	@GET
	@Path("/solicitudesDeCreacion/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerSolicitudes(@PathParam("idVendedor") final Integer idVendedor) {

		String email = obtenerEmailDeContextoDeSeguridad();
		
			try {
				return Response.ok(toResponseSolicitudes(nodoService.obtenerSolicitudesDeCreacionDe(email,idVendedor)),
						MediaType.APPLICATION_JSON).build();
			} catch (UsuarioInexistenteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

	}
	
	@POST
	@Path("/editarSolicitudDeCreacion")
	@Produces("application/json")
	public Response editarSolicitudDeCreacion(@Multipart(value = "editarSolicitudDeCreacion", type = "application/json") final String editarSolicitudDeCreacion) {
		EditarSolicitudCreacionNodoRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toEditNodoCreacionRequest(editarSolicitudDeCreacion);
			this.editarSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (SolicitudCreacionNodoException e) {
			return Response.status(406).entity(new ChasquiError("La solicitud no esta en etapa de gestión")).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(406).entity(new ChasquiError("La dirección seleccionada no pertenece al usuario")).build();
		} catch (NodoYaExistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError("Error desconocido")).build();
		}
	}
	
	@POST
	@Path("/cancelarSolicitudDeCreacion")
	@Produces("application/json")
	public Response cancelarSolicitudDeCreacion(@Multipart(value = "cancelarSolicitudDeCreacion", type = "application/json") final String cancelarSolicitudDeCreacion) {
		CancelarSolicitudCreacionNodoRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toCancelarNodoCreacionRequest(cancelarSolicitudDeCreacion);
			this.cancelarSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (SolicitudCreacionNodoException e) {
			return Response.status(406).entity(new ChasquiError("La solicitud no esta en etapa de gestión")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError("Error desconocido")).build();
		}
	}


	private void cancelarSolicitudDeCreacionDeNodo(CancelarSolicitudCreacionNodoRequest request,
			String emailAdministrador) throws UsuarioInexistenteException, SolicitudCreacionNodoException, VendedorInexistenteException, ConfiguracionDeVendedorException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		nodoService.cancelarSolicitudDeCreacionNodo(request.getIdSolicitud(), request.getIdVendedor(), cliente.getId());		
	}


	private void editarSolicitudDeCreacionDeNodo(EditarSolicitudCreacionNodoRequest request, String emailAdministrador) throws UsuarioInexistenteException, SolicitudCreacionNodoException, DireccionesInexistentes, NodoYaExistenteException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		validarDireccion(cliente, request.getIdDomicilio());
		nodoService.editarSolicitudDeCreacionNodo(request.getIdVendedor(),
				cliente,
				request.getIdSolicitud(),
				request.getNombreNodo(),
				cliente.obtenerDireccionConId(request.getIdDomicilio()),
				request.getTipoNodo(),
				request.getBarrio(),
				request.getDescripcion());
		
	}
	
	
	private void validarDireccion(Cliente cliente, Integer idDomicilio) throws DireccionesInexistentes {
		if(!cliente.contieneDireccion(idDomicilio)) {
			throw new DireccionesInexistentes();
		}		
		
	}

	private List<SolicitudCreacionNodoResponse> toResponseSolicitudes(List<SolicitudCreacionNodo> solicitudes) {
		List<SolicitudCreacionNodoResponse> response = new ArrayList<SolicitudCreacionNodoResponse>();
		//TODO seguir desde aca la prox. Solucionar Error serializing the response, please check the server logs, response class : ArrayList.
		for(SolicitudCreacionNodo solicitud : solicitudes){
			response.add(new SolicitudCreacionNodoResponse(solicitud));
		}
		return response;
	}
	
	private List<NodoResponse> toResponse(List<Nodo> nodos) {
		List<NodoResponse> response = new ArrayList<NodoResponse>();
		//TODO seguir desde aca la prox. Solucionar Error serializing the response, please check the server logs, response class : ArrayList.
		for(Nodo nodo : nodos){
			response.add(new NodoResponse(nodo));
		}
		return response;
	}
	
	protected String obtenerEmailDeContextoDeSeguridad() {
		return SecurityContextHolder.getContext().getAuthentication().getName();

	}
	
	private CancelarSolicitudCreacionNodoRequest toCancelarNodoCreacionRequest(String cancelarSolicitudDeCreacion) throws JsonParseException, JsonMappingException, IOException {
		CancelarSolicitudCreacionNodoRequest request = new CancelarSolicitudCreacionNodoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(cancelarSolicitudDeCreacion, CancelarSolicitudCreacionNodoRequest.class);
		return request;
	}

	private EditarSolicitudCreacionNodoRequest toEditNodoCreacionRequest(String editarSolicitudDeCreacion) throws JsonParseException, JsonMappingException, IOException {
		EditarSolicitudCreacionNodoRequest request = new EditarSolicitudCreacionNodoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editarSolicitudDeCreacion, EditarSolicitudCreacionNodoRequest.class);
		return request;
	}
}
