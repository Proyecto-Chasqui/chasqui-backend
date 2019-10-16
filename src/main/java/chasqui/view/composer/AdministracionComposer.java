package chasqui.view.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Categoria;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Fabricante;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.genericEvents.Refresher;
import chasqui.view.renders.CategoriaRenderer;
import chasqui.view.renders.ProductoRenderer;
import chasqui.view.renders.ProductorRenderer;

@SuppressWarnings({ "serial", "deprecation" })
public class AdministracionComposer extends GenericForwardComposer<Component> implements Refresher{
	
	private Window administracionWindow;
	private AnnotateDataBinder binder;
	private Radio radioCategorias;
	private Radio radioProductos;
	private Radio radioConfiguracion;
	private Radio radioAltaUsuario;
	private Radio radioProductores;
	private Radio radioPedidos;
	private Radio radioPedidosColectivos;
	private Radio radioCaracteristicas;
	private Radio radioSolicitudesNodos;
	//menu superior principal
	private Menubar menubar;
	private Menuitem menuItemCategorias;
	private Menuitem menuItemProductos;
	private Menuitem menuItemProductores;
	private Menuitem menuItemPedidos;
	private Menuitem menuItemPedidosColecitvos;
	private Menuitem menuItemNodos;
	private Menuitem menuItemConfiguracion;
	private Menuitem menuItemMostrarFiltrosProductor;
	//fin menu superior principal
	//menu de botones de "crear"
	private Menubar submenubar;
	private Menuitem menuItemNuevaCategoria;
	private Menuitem menuItemNuevoProducto;
	private Menuitem menuItemNuevoProductor;
	private Menuitem menuItemMostrarFiltrosProducto;
	private Menuitem menuItemMostrarFiltrosPedidosColectivos;
	private Menuitem menuItemMostrarFiltrosPedidosIndividuales;
	private Menuitem menuItemReiniciarFiltrosPedidosColectivos;
	private Menuitem menuItemReiniciarFiltrosPedidosIndividuales;
	private Menuitem menuItemReiniciarFiltrosProductos;
	private Menuseparator separadorExport;
	private Menu menuExportar;
	private Menuitem menuItemExportarPedidos;
	private Menuitem menuItemExportarSeleccionados;
	//fin menu de botones
	private Menuitem menuItemLogOut;
	private Div divoldmenu;
	private Listbox oldmenu;
	private Listcell cellRadioSolicitudesNodos;
	private Listcell cellRadioPedidosColectivos;
	private Listcell cellRadioPedidos;
	private Categoria categoriaSeleccionada;
	private Toolbarbutton agregarButton;
	private Toolbarbutton agregarProductoButton;
	private Toolbarbutton agregarProductorButton;
	private Toolbarbutton logout;
	private List<Producto> productosFiltrados;
	private Producto productoSeleccionado;
	private Listbox listboxProductos;
	private Listbox listboxProductores;
	private Listbox listboxCategorias;
	private Include configuracionInclude;
	private Include altaUsuarioInclude;
	private Include estrategiasInclude;
	private Include usuariosActualesInclude;
	private Include pedidosInclude;
	private Include pedidosColectivosInclude;
	private Include solicitudesNodosInclude;
	private Include caracInclude; 
	private Vendedor usuarioLogueado;
	private List<Fabricante> fabricantes;
	private List<Fabricante> listfabricantes;
	private Combobox productorListBox;
	private Fabricante fabricanteSeleccionado;
	private Div divCategoria;
	private Div divProducto;
	private Div divProductores;
	private Div divPedidos;
	private Div divPedidosColectivos;
	private Div divSolicitudesNodos;
	private Div divCaracteristicas;
	private Div filtros_producto;
	private Div filtrosproductores;
	private UsuarioService usuarioService;
	private ProductoService  productoService;
	private ProductorService productorService;
	private Integer numeroDestacados = 0;
	private Integer numeroMaxDestacados = 6;
	private Textbox busquedaPorCodigoProducto;
	private Textbox busquedaPorNombreProductor;
	private Textbox busquedaPorNombreProducto;
	private Intbox busquedaPorStock;
	private List<String> destacado;
	private List<String> visibilidad;
	private String destacadoSeleccionado;
	private String visibilidadSeleccionada;
	private Component admcomponent;
	private static final String TODAS = "Todas";
	private static final String HABILITADO = "Visible";
	private static final String DESHABILITADO = "Oculto";
	private static final String DESTACADO = "Destacado";
	private static final String NO_DESTACADO = "No destacado";
//	private List<Producto>productos;
	private Div divFiltrosPedidosColectivos;
	private Div divFiltrosPedidosIndividuales;
	private HistorialPedidosColectivosComposer pedidosColectivosComposer;
	private PedidosComposer pedidosIndividualesComposer;
	//imagenes de ayuda
	private Image ayudapedidoscolectivos;
	private Image ayudapedidos;
	private Image ayudaproductores;
	private Image ayudacategorias;
	private Image ayudaproductos;

	
	public void doAfterCompose(Component comp) throws Exception{
		admcomponent = comp;
		Executions.getCurrent().getSession().setAttribute("administracionComposer",this);
		usuarioLogueado = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(usuarioLogueado == null){
			Executions.sendRedirect("/");
			return;
		}
		Executions.getCurrent().getSession().setAttribute("administracionWindow", comp);
		binder = new AnnotateDataBinder(comp);
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		productoService = (ProductoService) SpringUtil.getBean("productoService");
		productorService = (ProductorService) SpringUtil.getBean("productorService");
		numeroDestacados = productoService.obtenerVariantesDestacadas(usuarioLogueado.getId()).size();
		productosFiltrados = usuarioLogueado.getProductos();
		fabricantes = (List<Fabricante>) productorService.obtenerProductores(usuarioLogueado.getId());
		visibilidad = Arrays.asList(TODAS,HABILITADO,DESHABILITADO);
		destacado = Arrays.asList(TODAS,DESTACADO,NO_DESTACADO);
		listfabricantes = new ArrayList<Fabricante>();
		listfabricantes.addAll(fabricantes);
		super.doAfterCompose(comp);
		divFiltrosPedidosColectivos = (Div) pedidosColectivosInclude.getFellow("historialPedidosColectivosWindow").getFellow("filtros");
		divFiltrosPedidosIndividuales = (Div) pedidosInclude.getFellow("pedidosWindow").getFellow("filtros");
		pedidosIndividualesComposer = (PedidosComposer) Executions.getCurrent().getSession().getAttribute("pedidosComposer");
		pedidosColectivosComposer = (HistorialPedidosColectivosComposer) Executions.getCurrent().getSession().getAttribute("historialPedidosColectivosComposer");
		if(usuarioLogueado.getIsRoot()){
			inicializacionUsuarioROOT();
		}else{
			inicializacionUsuarioAdministrador();
		}
		comp.addEventListener(Events.ON_USER, new CategoriaEventListener(this));
		comp.addEventListener(Events.ON_NOTIFY, new ProductoEventListener(this));
		comp.addEventListener(Events.ON_RENDER, new RefreshEventListener(this));
		binder.loadAll();
	}

