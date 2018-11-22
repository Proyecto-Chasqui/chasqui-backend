package chasqui.view.composer;

import java.util.List;
import java.util.Map;

import org.apache.poi.sl.usermodel.TextBox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

@SuppressWarnings("serial")
public class ConfiguracionEstrategiasComposer extends GenericForwardComposer<Component>{
	
	private Window configuracionEstrategiasComercializacionWindow;
	private Button buttonGuardar;
	private AnnotateDataBinder binder;
	private Listbox listboxUsuarios;
	private List<Vendedor>usuarios;
	private Window altaUsuarioWindow;
	private Vendedor usuarioLogueado;
	private VendedorService vendedorService;
	private UsuarioService usuarioService;
	private Checkbox individual;
	private Checkbox colectiva; 
	private Checkbox nodos; 
	private Checkbox puntoDeEntrega; 
	private Checkbox entregaADomicilio;
	private UsuariosActualesComposer usuariosActualesComposer;
	private AdministracionComposer admComposer;
	private Component usuariosActualesComponent;
	private Window administracionWindow;
	private Window confwindow;
	private Component vcomp;
	private Vendedor usuarioSeleccionado;
	private Textbox textboxTiempoVencimiento;
	private Textbox urlMapa;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		Executions.getCurrent().getSession().setAttribute("configuracionEstrategiasComposer",this);
		
