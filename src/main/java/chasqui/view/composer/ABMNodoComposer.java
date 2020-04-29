package chasqui.view.composer;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.zkforge.ckez.CKeditor;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.RefreshListener;
import chasqui.view.genericEvents.Refresher;
@Deprecated
public class ABMNodoComposer extends GenericForwardComposer implements Refresher {

	private Vendedor vendedor;
	private NodoService nodoService;
	private UsuarioService usuarioService;
	private Nodo nodo;
	private boolean existe;
	private AnnotateDataBinder binder;
	public final static String CLAVE_NODO = "NODO";

	private Textbox txtAlias;
	private Textbox txtEmailAdmin;
	private Textbox txtLocalidad;
	private Textbox txtCalle;
	private Intbox intBoxAltura;
	private Textbox txtTelefono;
	private ListModelList<Direccion> direcciones;
	private Direccion direccionSeleccionada;
	private Combobox comboDirecciones;
	private Cliente cliente;
	private CKeditor txtDescripcion;
	private boolean existeUsuario;
	private Direccion direccionCreadaParaNodo;

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		direccionCreadaParaNodo = null;
		cliente = new Cliente();
		direcciones = new ListModelList<Direccion>();
		vendedor = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		Executions.getCurrent().getSession().setAttribute("direccionNodo", direccionCreadaParaNodo);
		Integer edicion = (Integer) Executions.getCurrent().getArg().get("accion");

		nodoService = (NodoService) SpringUtil.getBean("nodoService");
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");

		nodo = (Nodo) Executions.getCurrent().getArg().get(CLAVE_NODO);

		comp.addEventListener(Events.ON_NOTIFY, new RefreshListener<Refresher>(this));// ?

