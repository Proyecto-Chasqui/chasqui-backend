package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.vividsolutions.jts.simplify.TaggedLineStringSimplifier;

import chasqui.model.Tag;
import chasqui.model.TagTipoOrganizacion;
import chasqui.model.TagTipoProducto;
import chasqui.model.TagZonaDeCobertura;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.TagService;
import chasqui.services.interfaces.UsuarioService;

@SuppressWarnings("serial")
public class ConfiguracionPropiedadesComposer extends GenericForwardComposer<Component>{
	
	private AnnotateDataBinder binder;
	private Vendedor vendedorLogueado;
	private TagService tagService;
	private UsuarioService usuarioService;
	private Component component;
	private Integer limiteDeTagsProductos = 5;
	private Integer limiteDeTagsOrganizacion = 1;
	private Integer limitedeTagsZonaCobertura = 3;
	private Label cant_prod;
	private Label cant_org;
	private Label cant_zonas;
	//data tab seleccion tipo productos
	private List<TagTipoProducto> tagsTipoProductos;
	private List<TagTipoProducto> tagsTipoProductosEnVendedor;
	private Listbox listboxtagsproductossrc;
	private Listbox listboxtagsproductosdst;
	private Button buttonMoveToRight;
	private Button buttonMoveToLeft;
	//data tab seleccion tipo organizacion
	private List<TagTipoOrganizacion> tagsTipoOrganizacion;
	private List<TagTipoOrganizacion> tagsTipoOrganizacionEnVendedor;
	private Listbox listboxtagsorganizacionsrc;
	private Listbox listboxtagsorganizaciondst;
	private Button buttonMoverTagOrganizacionAVendedor;
	private Button buttonQuitarTagOrganizacionDeVendedor;
	//data tab seleccion zona de cobertura
	private List<TagZonaDeCobertura> tagsZonaDeCobertura;
	private List<TagZonaDeCobertura> tagsZonaDeCoberturaEnVendedor;
	private Listbox listboxtagszonadecoberturasrc;
	private Listbox listboxtagszonadecoberturadst;
	private Button buttonMoverTagZonadeCoberturaAVendedor;
	private Button buttonQuitarTagZonadeCoberturaDeVendedor;

	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		this.component = comp;
		tagService = (TagService) SpringUtil.getBean("tagService");
		usuarioService =  (UsuarioService) SpringUtil.getBean("usuarioService");
		setVendedorLogueado((Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME));
		setTagsTipoProductos(tagService.obtenerTagsTipoProducto());
		setTagsTipoOrganizacion(tagService.obtenerTagTipoOrganizacion());
		setTagsZonaDeCobertura(tagService.obtenerTagZonaDeCobertura());
		tagsTipoProductosEnVendedor = vendedorLogueado.getTagsTipoProducto();
		tagsTipoOrganizacionEnVendedor = vendedorLogueado.getTagsTipoOrganizacion();
		tagsZonaDeCoberturaEnVendedor = vendedorLogueado.getTagsZonaCobertura();
		binder = new AnnotateDataBinder(comp);
		syncLists();
		binder.loadAll();
		deshabilitarBotonesSiEsNecesario();
		setLabels();
	}
	
	private void setLabels() {
		cant_org.setValue("Límite " + tagsTipoOrganizacionEnVendedor.size() + "/" + limiteDeTagsOrganizacion);
		cant_zonas.setValue("Límite " + tagsZonaDeCoberturaEnVendedor.size() + "/" + limitedeTagsZonaCobertura);
		cant_prod.setValue("Límite " + tagsTipoProductosEnVendedor.size() + "/" + limiteDeTagsProductos);
	}

	private void syncLists() {
		removeAllFrom(tagsTipoProductosEnVendedor,tagsTipoProductos);
		removeAllFrom(tagsTipoOrganizacionEnVendedor,tagsTipoOrganizacion);
		removeAllFrom(tagsZonaDeCoberturaEnVendedor,tagsZonaDeCobertura);
		binder.loadAll();
	}
	
	private void deshabilitarBotonesSiEsNecesario() {
		deshabilitarBotonSiLlegoALimite(tagsZonaDeCoberturaEnVendedor.size(), limitedeTagsZonaCobertura, buttonMoverTagZonadeCoberturaAVendedor);
		deshabilitarBotonSiLlegoALimite(tagsTipoOrganizacionEnVendedor.size(), limiteDeTagsOrganizacion, buttonMoverTagOrganizacionAVendedor);
		deshabilitarBotonSiLlegoALimite(tagsTipoProductosEnVendedor.size(), limiteDeTagsProductos, buttonMoveToRight);
	}

	private void deshabilitarBotonSiLlegoALimite(int size, int i, Button button) {
		if(size >= i) {
			button.setDisabled(true);
			button.setImage("/imagenes/flecha_der_red.png");
		}
	}
	
	private void habilitarBotonSiEstaPorDebajo(int size, int i, Button button) {
		if(size < i) {
			button.setDisabled(false);
			button.setImage("/imagenes/flecha_der.png");
		}
	}
	
	public void mostrarAlertaDeSeleccion() {
		Clients.showNotification("Debe primero seleccionar un tag",
				"error", component, "top_center",
				3000, true);
	}
	
	private void removeAllFrom(List<? extends Tag> tagsEnVendedor, List<? extends Tag>tagsEnSistema) {
		List<Tag> tagEncontrados = new ArrayList<Tag> ();
		for(Tag tagenvendedor : tagsEnVendedor) {
			for(Tag tagensistema: tagsEnSistema) {
				if(tagenvendedor.getId().equals(tagensistema.getId())) {
					tagEncontrados.add(tagensistema);
				}
			}
		}
		tagsEnSistema.removeAll(tagEncontrados);
	}

	//seccion funciones tag tipo de productos
	public void onClick$buttonMoveToRight() {
        Listitem s = listboxtagsproductossrc.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        } else {
	            s.setParent(listboxtagsproductosdst);
	            List<Component> list= (List<Component>) s.getChildren();
	            Listcell cell = (Listcell) list.get(1);
	            Integer id = Integer.parseInt(cell.getLabel());
	            moverTagAVendedor((TagTipoProducto) encontrarTag(id, tagsTipoProductos));
	            deshabilitarBotonSiLlegoALimite(tagsTipoProductosEnVendedor.size(), limiteDeTagsProductos, buttonMoveToRight);
	            setLabels();
        }
	}

	public void onClick$buttonMoveToLeft() {
        Listitem s = listboxtagsproductosdst.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        }else {
            s.setParent(listboxtagsproductossrc);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            retirarTagDeVendedor((TagTipoProducto) encontrarTag(id, tagsTipoProductosEnVendedor));
            habilitarBotonSiEstaPorDebajo(tagsTipoProductosEnVendedor.size(), limiteDeTagsProductos, buttonMoveToRight);
            setLabels();
        }
	}
	
	private void moverTagAVendedor(TagTipoProducto tag) {
		tagsTipoProductos.remove(tag);
		tagsTipoProductosEnVendedor.add(tag);
		this.guardarVendedor();
	}
	
	private void guardarVendedor() {
		vendedorLogueado.setTagsTipoProducto(tagsTipoProductosEnVendedor);
		usuarioService.guardarUsuario(vendedorLogueado);
	}
	
	private void retirarTagDeVendedor(TagTipoProducto tag) {
		tagsTipoProductosEnVendedor.remove(tag);
		tagsTipoProductos.add(tag);
		this.guardarVendedor();
	}
	
	
	private Tag encontrarTag(Integer id, List<? extends Tag> tags) {
		Tag rtag = null;
		for(Tag tag : tags) {
			if(tag.getId().equals(id)) {
				return tag;
			}
		}
		
		return rtag;
	}

	//seccion funciones tag tipo organizacion
	public void onClick$buttonMoverTagOrganizacionAVendedor() {
        Listitem s = listboxtagsorganizacionsrc.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        } else {
            s.setParent(listboxtagsorganizaciondst);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            moverTagOrganizacionAVendedor((TagTipoOrganizacion) encontrarTag(id, tagsTipoOrganizacion));
            deshabilitarBotonSiLlegoALimite(tagsTipoOrganizacionEnVendedor.size(), limiteDeTagsOrganizacion, buttonMoverTagOrganizacionAVendedor);
            setLabels();
        }
	}

	public void onClick$buttonQuitarTagOrganizacionDeVendedor() {
        Listitem s = listboxtagsorganizaciondst.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        }else {
            s.setParent(listboxtagsorganizacionsrc);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            retirarTagOrganizacionDeVendedor((TagTipoOrganizacion) encontrarTag(id, tagsTipoOrganizacionEnVendedor));
            habilitarBotonSiEstaPorDebajo(tagsTipoOrganizacionEnVendedor.size(), limiteDeTagsOrganizacion, buttonMoverTagOrganizacionAVendedor);
            setLabels();
        }
	}
	
	private void moverTagOrganizacionAVendedor(TagTipoOrganizacion tag) {
		tagsTipoOrganizacion.remove(tag);
		tagsTipoOrganizacionEnVendedor.add(tag);
		this.guardarTagsOrganizacionVendedor();
	}
	
	private void guardarTagsOrganizacionVendedor() {
		vendedorLogueado.setTagsTipoOrganizacion(tagsTipoOrganizacionEnVendedor);
		usuarioService.guardarUsuario(vendedorLogueado);
	}
	
	private void retirarTagOrganizacionDeVendedor(TagTipoOrganizacion tag) {
		tagsTipoOrganizacionEnVendedor.remove(tag);
		tagsTipoOrganizacion.add(tag);
		this.guardarTagsOrganizacionVendedor();
	}

	//seccion funciones tag zona de cobertura
	public void onClick$buttonMoverTagZonadeCoberturaAVendedor() {
        Listitem s = listboxtagszonadecoberturasrc.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        } else {
            s.setParent(listboxtagszonadecoberturadst);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            moverTagZonaDeCoberturaAVendedor((TagZonaDeCobertura) encontrarTag(id, tagsZonaDeCobertura));
            deshabilitarBotonSiLlegoALimite(tagsZonaDeCoberturaEnVendedor.size(), limitedeTagsZonaCobertura, buttonMoverTagZonadeCoberturaAVendedor);
            setLabels();
        }
	}
	

	public void onClick$buttonQuitarTagZonaDeCoberturaDeVendedor() {
        Listitem s = listboxtagszonadecoberturadst.getSelectedItem();
        if (s == null) {
        	mostrarAlertaDeSeleccion();
        }else {
            s.setParent(listboxtagszonadecoberturasrc);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            retirarTagZonaDeCoberturaDeVendedor((TagZonaDeCobertura) encontrarTag(id, tagsZonaDeCoberturaEnVendedor));
            habilitarBotonSiEstaPorDebajo(tagsZonaDeCoberturaEnVendedor.size(), limitedeTagsZonaCobertura, buttonMoverTagZonadeCoberturaAVendedor);
            setLabels();
        }
	}
	
	private void moverTagZonaDeCoberturaAVendedor(TagZonaDeCobertura tag) {
		tagsZonaDeCobertura.remove(tag);
		tagsZonaDeCoberturaEnVendedor.add(tag);
		this.guardarTagsZonaDeCoberturaVendedor();
	}
	
	private void guardarTagsZonaDeCoberturaVendedor() {
		vendedorLogueado.setTagsZonaCobertura(tagsZonaDeCoberturaEnVendedor);
		usuarioService.guardarUsuario(vendedorLogueado);
	}
	
	private void retirarTagZonaDeCoberturaDeVendedor(TagZonaDeCobertura tag) {
		tagsZonaDeCoberturaEnVendedor.remove(tag);
		tagsZonaDeCobertura.add(tag);
		this.guardarTagsOrganizacionVendedor();
	}
	
	//getter and setters
	public Vendedor getVendedorLogueado() {
		return vendedorLogueado;
	}

	public void setVendedorLogueado(Vendedor vendedorLogueado) {
		this.vendedorLogueado = vendedorLogueado;
	}

	public Listbox getListboxtagsproductossrc() {
		return listboxtagsproductossrc;
	}

	public void setListboxtagsproductossrc(Listbox listboxtagsproductossrc) {
		this.listboxtagsproductossrc = listboxtagsproductossrc;
	}


	public Listbox getListboxtagsproductosdst() {
		return listboxtagsproductosdst;
	}


	public void setListboxtagsproductosdst(Listbox listboxtagsproductosdst) {
		this.listboxtagsproductosdst = listboxtagsproductosdst;
	}

	public Button getButtonMoveToRight() {
		return buttonMoveToRight;
	}

	public void setButtonMoveToRight(Button buttonMoveToRight) {
		this.buttonMoveToRight = buttonMoveToRight;
	}

	public Button getButtonMoveToLeft() {
		return buttonMoveToLeft;
	}

	public void setButtonMoveToLeft(Button buttonMoveToLeft) {
		this.buttonMoveToLeft = buttonMoveToLeft;
	}

	public List<TagTipoProducto> getTagsTipoProductos() {
		return tagsTipoProductos;
	}

	public void setTagsTipoProductos(List<TagTipoProducto> tagsTipoProductos) {
		this.tagsTipoProductos = tagsTipoProductos;
	}

	public List<TagTipoProducto> getTagsTipoProductosEnVendedor() {
		return tagsTipoProductosEnVendedor;
	}

	public void setTagsTipoProductosEnVendedor(List<TagTipoProducto> tagsTipoProductosEnVendedor) {
		this.tagsTipoProductosEnVendedor = tagsTipoProductosEnVendedor;
	}

	public List<TagTipoOrganizacion> getTagsTipoOrganizacion() {
		return tagsTipoOrganizacion;
	}

	public List<TagTipoOrganizacion> getTagsTipoOrganizacionEnVendedor() {
		return tagsTipoOrganizacionEnVendedor;
	}

	public Listbox getListboxtagsorganizacionsrc() {
		return listboxtagsorganizacionsrc;
	}

	public Listbox getListboxtagsorganizaciondst() {
		return listboxtagsorganizaciondst;
	}

	public Button getButtonMoverTagOrganizacionAVendedor() {
		return buttonMoverTagOrganizacionAVendedor;
	}

	public Button getButtonQuitarTagOrganizacionDeVendedor() {
		return buttonQuitarTagOrganizacionDeVendedor;
	}

	public void setTagsTipoOrganizacion(List<TagTipoOrganizacion> tagsTipoOrganizacion) {
		this.tagsTipoOrganizacion = tagsTipoOrganizacion;
	}

	public void setTagsTipoOrganizacionEnVendedor(List<TagTipoOrganizacion> tagsTipoOrganizacionEnVendedor) {
		this.tagsTipoOrganizacionEnVendedor = tagsTipoOrganizacionEnVendedor;
	}

	public void setListboxtagsorganizacionsrc(Listbox listboxtagsorganizacionsrc) {
		this.listboxtagsorganizacionsrc = listboxtagsorganizacionsrc;
	}

	public void setListboxtagsorganizaciondst(Listbox listboxtagsorganizaciondst) {
		this.listboxtagsorganizaciondst = listboxtagsorganizaciondst;
	}

	public void setButtonMoverTagOrganizacionAVendedor(Button buttonMoverTagOrganizacionAVendedor) {
		this.buttonMoverTagOrganizacionAVendedor = buttonMoverTagOrganizacionAVendedor;
	}

	public void setButtonQuitarTagOrganizacionDeVendedor(Button buttonQuitarTagOrganizacionDeVendedor) {
		this.buttonQuitarTagOrganizacionDeVendedor = buttonQuitarTagOrganizacionDeVendedor;
	}

	public List<TagZonaDeCobertura> getTagsZonaDeCobertura() {
		return tagsZonaDeCobertura;
	}

	public List<TagZonaDeCobertura> getTagsZonaDeCoberturaEnVendedor() {
		return tagsZonaDeCoberturaEnVendedor;
	}

	public Listbox getListboxtagszonadecoberturasrc() {
		return listboxtagszonadecoberturasrc;
	}

	public Listbox getListboxtagszonadecoberturadst() {
		return listboxtagszonadecoberturadst;
	}

	public Button getButtonMoverTagZonadeCoberturaAVendedor() {
		return buttonMoverTagZonadeCoberturaAVendedor;
	}

	public Button getButtonQuitarTagZonadeCoberturaDeVendedor() {
		return buttonQuitarTagZonadeCoberturaDeVendedor;
	}

	public Label getCant_prod() {
		return cant_prod;
	}

	public Label getCant_org() {
		return cant_org;
	}

	public Label getCant_zonas() {
		return cant_zonas;
	}

	public void setCant_prod(Label cant_prod) {
		this.cant_prod = cant_prod;
	}

	public void setCant_org(Label cant_org) {
		this.cant_org = cant_org;
	}

	public void setCant_zonas(Label cant_zonas) {
		this.cant_zonas = cant_zonas;
	}

	public void setTagsZonaDeCobertura(List<TagZonaDeCobertura> tagsZonaDeCobertura) {
		this.tagsZonaDeCobertura = tagsZonaDeCobertura;
	}

	public void setTagsZonaDeCoberturaEnVendedor(List<TagZonaDeCobertura> tagsZonaDeCoberturaEnVendedor) {
		this.tagsZonaDeCoberturaEnVendedor = tagsZonaDeCoberturaEnVendedor;
	}

	public void setListboxtagszonadecoberturasrc(Listbox listboxtagszonadecoberturasrc) {
		this.listboxtagszonadecoberturasrc = listboxtagszonadecoberturasrc;
	}

	public void setListboxtagszonadecoberturadst(Listbox listboxtagszonadecoberturadst) {
		this.listboxtagszonadecoberturadst = listboxtagszonadecoberturadst;
	}

	public void setButtonMoverTagZonadeCoberturaAVendedor(Button buttonMoverTagZonadeCoberturaAVendedor) {
		this.buttonMoverTagZonadeCoberturaAVendedor = buttonMoverTagZonadeCoberturaAVendedor;
	}

	public void setButtonQuitarTagZonadeCoberturaDeVendedor(Button buttonQuitarTagZonadeCoberturaDeVendedor) {
		this.buttonQuitarTagZonadeCoberturaDeVendedor = buttonQuitarTagZonadeCoberturaDeVendedor;
	}
}