		binder = new AnnotateDataBinder(comp);
		vcomp = comp;
		comp.addEventListener(Events.ON_NOTIFY, new ConfAccionEventListener(this));
		vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		usuariosActualesComponent = (Component) Executions.getCurrent().getSession().getAttribute("usuariosActualesComponent");
		admComposer = (AdministracionComposer) Executions.getCurrent().getSession().getAttribute("administracionComposer");
		usuariosActualesComposer = (UsuariosActualesComposer) Executions.getCurrent().getSession().getAttribute("usuariosActualesComposer");
		usuarios = vendedorService.obtenerVendedores(); //TODO obtener todos los vendedores aunque no tengan configurado el monto minimom y la fecha! hacer servicio ad-hoc en vendedor service
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		usuarios.add(usuarioLogueado);
		binder.loadAll();
	}
	
	private Component findAdministracionWindow(Component comp) {
		if(comp.getParent() instanceof Window && comp.getParent().getId().equals("configuracionEstrategiasComercializacionWindow")){
			return comp.getParent();
		}
		return findAdministracionWindow(comp.getParent());
	}
	
	
	public void setCallerWindow(UsuariosActualesComposer c){
		usuariosActualesComposer = c;
	}
	
	public void editar(Vendedor vendedor) {
		 usuarioSeleccionado = vendedor;
		 EstrategiasDeComercializacion estrategias = usuarioSeleccionado.getEstrategiasUtilizadas();
		 this.setTiempoVencimiento(vendedor);
		 this.fillUrlMap(vendedor.getMapaZonas());
		 if(estrategias != null){
			 completarChecks(estrategias);
		 }else{
			 if(vendedor != null){
				 vendedor.setEstrategiasUtilizadas(new EstrategiasDeComercializacion());
				 estrategias = vendedor.getEstrategiasUtilizadas();
				 completarChecks(estrategias);
			 }
		 }
	}
	
	private void fillUrlMap(String mapaZonas) {
		if(mapaZonas != null) {
			this.urlMapa.setValue(mapaZonas);
		}else {
			this.urlMapa.setValue("");
		}
	}

	private void setTiempoVencimiento(Vendedor vendedor) {
		 if(vendedor.getTiempoVencimientoPedidos() != null){
			 String tiempo = vendedor.getTiempoVencimientoPedidos().toString();
			 textboxTiempoVencimiento.setValue(tiempo);
		 }else{
			 if(vendedor != null){
				 vendedor.setTiempoVencimientoPedidos(0);
				 textboxTiempoVencimiento.setValue(vendedor.getTiempoVencimientoPedidos().toString());
			 }
		 }
		
	}

	private void completarChecks(EstrategiasDeComercializacion estrategias){
		individual.setChecked(estrategias.isCompraIndividual());
	 	nodos.setChecked(estrategias.isNodos());
	 	colectiva.setChecked(estrategias.isGcc());
	 	puntoDeEntrega.setChecked(estrategias.isPuntoDeEntrega());
	 	entregaADomicilio.setChecked(estrategias.isSeleccionDeDireccionDelUsuario());
	}
	
	private void liberarChecks(){
		individual.setChecked(false);
		nodos.setChecked(false);
		colectiva.setChecked(false);
		puntoDeEntrega.setChecked(false);
		entregaADomicilio.setChecked(false);
	}
	
	public Window getConfwindow() {
		return confwindow;
	}

	public void setConfwindow(Window confwindow) {
		this.confwindow = confwindow;
	}

	public void onClick$buttonGuardar(){
		if(usuarioSeleccionado != null){
			EstrategiasDeComercializacion estrategias = usuarioSeleccionado.getEstrategiasUtilizadas();
		 	estrategias.setCompraIndividual(individual.isChecked());
		 	estrategias.setNodos(nodos.isChecked());
		 	estrategias.setGcc(colectiva.isChecked());
		 	estrategias.setPuntoDeEntrega(puntoDeEntrega.isChecked());
		 	estrategias.setSeleccionDeDireccionDelUsuario(entregaADomicilio.isChecked());
		 	Integer tiempo = Integer.parseInt(textboxTiempoVencimiento.getValue());
		 	usuarioSeleccionado.setTiempoVencimientoPedidos(tiempo);
		 	usuarioSeleccionado.setMapaZonas(urlMapa.getValue());
		 	usuarioService.guardarUsuario(usuarioSeleccionado);
		 	liberarChecks();
			EventListener evt = new EventListener() {
				public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
					if(evt.getName().equals("onOK")){
						
					}
				}
			};
			Messagebox.show("Se guardaron los cambios", "Confirmar",
					Messagebox.OK,
					Messagebox.QUESTION,
					evt
					);
		}else{
			EventListener evt = new EventListener() {
				public void onEvent(Event evt) throws EstadoPedidoIncorrectoException{
					if(evt.getName().equals("onOK")){
						
					}
				}
			};
			Messagebox.show("No hay vendedor seleccionado", "Confirmar",
					Messagebox.OK,
					Messagebox.QUESTION,
					evt
					);
		}
	}

	public Vendedor getUsuarioLogueado() {
		return usuarioLogueado;
	}

	public void setUsuarioLogueado(Vendedor usuarioLogueado) {
		this.usuarioLogueado = usuarioLogueado;
	}

	public Checkbox getIndividual() {
		return individual;
	}

	public void setIndividual(Checkbox individual) {
		this.individual = individual;
	}

	public Checkbox getColectiva() {
		return colectiva;
	}

	public void setColectiva(Checkbox colectiva) {
		this.colectiva = colectiva;
	}

	public Checkbox getNodos() {
		return nodos;
	}

	public void setNodos(Checkbox nodos) {
		this.nodos = nodos;
	}

	public Checkbox getPuntoDeEntrega() {
		return puntoDeEntrega;
	}

	public void setPuntoDeEntrega(Checkbox puntoDeEntrega) {
		this.puntoDeEntrega = puntoDeEntrega;
	}
	
	public void setConfWindow (Window w ){
		this.confwindow = w;
	}

	public Window getConfiguracionEstrategiasComercializacionWindow() {
		return configuracionEstrategiasComercializacionWindow;
	}

	public void setConfiguracionEstrategiasComercializacionWindow(
			Window configuracionEstrategiasComercializacionWindow) {
		this.configuracionEstrategiasComercializacionWindow = configuracionEstrategiasComercializacionWindow;
	}

	public Button getButtonGuardar() {
		return buttonGuardar;
	}

	public void setButtonGuardar(Button buttonGuardar) {
		this.buttonGuardar = buttonGuardar;
	}

	public Checkbox getEntregaADomicilio() {
		return entregaADomicilio;
	}

	public void setEntregaADomicilio(Checkbox entregaADomicilio) {
		this.entregaADomicilio = entregaADomicilio;
	}

	public Textbox getTiempoVencimiento() {
		return textboxTiempoVencimiento;
	}

	public void setTiempoVencimiento(Textbox tiempoVencimiento) {
		this.textboxTiempoVencimiento = tiempoVencimiento;
	}

	public Textbox getUrlMapa() {
		return urlMapa;
	}

	public void setUrlMapa(Textbox urlMapa) {
		this.urlMapa = urlMapa;
	}
}

class ConfAccionEventListener implements EventListener<Event>{
	
	ConfiguracionEstrategiasComposer composer;
	public ConfAccionEventListener(ConfiguracionEstrategiasComposer c){
		this.composer = c;
	}
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(Events.ON_USER)){
			@SuppressWarnings("unchecked")
			Map<String,Object>param = (Map<String,Object>)event.getData();
			if(param.get("accion").equals("editarEstrategias")){
				composer.setCallerWindow((UsuariosActualesComposer) param.get("configUserWindow"));
				composer.setConfWindow((Window) param.get("estrategiasWindow"));
				composer.editar((Vendedor)param.get("usuario"));
			}
		}
		
	}
	
}

