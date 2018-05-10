package chasqui.view.composer;

import java.awt.font.TextMeasurer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.cxf.common.util.StringUtils;
import org.joda.time.DateTime;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.dtos.PedidosGrupalesDTO;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.Imagen;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.FileSaver;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoColectivoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.services.interfaces.ZonaService;

@SuppressWarnings({"deprecation","unused"})
public class PuntosDeRetiroComposer extends GenericForwardComposer<Component>{
	
	private AnnotateDataBinder binder;
	private Textbox textNombrePuntoDeRetiro;
	private Textbox textCalle;
	private Textbox textAltura;
	private Textbox textCodigoPostal;
	private Textbox textDepartamento;
	private Textbox txtMensaje;
	private Textbox textLocalidad;
	private Button btnGuardar;
	private Button btnLimpiar;
	private Button guardar;
	private Button cancelar;
	private Button btnAgregar;
	private Datebox fechaCierrePedidos;
	private Textbox txtDescripcion;
	private Button btnHabilitar;
	private PuntoDeRetiro puntoDeRetiroSeleccionado;	
	private List<PuntoDeRetiro> puntosDeRetiro;
	private Vendedor usuario;
	private PuntoDeRetiroService puntoDeRetiroService;
	private UsuarioService usuarioService;
	private VendedorService vendedorService;
	private PedidoService pedidoService;
	private PedidoColectivoService pedidoColectivoService;
	private GrupoService grupoService;
	
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		puntoDeRetiroService = (PuntoDeRetiroService) SpringUtil.getBean("puntoDeRetiroService");
		pedidoService = (PedidoService) SpringUtil.getBean("pedidoService");
		pedidoColectivoService = (PedidoColectivoService) SpringUtil.getBean("pedidoColectivoService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
		grupoService = (GrupoService) SpringUtil.getBean("grupoService");
		puntosDeRetiro = vendedorService.obtenerPuntosDeRetiroDeVendedor(usuario.getId());
		binder = new AnnotateDataBinder(c);
		binder.loadAll();
	}