	public void inicializacionUsuarioROOT(){
		divoldmenu.setVisible(true);
		oldmenu.setVisible(true);
		menubar.setVisible(false);
		radioAltaUsuario.setChecked(true);
		logout.setVisible(true);
		radioCategorias.getParent().getParent().setVisible(false);
		radioCategorias.setDisabled(true);
		radioProductos.setDisabled(true);
		radioProductos.getParent().getParent().setVisible(false);
		radioPedidos.setDisabled(true);
		radioPedidos.getParent().getParent().setVisible(false);
		radioProductores.setDisabled(true);
		radioProductores.getParent().getParent().setVisible(false);
		radioPedidosColectivos.setChecked(true);
		radioPedidosColectivos.getParent().getParent().setVisible(false);
		listboxCategorias.setVisible(false);
//		radioCaracteristicas.setDisabled(true);
		radioConfiguracion.setDisabled(true);
		radioConfiguracion.getParent().getParent().setVisible(false);
		radioSolicitudesNodos.getParent().getParent().setVisible(false);
		radioSolicitudesNodos.setDisabled(false);
		administracionWindow.setVisible(true);
		onClick$radioAltaUsuario();
	}
	
	public void inicializacionUsuarioAdministrador(){
		divoldmenu.setVisible(true);
		oldmenu.setVisible(false);
		menubar.setVisible(true);
		menuItemLogOut.setLabel(usuarioLogueado.getUsername());
		logout.setVisible(false);
		listboxProductos.setItemRenderer(new ProductoRenderer(this.self));
		listboxCategorias.setItemRenderer(new CategoriaRenderer(this.self));
		listboxProductores.setItemRenderer(new ProductorRenderer(this.self));
		administracionWindow.setVisible(true);
		mostrarSegunEstrategiasDeComercializacion();
		radioCategorias.setChecked(true);
		radioAltaUsuario.setVisible(false);
		radioAltaUsuario.getParent().getParent().setVisible(false);
		radioCaracteristicas.getParent().getParent().setVisible(false);
		refresh();
		onClick$menuItemCategorias();	
		
	}
	//TODO:
	private void mostrarSegunEstrategiasDeComercializacion(){
		mostrarTodosLosRadioButtons(false);
		EstrategiasDeComercializacion estrategias = usuarioLogueado.getEstrategiasUtilizadas();
		if(estrategias.isCompraIndividual()){
			cellRadioPedidos.setVisible(true);
			radioPedidos.setVisible(true); 
			menuItemPedidos.setVisible(true);
		}
		if(estrategias.isGcc()){
			cellRadioPedidosColectivos.setVisible(true);
			radioPedidosColectivos.setVisible(true);	
			menuItemPedidosColecitvos.setVisible(true);
		}
		if(estrategias.isNodos()){
			cellRadioSolicitudesNodos.setVisible(true);
			radioSolicitudesNodos.setVisible(true);
			menuItemNodos.setVisible(true);
			//cellRadioPedidos.setVisible(true);
			//radioPedidos.setVisible(true);
		}
	}
	
	private void mostrarTodosLosRadioButtons(Boolean b){
		radioPedidos.setVisible(b);
		radioPedidosColectivos.setVisible(b);
		radioSolicitudesNodos.setVisible(b);
		cellRadioPedidos.setVisible(b);
		cellRadioPedidosColectivos.setVisible(b);
		menuItemPedidos.setVisible(b);
		menuItemPedidosColecitvos.setVisible(b);
		menuItemNodos.setVisible(b);
		//cellRadioSolicitudesNodos.setVisible(b);

	}
	