		if (nodo != null && Constantes.VENTANA_MODO_LECTURA.equals(edicion)) {
			inicializarModoLectura();
		} else if (nodo != null && Constantes.VENTANA_MODO_EDICION.equals(edicion)) {
			llenarCampos();
			existe = true;
		}
		this.deshabilitarCampos();
		binder = new AnnotateDataBinder(comp);
	}
	
    public void onOK$enter() throws UsuarioInexistenteException {
    	this.limpiarcampos();
        this.onEnter();
    }
    

	
	public void onEnter() throws UsuarioInexistenteException{
		String mail = txtEmailAdmin.getValue();
		if(this.usuarioService.existeUsuarioCon(mail)){
			Cliente user =(Cliente) this.usuarioService.obtenerUsuarioPorEmail(mail);
			this.usuarioService.inicializarDirecciones(user);
			if(user.obtenerDireccionPredeterminada() != null){
				comboDirecciones.setDisabled(false);
				direcciones.clear();
				direcciones.addAll(user.getDireccionesAlternativas());
				if(direccionCreadaParaNodo != null){
					direcciones.add(direccionCreadaParaNodo);
				}
				comboDirecciones.setValue("Seleccione una dirección");
				direccionSeleccionada=null;
				//completarDireccion(user);
			}else{
				limpiarcampos();
				this.agregarDireccionACombo();
				Messagebox.show("El cliente no posee ninguna dirección asociada, puede crear y asignar una dirección al nodo.", "Aviso", Messagebox.OK, Messagebox.INFORMATION);
			}
		}else{
			limpiarcampos();
			this.agregarDireccionACombo();
			Messagebox.show("El cliente con el email: " + mail + " no existe, se le enviara un email de registro a chasqui al finalizar el registro del nodo", "Aviso", Messagebox.OK, Messagebox.INFORMATION);
		};
	}
	
	public void onClick$buttonConfirmarUsuario() throws UsuarioInexistenteException{
		this.onEnter();
	}

	public void onCompletarDireccionAlSeleccionar() throws UsuarioInexistenteException{
		String localidad = direccionSeleccionada.getLocalidad();
		String calle = direccionSeleccionada.getCalle();
		Integer altura = direccionSeleccionada.getAltura();
		String email = txtEmailAdmin.getValue();
		if(this.usuarioService.existeUsuarioCon(email)){
			Cliente user =(Cliente) this.usuarioService.obtenerUsuarioPorEmail(email);
			txtTelefono.setValue(user.getTelefonoFijo());
		}
		txtLocalidad.setValue(localidad);
		txtCalle.setValue(calle);
		intBoxAltura.setValue(altura);
		
	}
	
	public void onBorrarDireccionAlEditarMail() throws UsuarioInexistenteException{
		this.limpiarcampos();
		try{
			obtenerDireccionesDeCliente(txtEmailAdmin.getValue());
		}catch (UsuarioInexistenteException e){
			agregarDireccionACombo();
		}
		
	}
	
	public void agregarDireccionACombo(){
		direccionCreadaParaNodo = (Direccion) Executions.getCurrent().getSession().getAttribute("direccionNodo");
		if(direccionCreadaParaNodo != null){
			comboDirecciones.setDisabled(false);
			direccionSeleccionada = null;
			direcciones.add(direccionCreadaParaNodo);
			comboDirecciones.setValue("Seleccione una direccion");
		}
	}
	
	private void completarDireccion(Cliente user){
		String localidad = user.obtenerDireccionPredeterminada().getLocalidad();
		String calle = user.obtenerDireccionPredeterminada().getCalle();
		Integer altura = user.obtenerDireccionPredeterminada().getAltura();
		String telefono = user.getTelefonoFijo();
		txtLocalidad.setValue(localidad);
		txtCalle.setValue(calle);
		intBoxAltura.setValue(altura);
		txtTelefono.setValue(telefono);
	}
	
	public void limpiarcampos(){
		comboDirecciones.setDisabled(true);
		comboDirecciones.setValue(null);
		txtLocalidad.setValue("");
		txtCalle.setValue("");
		intBoxAltura.setValue(null);
		txtTelefono.setValue("");
		direcciones.clear();
		direccionSeleccionada = null;
		comboDirecciones.setValue("");
	}
	
	public void deshabilitarCampos(){
		comboDirecciones.setDisabled(true);
		txtLocalidad.setReadonly(true);
		txtCalle.setReadonly(true);
		intBoxAltura.setReadonly(true);
		txtTelefono.setReadonly(true);
	}
	
	public void onClick$buttonGuardar() {
		String alias = txtAlias.getValue();
		String email = txtEmailAdmin.getValue();
		String localidad = txtLocalidad.getValue();
		String calle = txtCalle.getValue();
		Integer altura = intBoxAltura.getValue();
		String telefono = txtTelefono.getValue();
		String descripcion= txtDescripcion.getValue();
		validar(alias, email, localidad, calle, altura, telefono);
		if (!existe && existeUsuario) {
			try {
				//TODO: Alta Nodo no debe pedir telefono.
				nodoService.altaNodo(alias, email, localidad, calle, altura, telefono, vendedor.getId(), descripcion);
				Messagebox.show("El nodo fue creado", "Aviso", Messagebox.OK, Messagebox.INFORMATION);
			} catch (UsuarioInexistenteException e) {
				Messagebox.show("El usuario con el email " + email + " es inexistente", "Error", Messagebox.OK,
						Messagebox.ERROR);
			} catch (NodoYaExistenteException e) {
				throw new WrongValueException(txtAlias,"Ya existe un nodo con ese alias");
			} catch (VendedorInexistenteException e) {
				Messagebox.show("El usuario con el email " + email + " es inexistente, se enviara un mail de registro al usuario.", "Error", Messagebox.OK,
						Messagebox.ERROR);
				throw new WrongValueException(txtEmailAdmin, "El nombre no debe ser vacio!");
			}
		} else {
			try{
				nodoService.altaNodoSinUsuario(alias, email, localidad, calle, altura, telefono, vendedor.getId(), descripcion);
				//TODO: Hacer mandar email.
				Messagebox.show("El nodo fue creado, se enviara un mail de registro a la dirección "+email, "Aviso", Messagebox.OK, Messagebox.INFORMATION);
			}catch(NodoYaExistenteException e){
				throw new WrongValueException(txtAlias,"Ya existe un Nodo con este alias");
			}catch(VendedorInexistenteException e){
				Messagebox.show("Su ID de vendedor es erroneo o inexistente", "Error", Messagebox.OK,
						Messagebox.ERROR);
			}
		}
		Events.sendEvent(Events.ON_RENDER, this.self.getParent(), null);
		this.self.detach();
	}

	private void validar(String alias, String email, String localidad, String calle, Integer altura, String telefono) {
		
		if(StringUtils.isEmpty(alias)){
			throw new WrongValueException(txtAlias,"El alias no debe ser vacio");
		}
		this.existeUsuario = usuarioService.existeUsuarioCon(email);
		if(this.camposVacios() || direccionSeleccionada == null){
			try{								
				obtenerDireccionesDeCliente(email);
				this.agregarDireccionACombo();
			}catch (UsuarioInexistenteException e){
				limpiarcampos();
				this.agregarDireccionACombo();
			}			
			throw new WrongValueException(comboDirecciones,"Seleccione o cree una nueva dirección"); 
		}
		
	}
	
	private void obtenerDireccionesDeCliente(String mail) throws UsuarioInexistenteException{
		Cliente user =(Cliente) this.usuarioService.obtenerUsuarioPorEmail(mail);
		this.usuarioService.inicializarDirecciones(user);
		if(user.obtenerDireccionPredeterminada() != null){
			comboDirecciones.setDisabled(false);
			direcciones.clear();
			direcciones.addAll(user.getDireccionesAlternativas());
			if(direccionCreadaParaNodo != null){
				direcciones.add(direccionCreadaParaNodo);
			}
			comboDirecciones.setValue("Seleccione una dirección");
			direccionSeleccionada=null;
			if(!direcciones.isEmpty()){
				comboDirecciones.setValue("Seleccione una dirección");
			}else{
				limpiarcampos();
			}
		}
	}
	
	private boolean camposVacios(){
		return txtAlias.getValue() == "" && txtCalle.getValue() == "" && txtLocalidad.getValue() == "";
	}
	
	public void onClick$buttonCrearNuevaDireccion(){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("abmComposer",this);
		Window w = (Window) Executions.createComponents("/crearDireccion.zul", this.self, params);
		w.doModal();
	}

	private void llenarCampos() {
		// TODO Auto-generated method stub

	}

	private void inicializarModoLectura() {
		// TODO Auto-generated method stub

	}
	
	public void onClick$cancelar(){
		this.self.detach();
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public ListModelList<Direccion> getDirecciones() {
		return direcciones;
	}

	public void setDirecciones(ListModelList<Direccion> direcciones) {
		this.direcciones = direcciones;
	}

	public Direccion getDireccionSeleccionada() {
		return direccionSeleccionada;
	}

	public void setDireccionSeleccionada(Direccion direccionSeleccionada) {
		this.direccionSeleccionada = direccionSeleccionada;
	}

	public Combobox getComboDirecciones() {
		return comboDirecciones;
	}

	public void setComboDirecciones(Combobox comboDirecciones) {
		this.comboDirecciones = comboDirecciones;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}