	public void onEliminarPuntoDeRetiro(Event e) throws VendedorInexistenteException{
		final List<Pedido> pedidosIndividuales = (List<Pedido>) pedidoService.obtenerPedidosIndividualesDeVendedor(usuario.getId(),null,null,Constantes.ESTADO_PEDIDO_CONFIRMADO,null,puntoDeRetiroSeleccionado.getId());
		final PedidosGrupalesDTO pedidosColectivos = this.obtenerPedidosColectivosDeVendedor(usuario.getId(),Constantes.ESTADO_PEDIDO_CONFIRMADO,puntoDeRetiroSeleccionado.getId());
		final Component c = this.self;
		if(pedidosIndividuales.isEmpty()) {
			Messagebox.show(
					"¿Está seguro que desea eliminar el punto de retiro "  + puntoDeRetiroSeleccionado.getNombre() +
					"?",
					"Pregunta",
		    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
		    		new String[] {"Aceptar","Cancelar"},
		    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

				public void onEvent(ClickEvent event) throws Exception {
					String edata= event.getData().toString();
					switch (edata){
					case "YES":
						puntosDeRetiro.remove(puntoDeRetiroSeleccionado);
						usuario.eliminarPuntoDeRetiro(puntoDeRetiroSeleccionado);
						puntoDeRetiroSeleccionado = null;
						usuarioService.guardarUsuario(usuario);
						binder.loadAll();
					case "NO":
					}
				}

				});
		}else {
			Messagebox.show("Hay pedidos confirmados que estan asociados al punto de entrega " + puntoDeRetiroSeleccionado.getNombre()+
					" ¿Está seguro que desea eliminaro?. (Recuerde que por mas que elimine el punto de retiro, todos los pedidos confirmados"
					+ " permaneceran asociados al mismo.)",
					"Advertencia",
		    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO,  Messagebox.Button.OK},
		    		new String[] {"Aceptar","Cancelar","Ver Pedidos"},
		    		Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>(){

				public void onEvent(ClickEvent event) throws Exception {
					Object edata= event.getData();
					String value = "NO";
					if(edata!=null) {
						value = edata.toString();
					}
					switch (value){
					case "YES":
						puntosDeRetiro.remove(puntoDeRetiroSeleccionado);
						usuario.eliminarPuntoDeRetiro(puntoDeRetiroSeleccionado);
						puntoDeRetiroSeleccionado = null;
						usuarioService.guardarUsuario(usuario);
						binder.loadAll();
						break;
					case "NO":
						binder.loadAll();
						break;
					case "OK":
						mostrarMensajeDePedidos(pedidosIndividuales,pedidosColectivos);
						event.stopPropagation();
						break;
					}
				}

				});
		}
		
		this.binder.loadAll();
	}
	
	//a falta de una referencia de pedidoColectivo a Grupo es necesario hacer esta
	//Funcionalidad de "weaving" en memoria. Evaluar costos de eficiencia.
	private PedidosGrupalesDTO obtenerPedidosColectivosDeVendedor(Integer id, String estadoPedido,
			Integer idPuntoRetiro) throws VendedorInexistenteException {
		PedidosGrupalesDTO pedidos = null;
		List<GrupoCC> grupos = grupoService.obtenerGruposDe(usuario.getId());
		List<PedidoColectivo> lpedidos = new ArrayList<PedidoColectivo>();
		Map<Integer,GrupoCC> pedidoGrupo= new HashMap<Integer,GrupoCC>();
		for(GrupoCC g: grupos) {
			List<PedidoColectivo> l = (List<PedidoColectivo>) pedidoColectivoService.obtenerPedidosColectivosDeVendedorDeGrupo(usuario.getId(), g.getId(), null, null, estadoPedido, null, idPuntoRetiro);
			lpedidos.addAll(l);
			completarMap(pedidoGrupo,l,g);
		}
		pedidos = new PedidosGrupalesDTO(lpedidos,pedidoGrupo);
		return pedidos;
	}

	private void completarMap(Map<Integer, GrupoCC> pedidoGrupo, List<PedidoColectivo> l, GrupoCC g) {
		for(PedidoColectivo p:l) {
			pedidoGrupo.put(p.getId(), g);
		}		
	}

	private void mostrarMensajeDePedidos(final List<Pedido> pedidos, final PedidosGrupalesDTO pedidosColectivos) {
		if(!pedidos.isEmpty()&&!pedidosColectivos.getPedidos().isEmpty()) {
			Messagebox.show("¿Que pedidos desea ver?",
					"Advertencia",
					new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
					new String[] {"Individual","Colectivo"},
					Messagebox.EXCLAMATION, null, new EventListener<ClickEvent>(){

				public void onEvent(ClickEvent event) throws Exception {
					Object edata= event.getData();
					String value = "OK";
					if(edata!=null) {
						value = edata.toString();
					}
					switch (value){
					case "YES":
						Map<String,Object>params = new HashMap<String,Object>();
						params.put("PedidosIndividuales", pedidos);
						Window previewPedidosIndividuales = (Window) Executions.createComponents("/previewPedidoIndividual.zul", null ,params );
						previewPedidosIndividuales.doModal();
						binder.loadAll();
						break;
					case "NO":
						Map<String,Object>paramsc = new HashMap<String,Object>();
						paramsc.put("PedidosColectivos", pedidosColectivos);
						Window previewPedidosColectivos = (Window) Executions.createComponents("/previewPedidosColectivos.zul", null ,paramsc );
						previewPedidosColectivos.doModal();
						break;
					case "OK":
						binder.loadAll();
						break;
				}				
			}

			});
		}else{
			if(!pedidos.isEmpty()) {
				Map<String,Object>params = new HashMap<String,Object>();
				params.put("PedidosIndividuales", pedidos);
				Window previewPedidosIndividuales = (Window) Executions.createComponents("/previewPedidoIndividual.zul", null ,params );
				previewPedidosIndividuales.doModal();
				binder.loadAll();
			}else{
				Map<String,Object>paramsc = new HashMap<String,Object>();
				paramsc.put("PedidosColectivos", pedidosColectivos);
				Window previewPedidosColectivos = (Window) Executions.createComponents("/previewPedidosColectivos.zul", null ,paramsc );
				previewPedidosColectivos.doModal();
			}
		}
	}
	
	
	public void onClick$btnLimpiar(){
		limpiarCampos();
		this.binder.loadAll();
	}
	
	private void limpiarCampos(){
		textNombrePuntoDeRetiro.setValue(null);
		textCalle.setValue(null);
		textAltura.setValue(null);
		textLocalidad.setValue(null);
		textCodigoPostal.setValue(null);
		textDepartamento.setValue(null);
		txtMensaje.setValue(null);
		puntoDeRetiroSeleccionado = null;
		btnLimpiar.setLabel("Limpiar campos");
		btnGuardar.setLabel("Agregar");
	}

	public void onClick$btnAgregar() throws VendedorInexistenteException{		
		validarPuntoDeRetiro();
		Messagebox.show(fraseDeContexto(),"Pregunta",Messagebox.YES | Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					btnAgregar.setLabel("Agregar");
					if(puntoDeRetiroSeleccionado == null){
						agregarPuntoDeRetiro(new PuntoDeRetiro(new Direccion()));
					}else{
						agregarPuntoDeRetiro(puntoDeRetiroSeleccionado);
					}		
					limpiarCampos();
					puntoDeRetiroSeleccionado = null;
					binder.loadAll();
				case Messagebox.NO:
					break;
				}
				
			}

			});		
	}
	
	private String fraseDeContexto(){
		String mensaje = "¿Está seguro que desea agregar un nuevo punto de retiro ?";
		String s = "";
		if(btnAgregar.getLabel().equals("Guardar Cambios")){
			mensaje =  "¿Está seguro que desea guardar los cambios para el punto de retiro "+ textNombrePuntoDeRetiro.getValue() +" ?";
		}
		return mensaje;
	}
	
	public void agregarPuntoDeRetiro(PuntoDeRetiro puntoDeRetiro) throws VendedorInexistenteException{
		btnLimpiar.setVisible(true);
		btnLimpiar.setLabel("Limpiar Campos");
		puntoDeRetiro.setNombre(textNombrePuntoDeRetiro.getValue());
		puntoDeRetiro.setCalle(textCalle.getValue());
		puntoDeRetiro.setAltura(Integer.parseInt(textAltura.getValue()));
		puntoDeRetiro.setLocalidad(textLocalidad.getValue());
		puntoDeRetiro.setCodigoPostal(textCodigoPostal.getValue());
		puntoDeRetiro.setDescripcion(txtMensaje.getValue());
		if(usuario.existePuntoDeRetiro(puntoDeRetiro)){
			puntoDeRetiroService.guardarPuntoDeRetiro(puntoDeRetiro);
			usuario = vendedorService.obtenerVendedor(usuario.getNombre());
		}else{
			puntoDeRetiro.setDisponible(true);
		    usuario.agregarPuntoDeRetiro(puntoDeRetiro);
		    puntosDeRetiro.add(puntoDeRetiro);
		    usuarioService.guardarUsuario(usuario);
		}	
	}	
	
	public void onHabilitarPuntoDeRetiro() throws VendedorInexistenteException{
		Messagebox.show("¿Seguro que desea "+palabraDeContexto()+" el punto de retiro " + puntoDeRetiroSeleccionado.getNombre() +" ?","Pregunta",Messagebox.YES | Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					puntoDeRetiroSeleccionado.setDisponible((! puntoDeRetiroSeleccionado.getDisponible()));
					puntoDeRetiroService.guardarPuntoDeRetiro(puntoDeRetiroSeleccionado);
					usuario = vendedorService.obtenerVendedorPorId(usuario.getId());
					binder.loadAll();
				case Messagebox.NO:
					break;
				}
				
			}

			});	
	}
	
	private String palabraDeContexto(){
		String s= "";
		if(puntoDeRetiroSeleccionado.getDisponible()){
			s = "deshabilitar";
		}else{
			s = "habilitar";
		}
		return s;
	}
	
	public void onEditarPuntoDeRetiro(){
		btnAgregar.setLabel("Guardar Cambios");
		btnLimpiar.setLabel("Cancelar");
		Integer paltura = puntoDeRetiroSeleccionado.getAltura();
		textNombrePuntoDeRetiro.setValue(puntoDeRetiroSeleccionado.getNombre());
		textCalle.setValue(puntoDeRetiroSeleccionado.getCalle());
		textAltura.setValue(paltura.toString());
		textLocalidad.setValue(puntoDeRetiroSeleccionado.getLocalidad());
		textCodigoPostal.setValue(puntoDeRetiroSeleccionado.getCodigoPostal());
		textDepartamento.setValue(puntoDeRetiroSeleccionado.getDepartamento());
		txtMensaje.setValue(puntoDeRetiroSeleccionado.getDescripcion());
		this.binder.loadAll();
	}
	
	
	private void validarPuntoDeRetiro(){
		if(StringUtils.isEmpty(textNombrePuntoDeRetiro.getValue())){
			throw new WrongValueException(textNombrePuntoDeRetiro,"El nombre no debe ser vacio");
		}
		if(estaEnLista(textNombrePuntoDeRetiro.getValue())){
			throw new WrongValueException(textNombrePuntoDeRetiro,"El punto de retiro: '"+textNombrePuntoDeRetiro.getValue()+ "' ya se encuentra en la lista" );
		}
		if(StringUtils.isEmpty(textCalle.getValue())){
			throw new WrongValueException(textCalle,"La calle no debe ser vacia");
		}
		
		if(StringUtils.isEmpty(textAltura.getValue().toString())){
			throw new WrongValueException(textAltura,"La altura no debe ser vacia");
		}
		
		if(StringUtils.isEmpty(textLocalidad.getValue().toString())){
			throw new WrongValueException(textLocalidad,"La localidad no debe ser vacia");
		}
				
		if(StringUtils.isEmpty(txtMensaje.getValue())){
			txtMensaje.setFocus(true);
			throw new WrongValueException(txtMensaje, "El mensaje no debe estar vacio");
		}
	}
	
	
	private boolean estaEnLista(String nombre){
		if(puntosDeRetiro != null){
			for(PuntoDeRetiro pr : puntosDeRetiro){
				if(pr.getNombre().equalsIgnoreCase(nombre) && ! estaEditando(pr.getId())){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean estaEditando(Integer id){
		return puntoDeRetiroSeleccionado != null && puntoDeRetiroSeleccionado.getId() == id;
	}

	public PuntoDeRetiro getPuntoDeRetiroSeleccionado() {
		return puntoDeRetiroSeleccionado;
	}

	public void setPuntoDeRetiroSeleccionado(PuntoDeRetiro puntoDeRetiroSeleccionado) {
		this.puntoDeRetiroSeleccionado = puntoDeRetiroSeleccionado;
	}

	public List<PuntoDeRetiro> getPuntosDeRetiro() {
		return puntosDeRetiro;
	}

	public void setPuntosDeRetiro(List<PuntoDeRetiro> puntosDeRetiro) {
		this.puntosDeRetiro = puntosDeRetiro;
	}
		
	
}