	public void onClick$menuItemCategorias(){
		divProducto.setVisible(false);
		menuExportar.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(true);
		ayudaproductos.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		filtros_producto.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		submenubar.setVisible(true);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosProducto.setVisible(false);
		menuItemNuevaCategoria.setVisible(true);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		agregarProductoButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		configuracionInclude.setVisible(false);
		divProductores.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divPedidos.setVisible(false);
		pedidosInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		caracInclude.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		agregarButton.setVisible(true);
		divCategoria.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	@Deprecated
	public void onClick$radioCategorias(){
		divProducto.setVisible(false);
		agregarProductoButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		configuracionInclude.setVisible(false);
		divProductores.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divPedidos.setVisible(false);
		pedidosInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		caracInclude.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		agregarButton.setVisible(true);
		divCategoria.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	
	public void onClick$radioCaracteristicas(){
		divProducto.setVisible(false);
		submenubar.setVisible(false);
		menuExportar.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		agregarProductoButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		configuracionInclude.setVisible(false);
		divProductores.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divPedidos.setVisible(false);
		pedidosInclude.setVisible(false);
		agregarButton.setVisible(false);
		divCategoria.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		divCaracteristicas.setVisible(true);
		caracInclude.setSrc("/caracteristica.zul");
		caracInclude.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		estrategiasInclude.setVisible(false);
		binder.loadAll();
	}

	public void onClick$menuItemProductos() throws VendedorInexistenteException{
		sincWithBD();
		onClick$buscarProducto();
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(true);
		menuItemMostrarFiltrosProductor.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		menuExportar.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(true);
		divFiltrosPedidosColectivos.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		submenubar.setVisible(true);
		filtros_producto.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosProducto.setVisible(true);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(true);
		menuItemNuevoProductor.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		divCategoria.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		agregarProductoButton.setVisible(true);
		divProducto.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();

	}
	@Deprecated
	public void onClick$radioProductos() throws VendedorInexistenteException{
		sincWithBD();
		onClick$buscarProducto();
		agregarButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		divCategoria.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		agregarProductoButton.setVisible(true);
		divProducto.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();

	}
	
	public void onClick$menuItemConfiguracion(){
		menuItemMostrarFiltrosProducto.setVisible(false);
		submenubar.setVisible(false);
		menuExportar.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		filtros_producto.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		agregarProductorButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		divCategoria.setVisible(false);
		divProducto.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		configuracionInclude.setVisible(true);
		
		binder.loadAll();
	}
	@Deprecated
	public void onClick$radioConfiguracion(){
		agregarProductorButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		divCategoria.setVisible(false);
		divProducto.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		configuracionInclude.setVisible(true);
		
		binder.loadAll();
	}
	
	public void onClick$radioAltaUsuario(){
		submenubar.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuExportar.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		separadorExport.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		filtros_producto.setVisible(false);
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		agregarProductorButton.setVisible(false);
		configuracionInclude.setVisible(false);
		divProductores.setVisible(false);
		divPedidos.setVisible(false);
		pedidosInclude.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		altaUsuarioInclude.setVisible(true);
		usuariosActualesInclude.setVisible(true);
		estrategiasInclude.setVisible(true);
		binder.loadAll();
	}
	
	public void onClick$menuItemProductores() throws VendedorInexistenteException{
		sincWithBD();
		onBuscarProductor();
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(true);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(true);
		menuExportar.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		submenubar.setVisible(true);
		divFiltrosPedidosIndividuales.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		filtros_producto.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosProducto.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(true);
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		divProductores.setVisible(true);
		agregarProductorButton.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	@Deprecated
	public void onClick$radioProductores() throws VendedorInexistenteException{
		sincWithBD();
		onBuscarProductor();
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		divProductores.setVisible(true);
		agregarProductorButton.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	
	public void onClick$menuItemPedidos(){
		submenubar.setVisible(true);
		filtros_producto.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(true);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemExportarPedidos.setVisible(true);
		menuItemMostrarFiltrosProductor.setVisible(false);
		separadorExport.setVisible(true);
		menuExportar.setVisible(true);
		menuItemExportarSeleccionados.setVisible(true);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(true);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(true);
		divFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosProducto.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		filtros_producto.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosInclude.setVisible(true);
		divPedidos.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	@Deprecated
	public void onClick$radioPedidos(){
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosInclude.setVisible(true);
		divPedidos.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);
		binder.loadAll();
	}
	
	public void onClick$menuItemPedidosColecitvos(){
		submenubar.setVisible(true);
		menuExportar.setVisible(false);
		filtros_producto.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(true);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(true);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(true);
		menuItemMostrarFiltrosProducto.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		pedidosColectivosInclude.setVisible(true);
		divPedidosColectivos.setVisible(true);
		binder.loadAll();
	}
	@Deprecated
	public void onClick$radioPedidosColectivos(){
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		divSolicitudesNodos.setVisible(false);
		solicitudesNodosInclude.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		pedidosColectivosInclude.setVisible(true);
		divPedidosColectivos.setVisible(true);
		binder.loadAll();
	}
	
	public void onClick$menuItemNodos(){	
		submenubar.setVisible(false);
		filtros_producto.setVisible(false);
		menuExportar.setVisible(false);
		menuItemMostrarFiltrosProductor.setVisible(false);
		separadorExport.setVisible(false);
		ayudapedidoscolectivos.setVisible(false);
		ayudapedidos.setVisible(false);
		ayudaproductores.setVisible(false);
		ayudacategorias.setVisible(false);
		ayudaproductos.setVisible(false);
		menuItemExportarPedidos.setVisible(false);
		menuItemExportarSeleccionados.setVisible(false);
		menuItemReiniciarFiltrosPedidosColectivos.setVisible(false);
		menuItemReiniciarFiltrosPedidosIndividuales.setVisible(false);
		menuItemReiniciarFiltrosProductos.setVisible(false);
		menuItemMostrarFiltrosPedidosIndividuales.setVisible(false);
		divFiltrosPedidosColectivos.setVisible(false);
		divFiltrosPedidosIndividuales.setVisible(false);
		menuItemMostrarFiltrosPedidosColectivos.setVisible(false);
		menuItemMostrarFiltrosProducto.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		menuItemNuevoProducto.setVisible(false);
		menuItemNuevoProductor.setVisible(false);
		menuItemNuevaCategoria.setVisible(false);
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		divSolicitudesNodos.setVisible(true);
		solicitudesNodosInclude.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);

		binder.loadAll();
	}
	
	@Deprecated
	public void onClick$radioSolicitudesNodos(){	
		divProducto.setVisible(false);
		divCategoria.setVisible(false);
		agregarButton.setVisible(false);
		agregarProductoButton.setVisible(false);
		configuracionInclude.setVisible(false);
		altaUsuarioInclude.setVisible(false);
		usuariosActualesInclude.setVisible(false);
		divProductores.setVisible(false);
		caracInclude.setVisible(false);
		divCaracteristicas.setVisible(false);
		agregarProductorButton.setVisible(false);
		pedidosInclude.setVisible(false);
		divPedidos.setVisible(false);
		divSolicitudesNodos.setVisible(true);
		solicitudesNodosInclude.setVisible(true);
		pedidosColectivosInclude.setVisible(false);
		divPedidosColectivos.setVisible(false);

		binder.loadAll();
	}

	public void sincWithBD() throws VendedorInexistenteException {
		Vendedor user =(Vendedor) usuarioService.obtenerUsuarioPorID(usuarioLogueado.getId());
		usuarioService.inicializarListasDe(user);
		Executions.getCurrent().getSession().setAttribute(Constantes.SESSION_USERNAME, user);
		usuarioLogueado = user;
	}
	
	public void refresh(){
//		usuarioLogueado = (Vendedor) usuarioService.obtenerVendedorPorID(usuarioLogueado.getId());
//		productos= new ArrayList<Producto>(usuarioLogueado.obtenerProductos());
		binder.loadAll();
	}
	
	public void onClick$logout(){
		Executions.getCurrent().getSession().invalidate();
		Executions.sendRedirect("/");
	}
	
	public void onClick$menuItemLogOut(){
		Executions.getCurrent().getSession().invalidate();
		Executions.sendRedirect("/");
	}
	
	public void onMostrarFiltrosProducto() {
		filtros_producto.setVisible(!filtros_producto.isVisible());
	}
	
	public void onMostrarFiltrosPedidosColectivos() {
		divFiltrosPedidosColectivos.setVisible(!divFiltrosPedidosColectivos.isVisible());
	}
	
	public void onMostrarFiltrosPedidosIndividuales() {
		divFiltrosPedidosIndividuales.setVisible(!divFiltrosPedidosIndividuales.isVisible());
	}
	
	public void onMostrarFiltrosProductores() {
		filtrosproductores.setVisible(!filtrosproductores.isVisible());
	}
	
	public void onExportarPedidosIndividuales() {
		try {
			pedidosIndividualesComposer.onClick$exportarTodosbtn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onExportarPedidosIndividualesSeleccionados() {
		try {
			pedidosIndividualesComposer.onClick$exportarSeleccionados();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onReiniciarFiltrosPedidosColectivos() {
		pedidosColectivosComposer.onClick$limpiarCamposbtn();
	}
	
	public void onReiniciarFiltrosPedidosIndividuales() {
		pedidosIndividualesComposer.onClick$limpiarCamposbtn();
	}
	
	public void onReiniciarFiltrosProductos() {
		this.onClick$limpiarCamposbtn();
	}
	
	public void onEditarCategoria(Categoria c){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("categoria",c);
		params.put("esEdicion", true);
		Window abmCategoria = (Window) Executions.createComponents("/abmCategoria.zul", this.self,params );
		abmCategoria.doModal();
	}
	
	public void onCrearCategoria(){
		Map<String,Object>params = new HashMap<String,Object>();
		params.put("esEdicion", false);
		Window abmCategoria = (Window) Executions.createComponents("/abmCategoria.zul", this.self,params);
		abmCategoria.doModal();
	}
	
	public void onEliminarCategoria(Categoria c){
		validarCategoriaValidaParaEliminar(c);
	}
	
	private void validarCategoriaValidaParaEliminar(Categoria c){
		if (c.getProductos() != null && c.getProductos().size() > 0){
				Messagebox.show("La categoria: '" + c.getNombre() + "' aún tiene productos asociados a ella. desasocie los mismos para eliminar la categoria"
						, "Error!", Messagebox.OK, Messagebox.ERROR);
				return;
			}
		usuarioLogueado.eliminarCategoria(c);
		usuarioService.guardarUsuario(usuarioLogueado);
		Events.sendEvent(Events.ON_RENDER,this.self,null);
	}
	
	
	public void onEditarProducto(Producto p){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_EDICION);
		params.put("producto", p);
		Window windowProducto = (Window) Executions.createComponents("/abmProducto.zul", this.self, params);
		windowProducto.doModal();
	}
	
	public void onEliminarProducto(final Producto p){
		Messagebox.show(
				"¿Esta seguro que desea eliminar el producto  " + p.getNombre() + " ?",
				"Pregunta",
	    		new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.ABORT},
	    		new String[] {"Aceptar","Cancelar"},
	    		Messagebox.INFORMATION, null, new EventListener<ClickEvent>(){

			public void onEvent(ClickEvent event) throws Exception {
				String edata= event.getData().toString();
				switch (edata){
				case "YES":
					try {
						p.getCategoria().eliminarProducto(p);
						p.getFabricante().eliminarProducto(p);
						usuarioService.guardarUsuario(usuarioLogueado);
						onClick$buscarProducto();
						binder.loadAll();
						Clients.showNotification("El producto: '" + p.getNombre() + "' fue eliminado con exito!", "info", admcomponent, "middle_center", 2000,true);
					} catch (Exception e) {
						Clients.showNotification("Ocurrio un error desconocido", "error", admcomponent, "middle_center", 3000,true);
						e.printStackTrace();						
					}
					break;
				case "ABORT":
				}
			}
			});
	}
	
	public void onDestacarProducto(Producto p, Toolbarbutton botonDestacar){
		Variante v = p.getVariantes().get(0);
		if(v.getDestacado()){
			v.setDestacado(!v.getDestacado());
			destacar(v);
			usuarioService.guardarUsuario(usuarioLogueado);
		}else{
			if(!v.getDestacado() && numeroDestacados < numeroMaxDestacados){
				v.setDestacado(!v.getDestacado());
				destacar(v);
				usuarioService.guardarUsuario(usuarioLogueado);
			}else{
				Messagebox.show("Solo se pueden tener hasta 6 destacados",
						"Advertencia", 
						Messagebox.OK,
						Messagebox.INFORMATION
						);
			}
		}
		this.binder.loadAll();
	}
	
//	///////Ocultar producto
	public void onOcultarProducto(Producto producto){
		producto.setOcultado(!producto.isOcultado());
		usuarioService.guardarUsuario(usuarioLogueado);
		this.binder.loadAll();
	}
//	///////Ocultar producto
	
	public void onBuscar() {
		this.onClick$buscarProducto();
	}
	
	public void onBuscarProductor() throws WrongValueException, VendedorInexistenteException {
		this.listfabricantes.clear();
		this.listfabricantes.addAll(productorService.obtenerProductoresPorNombre(usuarioLogueado.getId(),busquedaPorNombreProductor.getValue()));
		this.binder.loadAll();
	}
	
	public void onClick$buscarProducto(){
		
		Integer fabricanteSeleccionadoId = null;
		if(fabricanteSeleccionado!=null||busquedaPorCodigoProducto.getValue() != null){
			Boolean habilitado = evaluarVisibilidad(this.visibilidadSeleccionada);
			Boolean destacado = evaluarPropiedadDestacada(this.destacadoSeleccionado);
			if(fabricanteSeleccionado != null) {
				fabricanteSeleccionadoId = fabricanteSeleccionado.getId();
			}
			productosFiltrados.clear();	
			productosFiltrados.addAll(usuarioLogueado.obtenerProductosDelFabricante(fabricanteSeleccionadoId,busquedaPorCodigoProducto.getValue(),destacado,habilitado,busquedaPorStock.getValue(), busquedaPorNombreProducto.getValue()));
		}else{
			this.onClick$limpiarCamposbtn();
		}
		this.binder.loadAll();
	}
	
	private Boolean evaluarPropiedadDestacada(String destacadoSeleccionado2) {
		
		Boolean ret = null;
		if(destacadoSeleccionado2 != null) {
			if(destacadoSeleccionado2.equals(DESTACADO)) {
				ret = true;
			}
			if(destacadoSeleccionado2.equals(NO_DESTACADO)) {
				ret = false;
			}
		}		
		return ret;
	}

	private Boolean evaluarVisibilidad(String visibilidadSeleccionada2) {
		Boolean ret = null;
		if(visibilidadSeleccionada2 != null) {
			if(visibilidadSeleccionada2.equals(HABILITADO)) {
				ret = true;
			}
		
			if(visibilidadSeleccionada2.equals(DESHABILITADO)) {
				ret = false;
			}
		}
		
		return ret;
	}

	public void onClick$limpiarCamposbtn(){
		productosFiltrados.clear();
		productosFiltrados.addAll(usuarioLogueado.getProductos());
		visibilidadSeleccionada = null;
		destacadoSeleccionado = null;
		fabricanteSeleccionado = null;
		busquedaPorCodigoProducto.setValue(null);
		busquedaPorNombreProducto.setValue(null);
		busquedaPorStock.setValue(null);
		this.binder.loadAll();
	}
	
	
	private void destacar(Variante v){
		if(v.getDestacado()){
			//botonDestacar.setImage("/imagenes/destacado_on.png");
			numeroDestacados ++;
		}else{
			//botonDestacar.setImage("/imagenes/destacado_off.png");
			numeroDestacados--;
		}
	}
	
	public void onVisualizarProducto(Producto p){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_LECTURA);
		params.put("producto", p);
		Window windowProducto = (Window) Executions.createComponents("/abmProducto.zul", this.self, params);
		windowProducto.doModal();
	}
	
	public void onCrearProducto(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_EDICION);
		Window windowProducto = (Window) Executions.createComponents("/abmProducto.zul", this.self, params);
		windowProducto.doModal();
	}
	
	public void onCrearProductor(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_EDICION);
		Window windowProducto = (Window) Executions.createComponents("/abmProductor.zul", this.self, params);
		windowProducto.doModal();
	}
	
	public void onCrearNodo(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_EDICION);
		Window windowNodo = (Window) Executions.createComponents("/abmNodo.zul", this.self, params);
		windowNodo.doModal();		
	}
	
	public void verProductor(Fabricante f){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_LECTURA);
		params.put("productor", f);
		Window windowProducto = (Window) Executions.createComponents("/abmProductor.zul", this.self, params);
		windowProducto.doModal();
	}
	
