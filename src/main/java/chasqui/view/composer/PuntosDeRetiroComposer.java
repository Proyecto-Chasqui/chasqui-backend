package chasqui.view.composer;

import java.awt.font.TextMeasurer;
import java.util.Date;
import java.util.List;

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
import org.zkoss.zul.Textbox;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Direccion;
import chasqui.model.Imagen;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.services.impl.FileSaver;
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
	
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		usuario = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		puntoDeRetiroService = (PuntoDeRetiroService) SpringUtil.getBean("puntoDeRetiroService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
		puntosDeRetiro = vendedorService.obtenerPuntosDeRetiroDeVendedor(usuario.getId());
		binder = new AnnotateDataBinder(c);
		binder.loadAll();
	}

	public void onEliminarPuntoDeRetiro(){
		Messagebox.show("¿Está seguro que desea eliminar el punto de retiro " +puntoDeRetiroSeleccionado.getNombre()+" ?","Pregunta",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					puntosDeRetiro.remove(puntoDeRetiroSeleccionado);
					usuario.eliminarPuntoDeRetiro(puntoDeRetiroSeleccionado);
					puntoDeRetiroSeleccionado = null;
					usuarioService.guardarUsuario(usuario);
					binder.loadAll();
				case Messagebox.NO:
				}				
			}

			});
		this.binder.loadAll();
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
		btnLimpiar.setVisible(false);
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

