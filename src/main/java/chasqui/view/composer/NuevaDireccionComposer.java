package chasqui.view.composer;

import java.util.ArrayList;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

import com.vividsolutions.jts.geom.Point;

import chasqui.model.Direccion;
import chasqui.model.Imagen;
import chasqui.model.Producto;
import chasqui.model.Variante;
import chasqui.model.Vendedor;
import chasqui.services.impl.FileSaver;
import chasqui.view.genericEvents.RefreshListener;
import chasqui.view.genericEvents.Refresher;
import chasqui.view.renders.ImagenesRender;


public class NuevaDireccionComposer extends GenericForwardComposer<Component> implements Refresher{
	
	ABMNodoComposer composer;
	private Direccion direccionDeNodo;
	private Textbox txtAliasDireccion;
	private Textbox txtCalle;
	private Textbox txtLocalidad;
	private Intbox txtAltura;
	private Intbox txtCodigoPostal;
	private Textbox txtDepartamento;

	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		composer = (ABMNodoComposer) Executions.getCurrent().getArg().get("abmComposer");
		direccionDeNodo = (Direccion) Executions.getCurrent().getSession().getAttribute("direccionNodo");
	}
	
	public void onClick$Guardar(){
		this.validar();
		direccionDeNodo = new Direccion();
		direccionDeNodo.setAlias(txtAliasDireccion.getValue());
		direccionDeNodo.setAltura(txtAltura.getValue());
		direccionDeNodo.setCalle(txtCalle.getValue());
		direccionDeNodo.setCodigoPostal(txtCodigoPostal.getValue().toString());
		direccionDeNodo.setLocalidad(txtLocalidad.getValue().toString());
		direccionDeNodo.setDepartamento(txtDepartamento.getValue());
		Executions.getCurrent().getSession().setAttribute("direccionNodo", direccionDeNodo);
		composer.agregarDireccionACombo();
		this.self.detach();
	}
	
	public void onClick$Cancelar(){
		this.self.detach();
	}
	
	public void validar(){
		
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public Intbox getTxtCodigoPostal() {
		return txtCodigoPostal;
	}

	public void setTxtCodigoPostal(Intbox txtCodigoPostal) {
		this.txtCodigoPostal = txtCodigoPostal;
	}

	public Direccion getDireccionDeNodo() {
		return direccionDeNodo;
	}

	public void setDireccionDeNodo(Direccion direccionDeNodo) {
		this.direccionDeNodo = direccionDeNodo;
	}

	public Textbox getTxtAliasDireccion() {
		return txtAliasDireccion;
	}

	public void setTxtAliasDireccion(Textbox txtAliasDireccion) {
		this.txtAliasDireccion = txtAliasDireccion;
	}

	public Textbox getTxtCalle() {
		return txtCalle;
	}

	public void setTxtCalle(Textbox txtCalle) {
		this.txtCalle = txtCalle;
	}

	public Textbox getTxtLocalidad() {
		return txtLocalidad;
	}

	public void setTxtLocalidad(Textbox txtLocalidad) {
		this.txtLocalidad = txtLocalidad;
	}

	public Intbox getTxtAltura() {
		return txtAltura;
	}

	public void setTxtAltura(Intbox txtAltura) {
		this.txtAltura = txtAltura;
	}

	public Textbox getTxtDepartamento() {
		return txtDepartamento;
	}

	public void setTxtDepartamento(Textbox txtDepartamento) {
		this.txtDepartamento = txtDepartamento;
	}

}