	public void editarProductor(Fabricante f) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("accion", Constantes.VENTANA_MODO_EDICION);
		params.put("productor", f);
		Window windowProducto = (Window) Executions.createComponents("/abmProductor.zul", this.self, params);
		windowProducto.doModal();
		
	}
	
	public void eliminarProductor(Fabricante f) throws WrongValueException, VendedorInexistenteException{
		productorService.inicializarListasDeProducto(f);
		if (f.getProductos() != null && f.getProductos().size() > 0){
			Messagebox.show("El productor: '" + f.getNombre() + "' aún tiene productos asociados. desasocie los mismos para eliminar el productor"
					, "Error!", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		// mostrar cartel
		usuarioLogueado.eliminarProductor(f);
		productorService.eliminar(f);
		//usuarioService.guardarUsuario(usuarioLogueado);
		this.onBuscarProductor();
		alert("El productor: '" + f.getNombre() + "' fue eliminado con exito!");
		this.binder.loadAll();
	}
	
	public void onSelect$productorComboBox(SelectEvent evt){
		onClick$buscarProducto();
	}
	
	public void onSelect$destacadoComboBox(SelectEvent evt){
		onClick$buscarProducto();
	}
	
	public void onSelect$habilitadoComboBox(SelectEvent evt){
		onClick$buscarProducto();
	}

	public Vendedor getUsuarioLogueado() {
		return usuarioLogueado;
	}

	public void setUsuarioLogueado(Vendedor usuarioLogueado) {
		this.usuarioLogueado = usuarioLogueado;
	}

	public Categoria getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}
	public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public Producto getProductoSeleccionado() {
		return productoSeleccionado;
	}
	public void setProductoSeleccionado(Producto productoSeleccionado) {
		this.productoSeleccionado = productoSeleccionado;
	}

	public List<Fabricante> getFabricantes() {
		return fabricantes;
	}

	public void setFabricantes(List<Fabricante> fabricantes) {
		this.fabricantes = fabricantes;
	}

	public Combobox getProductorListBox() {
		return productorListBox;
	}

	public void setProductorListBox(Combobox productorListBox) {
		this.productorListBox = productorListBox;
	}

	public Fabricante getFabricanteSeleccionado() {
		return fabricanteSeleccionado;
	}

	public void setFabricanteSeleccionado(Fabricante fabricanteSeleccionado) {
		this.fabricanteSeleccionado = fabricanteSeleccionado;
	}

	public List<Producto> getProductosFiltrados() {
		return productosFiltrados;
	}

	public void setProductosFiltrados(List<Producto> productosFiltrados) {
		this.productosFiltrados = productosFiltrados;
	}

	public Listcell getCellRadioSolicitudesNodos() {
		return cellRadioSolicitudesNodos;
	}

	public void setCellRadioSolicitudesNodos(Listcell cellRadioSolicitudesNodos) {
		this.cellRadioSolicitudesNodos = cellRadioSolicitudesNodos;
	}

	public Listcell getCellRadioPedidosColectivos() {
		return cellRadioPedidosColectivos;
	}

	public void setCellRadioPedidosColectivos(Listcell cellRadioPedidosColectivos) {
		this.cellRadioPedidosColectivos = cellRadioPedidosColectivos;
	}

	public Listcell getCellRadioPedidos() {
		return cellRadioPedidos;
	}

	public void setCellRadioPedidos(Listcell cellRadioPedidos) {
		this.cellRadioPedidos = cellRadioPedidos;
	}

	public Include getEstrategiasInclude() {
		return estrategiasInclude;
	}

	public void setEstrategiasInclude(Include estrategiasInclude) {
		this.estrategiasInclude = estrategiasInclude;
	}

	public List<Fabricante> getListfabricantes() {
		return listfabricantes;
	}

	public void setListfabricantes(List<Fabricante> listfabricantes) {
		this.listfabricantes = listfabricantes;
	}

	public List<String> getDestacado() {
		return destacado;
	}

	public void setDestacado(List<String> destacado) {
		this.destacado = destacado;
	}

	public List<String> getVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(List<String> visibilidad) {
		this.visibilidad = visibilidad;
	}

	public String getDestacadoSeleccionado() {
		return destacadoSeleccionado;
	}

	public void setDestacadoSeleccionado(String destacadoSeleccionado) {
		this.destacadoSeleccionado = destacadoSeleccionado;
	}

	public String getVisibilidadSeleccionada() {
		return visibilidadSeleccionada;
	}

	public void setVisibilidadSeleccionada(String visibilidadSeleccionada) {
		this.visibilidadSeleccionada = visibilidadSeleccionada;
	}

	public Menuitem getMenuItemCategorias() {
		return menuItemCategorias;
	}

	public void setMenuItemCategorias(Menuitem menuItemCategorias) {
		this.menuItemCategorias = menuItemCategorias;
	}

	public Menuitem getMenuItemProductos() {
		return menuItemProductos;
	}

	public void setMenuItemProductos(Menuitem menuItemProductos) {
		this.menuItemProductos = menuItemProductos;
	}

	public Menuitem getMenuItemProductores() {
		return menuItemProductores;
	}

	public void setMenuItemProductores(Menuitem menuItemProductores) {
		this.menuItemProductores = menuItemProductores;
	}

	public Menuitem getMenuItemPedidos() {
		return menuItemPedidos;
	}

	public Menuitem getMenuItemPedidosColecitvos() {
		return menuItemPedidosColecitvos;
	}

	public Menuitem getMenuItemNodos() {
		return menuItemNodos;
	}

	public Menuitem getMenuItemConfiguracion() {
		return menuItemConfiguracion;
	}

	public void setMenuItemPedidos(Menuitem menuItemPedidos) {
		this.menuItemPedidos = menuItemPedidos;
	}

	public void setMenuItemPedidosColecitvos(Menuitem menuItemPedidosColecitvos) {
		this.menuItemPedidosColecitvos = menuItemPedidosColecitvos;
	}

	public void setMenuItemNodos(Menuitem menuItemNodos) {
		this.menuItemNodos = menuItemNodos;
	}

	public void setMenuItemConfiguracion(Menuitem menuItemConfiguracion) {
		this.menuItemConfiguracion = menuItemConfiguracion;
	}

	public Menuitem getMenuItemNuevaCategoria() {
		return menuItemNuevaCategoria;
	}

	public Menuitem getMenuItemNuevoProducto() {
		return menuItemNuevoProducto;
	}

	public Menuitem getMenuItemNuevoProductor() {
		return menuItemNuevoProductor;
	}

	public void setMenuItemNuevaCategoria(Menuitem menuItemNuevaCategoria) {
		this.menuItemNuevaCategoria = menuItemNuevaCategoria;
	}

	public void setMenuItemNuevoProducto(Menuitem menuItemNuevoProducto) {
		this.menuItemNuevoProducto = menuItemNuevoProducto;
	}

	public void setMenuItemNuevoProductor(Menuitem menuItemNuevoProductor) {
		this.menuItemNuevoProductor = menuItemNuevoProductor;
	}

	public Menubar getSubmenubar() {
		return submenubar;
	}

	public void setSubmenubar(Menubar submenubar) {
		this.submenubar = submenubar;
	}

	public Listbox getOldmenu() {
		return oldmenu;
	}

	public void setOldmenu(Listbox oldmenu) {
		this.oldmenu = oldmenu;
	}

	public Menubar getMenubar() {
		return menubar;
	}

	public void setMenubar(Menubar menubar) {
		this.menubar = menubar;
	}

	public Div getDivoldmenu() {
		return divoldmenu;
	}

	public void setDivoldmenu(Div divoldmenu) {
		this.divoldmenu = divoldmenu;
	}

	public Menuitem getMenuItemLogOut() {
		return menuItemLogOut;
	}

	public void setMenuItemLogOut(Menuitem menuItemLogOut) {
		this.menuItemLogOut = menuItemLogOut;
	}

	public Menuitem getMenuItemMostrarFiltrosProducto() {
		return menuItemMostrarFiltrosProducto;
	}

	public void setMenuItemMostrarFiltrosProducto(Menuitem menuItemMostrarFiltrosProducto) {
		this.menuItemMostrarFiltrosProducto = menuItemMostrarFiltrosProducto;
	}

	public Div getFiltros_producto() {
		return filtros_producto;
	}

	public void setFiltros_producto(Div filtros_producto) {
		this.filtros_producto = filtros_producto;
	}

	public Menuitem getMenuItemMostrarFiltrosPedidosColectivos() {
		return menuItemMostrarFiltrosPedidosColectivos;
	}

	public void setMenuItemMostrarFiltrosPedidosColectivos(Menuitem menuItemMostrarFiltrosPedidosColectivos) {
		this.menuItemMostrarFiltrosPedidosColectivos = menuItemMostrarFiltrosPedidosColectivos;
	}

	public Menuitem getMenuItemMostrarFiltrosPedidosIndividuales() {
		return menuItemMostrarFiltrosPedidosIndividuales;
	}

	public void setMenuItemMostrarFiltrosPedidosIndividuales(Menuitem menuItemMostrarFiltrosPedidosIndividuales) {
		this.menuItemMostrarFiltrosPedidosIndividuales = menuItemMostrarFiltrosPedidosIndividuales;
	}

	public Menuitem getMenuItemReiniciarFiltrosPedidosIndividuales() {
		return menuItemReiniciarFiltrosPedidosIndividuales;
	}

	public void setMenuItemReiniciarFiltrosPedidosIndividuales(Menuitem menuItemReiniciarFiltrosPedidosIndividuales) {
		this.menuItemReiniciarFiltrosPedidosIndividuales = menuItemReiniciarFiltrosPedidosIndividuales;
	}

	public Menuitem getMenuItemReiniciarFiltrosPedidosColectivos() {
		return menuItemReiniciarFiltrosPedidosColectivos;
	}

	public void setMenuItemReiniciarFiltrosPedidosColectivos(Menuitem menuItemReiniciarFiltrosPedidosColectivos) {
		this.menuItemReiniciarFiltrosPedidosColectivos = menuItemReiniciarFiltrosPedidosColectivos;
	}

	public Menuitem getMenuItemReiniciarFiltrosProductos() {
		return menuItemReiniciarFiltrosProductos;
	}

	public void setMenuItemReiniciarFiltrosProductos(Menuitem menuItemReiniciarFiltrosProductos) {
		this.menuItemReiniciarFiltrosProductos = menuItemReiniciarFiltrosProductos;
	}

	public Menuitem getMenuItemExportarPedidos() {
		return menuItemExportarPedidos;
	}

	public void setMenuItemExportarPedidos(Menuitem menuItemExportarPedidos) {
		this.menuItemExportarPedidos = menuItemExportarPedidos;
	}

	public Menuitem getMenuItemExportarSeleccionados() {
		return menuItemExportarSeleccionados;
	}

	public void setMenuItemExportarSeleccionados(Menuitem menuItemExportarSeleccionados) {
		this.menuItemExportarSeleccionados = menuItemExportarSeleccionados;
	}

	public Menu getMenuExportar() {
		return menuExportar;
	}

	public void setMenuExportar(Menu menuExportar) {
		this.menuExportar = menuExportar;
	}

	public Menuseparator getSeparadorExport() {
		return separadorExport;
	}

	public void setSeparadorExport(Menuseparator separadorExport) {
		this.separadorExport = separadorExport;
	}

	public Menuitem getMenuItemMostrarFiltrosProductor() {
		return menuItemMostrarFiltrosProductor;
	}

	public void setMenuItemMostrarFiltrosProductor(Menuitem menuItemMostrarFiltrosProductor) {
		this.menuItemMostrarFiltrosProductor = menuItemMostrarFiltrosProductor;
	}

	public Div getFiltrosproductores() {
		return filtrosproductores;
	}

	public void setFiltrosproductores(Div filtrosproductores) {
		this.filtrosproductores = filtrosproductores;
	}

	public Image getAyudapedidoscolectivos() {
		return ayudapedidoscolectivos;
	}

	public Image getAyudapedidos() {
		return ayudapedidos;
	}

	public Image getAyudaproductores() {
		return ayudaproductores;
	}

	public Image getAyudacategorias() {
		return ayudacategorias;
	}

	public void setAyudapedidoscolectivos(Image ayudapedidoscolectivos) {
		this.ayudapedidoscolectivos = ayudapedidoscolectivos;
	}

	public void setAyudapedidos(Image ayudapedidos) {
		this.ayudapedidos = ayudapedidos;
	}

	public void setAyudaproductores(Image ayudaproductores) {
		this.ayudaproductores = ayudaproductores;
	}

	public void setAyudacategorias(Image ayudacategorias) {
		this.ayudacategorias = ayudacategorias;
	}

	public Image getAyudaproductos() {
		return ayudaproductos;
	}

	public void setAyudaproductos(Image ayudaproductos) {
		this.ayudaproductos = ayudaproductos;
	}
	
//	public void setVisibleEstrategiasConfig(Boolean b){
//		estrategiasInclude.setVisible(b);
//	}
//	
//	public void setVisiblealtaUsuarioInclude(Boolean b){
//		altaUsuarioInclude.setVisible(b);
//	}

//	public List<Producto> getProductos() {
//		return productos;
//	}
//
//	public void setProductos(List<Producto> productos) {
//		this.productos = productos;
//	}

	
	
	
}


