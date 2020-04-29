package chasqui.model;

import chasqui.exceptions.InvitacionExistenteException;
import chasqui.view.composer.Constantes;

public class Nodo extends GrupoCC{
	
	private Integer id;
	private String tipo;
	private String emailAdministradorNodo;
	private Direccion direccionDelNodo;
	private Zona zona;
	private String barrio;
	private boolean activo;
	
	//Constructor
	public Nodo () {}
	@Deprecated
	public Nodo (Cliente administrador, String alias, String descripcion) {
		super(administrador, alias, descripcion);
		this.setTipo(Constantes.NODO_ABIERTO);
	}
	//creacion de nodo via solicitud.
	public Nodo (SolicitudCreacionNodo solicitud, Vendedor vendedor) {
		super((Cliente)solicitud.getUsuarioSolicitante(), solicitud.getNombreNodo(), solicitud.getDescripcion(), true);
		this.setBarrio(solicitud.getBarrio());
		this.setEmailAdministradorNodo(solicitud.getUsuarioSolicitante().getEmail());
		this.setDireccionDelNodo(solicitud.getDomicilio());
		this.setTipo(solicitud.getTipoNodo());
		this.setVendedor(vendedor);
		this.setActivo(true);
	}

	//Gets & Sets
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
//
//	public void setEstado(String estado) {
//		this.estado = estado;
//	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	

	public void abrirNodo() {
		this.tipo = Constantes.NODO_ABIERTO;
	}

	public void cerrarNodo() {
		this.tipo = Constantes.NODO_CERRADO;
	}

	public String getEmailAdministradorNodo() {
		return emailAdministradorNodo;
	}

	public void setEmailAdministradorNodo(String emailAdministradorNodo) {
		this.emailAdministradorNodo = emailAdministradorNodo;
	}

	public Direccion getDireccionDelNodo() {
		return direccionDelNodo;
	}

	public void setDireccionDelNodo(Direccion direccionDelNodo) {
		this.direccionDelNodo = direccionDelNodo;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public void invitarAlNodo(Cliente cliente) throws InvitacionExistenteException{
		MiembroDeGCC miembro = this.validarNuevoMiembro(cliente.getEmail(),cliente);
		miembro.setAvatar(cliente.getImagenPerfil());
		miembro.setNickname(cliente.getUsername());
		miembro.setIdCliente(cliente.getId());
		this.getCache().add(miembro);
	}

	private MiembroDeGCC validarNuevoMiembro(String emailCliente, Cliente cliente) {
		MiembroDeGCC miembro = this.findMiembro(emailCliente);

		if (miembro == null) {
			miembro = new MiembroDeGCC(cliente);
			miembro.setEstadoInvitacion(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA);
		} else {
			throw new InvitacionExistenteException("El cliente que pretende invitar ya existe en el nodo");
		}
		return miembro;

	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public Zona getZona() {
		return zona;
	}
	public void setZona(Zona zona) {
		this.zona = zona;
	}

	
}
