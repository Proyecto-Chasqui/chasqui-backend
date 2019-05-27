package chasqui.view.composer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import chasqui.model.Vendedor;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.renders.UsuarioRenderer;

@SuppressWarnings({"serial","deprecation"})
public class UsuariosActualesComposer extends GenericForwardComposer<Component> {
	
	private AnnotateDataBinder binder;
	private Listbox listboxUsuarios;
	private List<Vendedor>usuarios;
	private Window altaUsuarioWindow;
	private Window administracionWindow;
	private Vendedor usuarioSeleccionado;
	private Vendedor usuarioLogueado;
	private VendedorService vendedorService;
	private UsuarioService usuarioService;
	private Window estrategiasWindow;
	private ConfiguracionEstrategiasComposer composerEstrategias;
	private Component vcomp;
	private AdministracionComposer admComposer;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		this.vcomp = comp;
		Executions.getCurrent().getSession().setAttribute("usuariosActualesComposer",this);
		Executions.getCurrent().getSession().setAttribute("usuariosActualesComponent",comp);
		admComposer = (AdministracionComposer) Executions.getCurrent().getSession().getAttribute("administracionComposer");
		binder = new AnnotateDataBinder(comp);
		administracionWindow = (Window) findAdministracionWindow(comp);
		listboxUsuarios.setItemRenderer(new UsuarioRenderer((Window) this.self));
		conectarVentanas(administracionWindow);
		Events.sendEvent(Events.ON_USER,altaUsuarioWindow,this.self);
		composerEstrategias = (ConfiguracionEstrategiasComposer) Executions.getCurrent().getSession().getAttribute("configuracionEstrategiasComposer");
		comp.addEventListener(Events.ON_NOTIFY, new AccionEventListener(this));
		vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		usuarios = vendedorService.obtenerVendedores(); //TODO obtener todos los vendedores aunque no tengan configurado el monto minimom y la fecha! hacer servicio ad-hoc en vendedor service
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		usuarios.add(usuarioLogueado);
		binder.loadAll();
	}
	
	
	private Component findAdministracionWindow(Component comp) {
		if(comp.getParent() instanceof Window && comp.getParent().getId().equals("administracionWindow")){
			return comp.getParent();
		}
		return findAdministracionWindow(comp.getParent());
	}
	
	public Component conectarVentanas(Component c){
		for(Component child : c.getChildren()){
			if(child instanceof Window && child.getId().equals("altaUsuarioWindow")){
				altaUsuarioWindow = (Window) child;
			}else{
				conectarVentanas(child);
			}
			if(child instanceof Window && child.getId().equals("configuracionEstrategiasComercializacionWindow")){
				estrategiasWindow = (Window) child;
			}else{
				conectarVentanas(child);
			}
		}
		return null;
	}
	
	private Vendedor buscarVendededor(Vendedor v){
		for(Vendedor u : usuarios){
			if(u != null && u.getUsername().equals(v.getUsername())){
				return u;
			}
		}
		return null;
	}
	
	public void agregar(Vendedor v){
		Vendedor u = buscarVendededor(v);
		if(u == null){
			usuarios.add(v);			
		}		
		this.binder.loadAll();
	}
	
	public void editar(Vendedor u){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("accion", "editar");
		params.put("usuario", u);
		Events.sendEvent(Events.ON_USER,altaUsuarioWindow,params);
	}
	
	public void eliminar(final Vendedor u){
		
		Messagebox.show(Labels.getLabel("zk.message.eliminar.usuario",new String[]{u.getUsername()}),
				Labels.getLabel("zk.tittle.eliminar.usuario"), Messagebox.YES | Messagebox.NO,Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						switch ((Integer) event.getData()){
							case Messagebox.YES:
								Map<String,Object>params = new HashMap<String,Object>();
								params.put("accion", "eliminar");
								params.put("usuario", u);
								Events.sendEvent(Events.ON_USER,altaUsuarioWindow,params);
								usuarios.remove(u);
								usuarioService.eliminarUsuario(u);
								binder.loadAll();		
							
							case Messagebox.NO:
								return;
							
						}
					}	
				});
	}
	
	
	
	

	public List<Vendedor> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Vendedor> usuarios) {
		this.usuarios = usuarios;
	}
	public Vendedor getUsuarioSeleccionado() {
		return usuarioSeleccionado;
	}
	public void setUsuarioSeleccionado(Vendedor usuarioSeleccionado) {
		this.usuarioSeleccionado = usuarioSeleccionado;
	}

	public void editarEstrategias(Vendedor u) {
		this.composerEstrategias.editar(u);
	}


	public Window getAltaUsuarioWindow() {
		return altaUsuarioWindow;
	}


	public void setAltaUsuarioWindow(Window altaUsuarioWindow) {
		this.altaUsuarioWindow = altaUsuarioWindow;
	}

	public void onCargarStartUp(Vendedor v){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("vendedor", v);
		Window windowCargaStartUp = (Window) Executions.createComponents("/cargaStartup.zul", this.self, params);
		windowCargaStartUp.doModal();
	}
	
}

class AccionEventListener implements EventListener<Event>{
	
	private UsuariosActualesComposer composer;
	
	public AccionEventListener(UsuariosActualesComposer c){
		this.composer = c;
	}
	
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(Events.ON_NOTIFY)){
			@SuppressWarnings("unchecked")
			Map<String,Object>param = (Map<String,Object>)event.getData();
			if(param.get("accion").equals("editar")){
				composer.editar((Vendedor)param.get("usuario"));
				composer.agregar((Vendedor) param.get("usuario"));
			}
			if(param.get("accion").equals("eliminar")){
				composer.eliminar((Vendedor) param.get("usuario"));
				composer.agregar((Vendedor) param.get("usuario"));
			}
			if(param.get("accion").equals("editarEstrategias")){
				composer.editarEstrategias((Vendedor)param.get("usuario"));
			}			
			if(param.get("accion").equals("cargarStartUp")){
				this.composer.onCargarStartUp((Vendedor)param.get("usuario"));
			}			
		}		
	}
}
