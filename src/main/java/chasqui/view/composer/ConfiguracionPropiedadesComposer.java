package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import chasqui.model.Tag;
import chasqui.model.TagTipoProducto;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.TagService;
import chasqui.services.interfaces.UsuarioService;

@SuppressWarnings("serial")
public class ConfiguracionPropiedadesComposer extends GenericForwardComposer<Component>{
	
	private AnnotateDataBinder binder;
	private Vendedor vendedorLogueado;
	private List<TagTipoProducto> tagsTipoProductos;
	private List<TagTipoProducto> tagsTipoProductosEnVendedor;
	private Listbox listboxtagsproductossrc;
	private Listbox listboxtagsproductosdst;
	private Button buttonMoveToRight;
	private Button buttonMoveToLeft;
	private TagService tagService;
	private UsuarioService usuarioService;
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		tagService = (TagService) SpringUtil.getBean("tagService");
		usuarioService =  (UsuarioService) SpringUtil.getBean("usuarioService");
		setVendedorLogueado((Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME));
		setTagsTipoProductos(tagService.obtenerTagsTipoProducto());
		tagsTipoProductosEnVendedor = vendedorLogueado.getTagsTipoProducto();
		binder = new AnnotateDataBinder(comp);
		syncLists();
	}
	
	private void syncLists() {
		List<Tag> tags = new ArrayList<Tag> ();
		for(Tag tagenvendedor : tagsTipoProductosEnVendedor) {
			for(Tag tagensistema: tagsTipoProductos) {
				if(tagenvendedor.getId().equals(tagensistema.getId())) {
					tags.add(tagensistema);
				}
			}
		}
		tagsTipoProductos.removeAll(tags);
		binder.loadAll();
	}

	public void onClick$buttonMoveToRight() {
        Listitem s = listboxtagsproductossrc.getSelectedItem();
        if (s == null) {
            Messagebox.show("Select an item first");
        } else {
            s.setParent(listboxtagsproductosdst);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            moverTagAVendedor(encontrarTagTipoProducto(id));
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
	
	private TagTipoProducto encontrarTagTipoProducto(Integer id) {
		TagTipoProducto rtag = null;
		for(TagTipoProducto tag : tagsTipoProductos) {
			if(tag.getId().equals(id)) {
				return tag;
			}
		}
		
		return rtag;
	}
	
	private TagTipoProducto encontrarTagTipoProductoEnVendedor(Integer id) {
		TagTipoProducto rtag = null;
		for(TagTipoProducto tag : tagsTipoProductosEnVendedor) {
			if(tag.getId().equals(id)) {
				return tag;
			}
		}
		
		return rtag;
	}
	
	public void onMoveToRight() {
		this.onClick$buttonMoveToRight();
	}
	
	public void onClick$buttonMoveToLeft() {
        Listitem s = listboxtagsproductosdst.getSelectedItem();
        if (s == null) {
            Messagebox.show("Select an item first");
        }else {
            s.setParent(listboxtagsproductossrc);
            List<Component> list= (List<Component>) s.getChildren();
            Listcell cell = (Listcell) list.get(1);
            Integer id = Integer.parseInt(cell.getLabel());
            retirarTagDeVendedor(encontrarTagTipoProductoEnVendedor(id));
        }
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
}
