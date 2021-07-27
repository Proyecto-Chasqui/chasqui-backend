package chasqui.service.rest.impl;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.ws.rs.POST;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Vendedor;
import chasqui.service.rest.request.ArrepentimientoRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.VendedorService;

@Service
@Path("/soporte")
public class SoporteListener {

	@Autowired
	MailService mailService;

	@Autowired
	VendedorService vendedorService;

	@Autowired
	String mailAdmin;

  @POST
	@Path("/arrepentimiento")
	@Produces("application/json")
	public Response recibirArrepentimiento(
		@Multipart(value = "solicitud", type = "application/json") String request) {
		try {

			String emailTo = mailAdmin;
			ArrepentimientoRequest solicitud = toArrepentimientoRequest(request);
			String nombreVendedor = solicitud.getNombreVendedor(); // nombre corto del vendedor
			
			// Busca email del vendedor si existe el nombreVendedor
			if(!nombreVendedor.isEmpty()) {
				Vendedor vendedor = this.buscarVendedor(nombreVendedor);
				if(vendedor != null) {
					emailTo = vendedor.getEmail();
					solicitud.setNombreVendedor(vendedor.getNombre());// setea el nombre completo del vendedor
				}
			}

			mailService.enviarEmailSolictudArrepentimiento(emailTo, solicitud);

			Map<String, Object> response = new HashMap<>();
			response.put("emailTo", emailTo);

			return Response.ok(response, MediaType.APPLICATION_JSON).build();

		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	private Vendedor buscarVendedor(String nombreCorto) {
		try {
			return vendedorService.obtenerVendedorPorNombreCorto(nombreCorto);
		} catch (VendedorInexistenteException e) {
			// escenario esperado, nada para hacer	
			return null;
		}
	}

	private ArrepentimientoRequest toArrepentimientoRequest(String request) throws IOException {
		ArrepentimientoRequest solicitud;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		solicitud = mapper.readValue(request, ArrepentimientoRequest.class);
		return solicitud;
	}
	
}
