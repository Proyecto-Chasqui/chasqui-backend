package chasqui.service.rest.impl;

import java.io.IOException;

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

import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioExistenteException;
import chasqui.model.Cliente;
import chasqui.security.Encrypter;
import chasqui.service.rest.request.IdInvitacionRequest;
import chasqui.service.rest.request.LoginRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.service.rest.request.SingUpRequestWithInvitation;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.LoginResponse;
import chasqui.service.rest.response.MailResponse;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.UsuarioService;


@Path("/sso")
public class SingInSingUpListener{

	
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	MailService mailService;
	@Autowired
	NotificacionService notificacionService;
	@Autowired
	private Encrypter encrypter;

	@POST
	@Path("/singIn")
	@Produces("application/json")
	public Response logIn(@Multipart(value="loginRequest", type="application/json")final String loginRequest ){
		
		try{
			LoginRequest request = toLoginRequest(loginRequest);
			Cliente c = usuarioService.loginCliente(request.getEmail(), request.getPassword());
			return toLoginResponse(c);
		}catch(Exception e){
			return Response.status(401).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/obtenerMailInvitado")
	@Produces("application/json")
	public Response obtenerMailDeClienteInvitado(@Multipart(value="idInvitacionRequest", type="application/json")final String idInvitacionRequest) throws Exception {
		
		try {
			IdInvitacionRequest request = toIdInvitacionRequest(idInvitacionRequest);
			String valueDecripted = encrypter.decryptURL(request.getIdInvitacion());
			return toMailResponse(notificacionService.obtenerNotificacionPorID(Integer.valueOf(valueDecripted)).getUsuarioDestino());
		} catch (Exception e) {
			return Response.status(406).entity(new ChasquiError("Invitacion inexistente")).build();
		}

	}

	private Response toMailResponse(String usuarioDestino) {
		MailResponse response = new MailResponse(usuarioDestino); 
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/singUp")
	@Produces("application/json")
	public Response singUp(@Multipart(value="singUpRequest",type="application/json") final String singUpRequest) {
		try{
			SingUpRequest request = toSingUpRequest(singUpRequest);
			Cliente c = usuarioService.crearCliente(request);
			return toLoginResponse(c);
		}catch(IOException e){
			return Response.status(406).entity(new ChasquiError("Debe completar todos los campos")).build();
		}catch (RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(UsuarioExistenteException e){
			return Response.status(409).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/singUp/invitation")
	@Produces("application/json")
	public Response singUpWithInvitation(@Multipart(value="singUpRequest",type="application/json") final String singUpRequest) {
		try{
			SingUpRequestWithInvitation request = toSingUpRequestWithInvitation(singUpRequest);
			Cliente c = usuarioService.crearCliente(request);
			return toLoginResponse(c);
		}catch(IOException e){
			return Response.status(406).entity(new ChasquiError("Debe completar todos los campos")).build();
		}catch (RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(UsuarioExistenteException e){
			return Response.status(409).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	
	@GET
	@Path("/resetPass/{email}")
	@Produces("application/json")
	public Response olvidoContraseña(@PathParam("email")final String email){
		
		try{
			mailService.enviarEmailRecuperoContraseñaCliente(email);
		}catch(UsuarioExistenteException e){
			return Response.status(406).entity(new ChasquiError("Email invalido")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
		return Response.ok().build();
	}



	private SingUpRequest toSingUpRequest(String request) throws IOException {
		SingUpRequest singUpRequest = new SingUpRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		singUpRequest = mapper.readValue(request, SingUpRequest.class);
		return singUpRequest;
	}

	private SingUpRequestWithInvitation toSingUpRequestWithInvitation(String request) throws IOException {
		SingUpRequestWithInvitation singUpRequestWithInvitation = new SingUpRequestWithInvitation();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		singUpRequestWithInvitation = mapper.readValue(request, SingUpRequestWithInvitation.class);
		return singUpRequestWithInvitation;
	}

	private IdInvitacionRequest toIdInvitacionRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		IdInvitacionRequest idInvitacionRequest = new IdInvitacionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		idInvitacionRequest = mapper.readValue(request, IdInvitacionRequest.class);
		
		return idInvitacionRequest;
	}
	
	private Response toLoginResponse(Cliente c) {
		LoginResponse response = new LoginResponse(c); 
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}



	private LoginRequest toLoginRequest(String request) throws IOException{
		LoginRequest loginRequest = new LoginRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		loginRequest = mapper.readValue(request, LoginRequest.class);
		return loginRequest;
	}
}
