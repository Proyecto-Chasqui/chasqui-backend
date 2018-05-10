package chasqui.view.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.UsuarioService;


@SuppressWarnings("serial")
public class ABMCuestionarioComposer extends GenericForwardComposer<Component>{
	//Variables generales
	Vendedor usuario;
	UsuarioService usuarioService;
	private AnnotateDataBinder binder;
	//Variables de selección de preguntas
	private Component mostrarNuevaPregunta;
	private List<PreguntaDeConsumo> preguntasIndividualesDeConsumo;
	private List<PreguntaDeConsumo> preguntasColectivasDeConsumo;
	private PreguntaDeConsumo preguntaIndividualSeleccionada;
	private PreguntaDeConsumo preguntaColectivaSeleccionada;
	//variables de edición de preguntas
	private Component editarPregunta; 
	private PreguntaDeConsumo preguntaAEditar;
	private String respuestaSeleccionada;
	private List<String> respuestasDeConsumo;
	private Textbox textNombrePregunta;
	private Textbox textNombreRespuesta;
	private boolean isPreguntaIndividual = true;
	private Integer indice = -1;
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		usuario = usuarioService.obtenerVendedorPorID(((Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME)).getId());
		preguntasIndividualesDeConsumo = usuario.getPreguntasDePedidosIndividuales();
		preguntasColectivasDeConsumo = usuario.getPreguntasDePedidosColectivos();
		this.editarPregunta.setVisible(false);
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();
	}
	
	//Area de metodos para selección de preguntas
	
	private void cambiarContextoAEditar(){
		this.mostrarNuevaPregunta.setVisible(false);
		this.editarPregunta.setVisible(true);
		binder.loadAll();
	}
	
	private void guardarEnBD(){
		this.usuarioService.guardarUsuario(usuario);
		binder.loadAll();
	}
	
	private void limpiarCampos(){
		respuestaSeleccionada = null;
		if(respuestasDeConsumo != null){
			respuestasDeConsumo=null;
		}
		textNombrePregunta.setValue("");
		textNombreRespuesta.setValue("");
		preguntaIndividualSeleccionada = null;
		preguntaColectivaSeleccionada = null;
		binder.loadAll();
	}
	
	public void onEditarPreguntaIndividual(){
			this.indice = this.preguntasIndividualesDeConsumo.indexOf(preguntaIndividualSeleccionada);
			this.preguntaAEditar = preguntaIndividualSeleccionada;
			this.textNombrePregunta.setValue(this.preguntaIndividualSeleccionada.getNombre());
			this.respuestasDeConsumo = this.preguntaIndividualSeleccionada.getOpciones();
			this.isPreguntaIndividual = true;
			this.cambiarContextoAEditar();
	}

	public void onEditarPreguntaColectiva(){
		this.indice = this.preguntasColectivasDeConsumo.indexOf(preguntaColectivaSeleccionada);
		this.preguntaAEditar = preguntaColectivaSeleccionada;
		this.textNombrePregunta.setValue(this.preguntaColectivaSeleccionada.getNombre());
		this.respuestasDeConsumo = this.preguntaColectivaSeleccionada.getOpciones();
		this.isPreguntaIndividual = false;
		this.cambiarContextoAEditar();
	}
	
	private void showError(String string) {
		Messagebox.show(string,"Advertencia" ,Messagebox.OK,Messagebox.INFORMATION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.OK:
					
				}				
			}

			});		
	}
	
	//Area de metodos para la edición/creación de preguntas
	
	private void cambiarContextoAPreguntas(){
		this.mostrarNuevaPregunta.setVisible(true);
		this.editarPregunta.setVisible(false);
	}
	
	public void onClick$btnAgregarPreguntaIndividual(){
		if(preguntasIndividualesDeConsumo.size() <=4){
			this.cambiarContextoAEditar();
			this.indice = -1;
			this.isPreguntaIndividual = true;
			this.limpiarCampos();
			this.respuestasDeConsumo = new ArrayList<String>();
		}else{
			showError("No se puede tener mas de 5 preguntas de consumo individual");
		}
	}
	
	public void onClick$btnAgregarPreguntaColectiva(){
		if(preguntasColectivasDeConsumo.size() <=4){
			this.cambiarContextoAEditar();
			this.indice = -1;
			this.isPreguntaIndividual = false;
			this.limpiarCampos();
			this.respuestasDeConsumo = new ArrayList<String>();
		}else{
			showError("No se puede tener mas de 5 preguntas de consumo colectivo");
		}
	}
	
	public void onClick$btnGuardarRespuesta(){
		if(!textNombreRespuesta.getValue().equals("")){
			if(respuestasDeConsumo.size()<15){
				this.respuestasDeConsumo.add(textNombreRespuesta.getValue());
				textNombreRespuesta.setValue("");
				binder.loadAll();
			}else{
				showError("No se puede tener mas de 15 respuestas por preguntas");
			}
		}else{
			Messagebox.show("La respuesta no debe ser vacia","Pregunta",Messagebox.OK,Messagebox.INFORMATION,
					new EventListener<Event>(){

				public void onEvent(Event event) throws Exception {
					switch (((Integer) event.getData()).intValue()){
					case Messagebox.OK:
						
					}				
				}

				});
		}	
	}
	
	
	
	public void onClick$btnGuardarCambios(){
		if(isPreguntaIndividual){
			this.guardarPreguntaIndividual();
		}else{
			this.guardarPreguntaColectiva();
		}
		cambiarContextoAPreguntas();
		binder.loadAll();
	}
	
	public void onClick$btnCancelarCambios() throws VendedorInexistenteException{
		this.limpiarCampos();
		this.cambiarContextoAPreguntas();
		this.recargarListas();
		binder.loadAll();
	}
	
	private void recargarListas() throws VendedorInexistenteException{
		this.usuario = usuarioService.obtenerVendedorPorID(usuario.getId());
		this.preguntasColectivasDeConsumo = usuario.getPreguntasDePedidosColectivos();
		this.preguntasIndividualesDeConsumo = usuario.getPreguntasDePedidosIndividuales();
	}
	
	public void onEliminarRespuesta(){
		if(this.preguntaAEditar != null){
		List<String> list = new ArrayList<String>();
		list.addAll(respuestasDeConsumo);
		list.remove(respuestaSeleccionada);
		this.respuestasDeConsumo.clear();
		this.respuestasDeConsumo.addAll(list);
		}else{
			respuestasDeConsumo.remove(respuestaSeleccionada);
		}
		binder.loadAll();
	}
	
	private void guardarPreguntaColectiva() {
		if(indice.equals(-1)){
			PreguntaDeConsumo pc = new PreguntaDeConsumo(this.textNombrePregunta.getValue(),true,this.respuestasDeConsumo);
			this.preguntasColectivasDeConsumo.add(pc);
		}else{
			preguntaColectivaSeleccionada.setNombre(textNombrePregunta.getValue());
			preguntaColectivaSeleccionada.setHabilitada(true);
			preguntaColectivaSeleccionada.setOpciones(this.respuestasDeConsumo);
		}
		guardarEnBD();
	}
	
	public void onOK() {
		this.onClick$btnGuardarRespuesta();  
	}
	
	public void onHabilitarPreguntaColectiva(){
		this.preguntaColectivaSeleccionada.setHabilitada(!preguntaColectivaSeleccionada.getHabilitada());
		guardarEnBD();
	}
	
	public void onHabilitarPreguntaIndividual(){
		this.preguntaIndividualSeleccionada.setHabilitada(!preguntaIndividualSeleccionada.getHabilitada());
		guardarEnBD();
	}
	
	public void onEliminarPreguntaColectiva(){
		Messagebox.show("¿Está seguro que desea eliminar la pregunta " +preguntaColectivaSeleccionada.getNombre()+" ?","Pregunta",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					preguntasColectivasDeConsumo.remove(preguntaColectivaSeleccionada);
					guardarEnBD();
					binder.loadAll();
				case Messagebox.NO:
				}				
			}

			});

	}
	
	public void onEliminarPreguntaIndividual(){
		
		Messagebox.show("¿Está seguro que desea eliminar la pregunta " +preguntaIndividualSeleccionada.getNombre()+" ?","Pregunta",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION,
				new EventListener<Event>(){

			public void onEvent(Event event) throws Exception {
				switch (((Integer) event.getData()).intValue()){
				case Messagebox.YES:
					preguntasIndividualesDeConsumo.remove(preguntaIndividualSeleccionada);
					guardarEnBD();
					binder.loadAll();
				case Messagebox.NO:
				}				
			}

			});

	}
	
	
	private void guardarPreguntaIndividual() {		
		if(indice.equals(-1)){
			PreguntaDeConsumo pc = new PreguntaDeConsumo(this.textNombrePregunta.getValue(),true,this.respuestasDeConsumo);
			this.preguntasIndividualesDeConsumo.add(pc);
		}else{
			preguntaIndividualSeleccionada.setNombre(textNombrePregunta.getValue());
			preguntaIndividualSeleccionada.setHabilitada(true);
			preguntaIndividualSeleccionada.setOpciones(this.respuestasDeConsumo);
		}
		guardarEnBD();
	}
	
	
	
	//getters setters

	
	public void onClick$btnNuevaPregunta() throws VendedorInexistenteException{
		Window w = (Window) Executions.createComponents("/ABMPregunta.zul", this.self, null);
		w.doModal();	
	}

	public Component getMostrarNuevaPregunta() {
		return mostrarNuevaPregunta;
	}

	public void setMostrarNuevaPregunta(Component mostrarNuevaPregunta) {
		this.mostrarNuevaPregunta = mostrarNuevaPregunta;
	}

	public Component getEditarPregunta() {
		return editarPregunta;
	}

	public void setEditarPregunta(Component editarPregunta) {
		this.editarPregunta = editarPregunta;
	}

	public String getRespuestaSeleccionada() {
		return respuestaSeleccionada;
	}

	public void setRespuestaSeleccionada(String respuestaSeleccionada) {
		this.respuestaSeleccionada = respuestaSeleccionada;
	}

	public List<String> getRespuestasDeConsumo() {
		return respuestasDeConsumo;
	}

	public void setRespuestasDeConsumo(List<String> respuestasDeConsumo) {
		this.respuestasDeConsumo = respuestasDeConsumo;
	}

	public List<PreguntaDeConsumo> getPreguntasIndividualesDeConsumo() {
		return preguntasIndividualesDeConsumo;
	}

	public void setPreguntasIndividualesDeConsumo(List<PreguntaDeConsumo> preguntasIndividualesDeConsumo) {
		this.preguntasIndividualesDeConsumo = preguntasIndividualesDeConsumo;
	}

	public List<PreguntaDeConsumo> getPreguntasColectivasDeConsumo() {
		return preguntasColectivasDeConsumo;
	}

	public void setPreguntasColectivasDeConsumo(List<PreguntaDeConsumo> preguntasColectivasDeConsumo) {
		this.preguntasColectivasDeConsumo = preguntasColectivasDeConsumo;
	}

	public PreguntaDeConsumo getPreguntaIndividualSeleccionada() {
		return preguntaIndividualSeleccionada;
	}

	public void setPreguntaIndividualSeleccionada(PreguntaDeConsumo preguntaIndividualSeleccionada) {
		this.preguntaIndividualSeleccionada = preguntaIndividualSeleccionada;
	}

	public PreguntaDeConsumo getPreguntaColectivaSeleccionada() {
		return preguntaColectivaSeleccionada;
	}

	public void setPreguntaColectivaSeleccionada(PreguntaDeConsumo preguntaColectivaSeleccionada) {
		this.preguntaColectivaSeleccionada = preguntaColectivaSeleccionada;
	}

	public boolean isPreguntaIndividual() {
		return isPreguntaIndividual;
	}

	public void setPreguntaIndividual(boolean isPreguntaIndividual) {
		this.isPreguntaIndividual = isPreguntaIndividual;
	}

	public Textbox getTextNombreRespuesta() {
		return textNombreRespuesta;
	}

	public void setTextNombreRespuesta(Textbox textNombreRespuesta) {
		this.textNombreRespuesta = textNombreRespuesta;
	}




}