class CategoriaEventListener implements EventListener<Event>{

	AdministracionComposer composer;
	public CategoriaEventListener(AdministracionComposer comp){
		this.composer = comp;
	}
	
	public void onEvent(Event event) throws Exception {
		Map<String,Object>params = (Map<String,Object>) event.getData();
		if(params.get("accion").equals("editar")){
			this.composer.onEditarCategoria((Categoria) params.get("categoria"));
		}
		if(params.get("accion").equals("eliminar")){
			this.composer.onEliminarCategoria((Categoria) params.get("categoria"));
		}
		
	}
	
}


class ProductoEventListener implements EventListener<Event>{

	AdministracionComposer composer;
	public ProductoEventListener(AdministracionComposer comp){
		this.composer = comp;
	}
	
	public void onEvent(Event event) throws Exception {
		Map<String,Object>params = (Map<String,Object>) event.getData();
		if(params == null){
			composer.refresh();
			return;
		}
		if(params.get("accion").equals("productoGuardado")){
			this.composer.onClick$buscarProducto();
		}
		Producto p = (Producto) params.get("producto");
		Fabricante f = (Fabricante) params.get("productor");
		Toolbarbutton botonDestacar = (Toolbarbutton) params.get("boton");
		Toolbarbutton botonOcultar = (Toolbarbutton) params.get("boton");
		if(params.get("accion").equals("editar") && p != null){
			this.composer.onEditarProducto(p);
		}
		if(params.get("accion").equals("eliminar") && p != null){
			this.composer.onEliminarProducto(p);
		}
		if(params.get("accion").equals("visualizar") && p != null){
			this.composer.onVisualizarProducto(p);
		}
		if(params.get("accion").equals("eliminar") && f != null){
			this.composer.eliminarProductor(f);
		}
		if(params.get("accion").equals("visualizar") && f != null){
			this.composer.verProductor(f);
		}
		if(params.get("accion").equals("edicion") && f != null){
			this.composer.editarProductor(f);
		}
		if(params.get("accion").equals("destacar") && p != null){
			this.composer.onDestacarProducto(p,botonDestacar);
		}
		if(params.get("accion").equals("ocultar") && p != null){
			this.composer.onOcultarProducto(p);
		}
	}
	
}

class RefreshEventListener implements EventListener<Event>{

	AdministracionComposer composer;
	public RefreshEventListener(AdministracionComposer adm){
		this.composer = adm;
	}

	public void onEvent(Event event) throws Exception {
		if(event.getData() != null){
			if(event.getData() instanceof Categoria){
//				Categoria c = (Categoria) event.getData();
//				composer.getUsuarioLogueado().getCategorias().add(c);				
			}
			if(event.getData() instanceof Producto){
//				Producto p= (Producto) event.getData();
//				p.getCategoria().agregarProducto(p);
//				composer.getProductos().add(p);
			}
		}
		composer.refresh();
		
	}
	
}