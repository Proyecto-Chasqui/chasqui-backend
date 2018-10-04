package chasqui.test.builders;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Historial;
import chasqui.model.Notificacion;
import chasqui.model.Pedido;
import chasqui.security.Encrypter;

public class ClienteBuilder {
	
	private Encrypter encrypter;
	
	//Variables default
	private String token = "unToken";
	private String password = "password";
	private String EMAIL_CLIENTE = "unemail@gmail.com";
	private String nombreCliente = "unCliente";
	private String apellido = "unApellido";
	private String telefonoFijo = "12314124";
	private String telefonoMovil = "1234214124";
	private String nickName = "unNickName";
	private List<Direccion> direccionesAlternativas = new ArrayList<Direccion>();
	private Historial historialPedidos = null;
	private boolean enabled = true;
	private String idDIspositivo = null;
	private String imagenPerfil = null;
	private boolean isRoot = false;
	private List<Notificacion> notificaciones = new ArrayList<Notificacion>();
	private String rol = "cliente";
	private String username = "unUserName";
	private List<Pedido> pedidos = new ArrayList<Pedido>();
	
	public static ClienteBuilder unCliente(Encrypter pencripter){
		return new ClienteBuilder().conEncrypter(pencripter);
	}
	//caracteristicas distintivas de chasqui.
	public Cliente build() throws Exception{
		Cliente cliente = new Cliente();
		cliente.setToken(token);
		cliente.setPassword(encrypter.encrypt(password));
		cliente.setEmail(EMAIL_CLIENTE);
		cliente.setNombre(nombreCliente);
		cliente.setApellido(apellido);
		cliente.setTelefonoFijo(telefonoFijo);
		cliente.setTelefonoMovil(telefonoMovil);
		cliente.setUsername(nickName);
		cliente.setDireccionesAlternativas(direccionesAlternativas);
		cliente.setHistorialPedidos(historialPedidos);
		cliente.setEnabled(enabled);
		cliente.setIdDispositivo(idDIspositivo);
		cliente.setImagenPerfil(imagenPerfil);
		cliente.setIsRoot(isRoot);
		cliente.setNotificaciones(notificaciones);
		cliente.setRol(rol);
		cliente.setUsername(username);
		cliente.setPedidos(pedidos);
		return cliente;
	}
	
	public ClienteBuilder conEncrypter(Encrypter pEncrypter){
		this.encrypter = pEncrypter;
		return this;
	}
	
	public ClienteBuilder conPedidos(List<Pedido> pPedidos){
		this.pedidos = pPedidos;
		return this;
	}
	
	public ClienteBuilder conNombreDeUsuario(String pUsername){
		this.username = pUsername;
		return this;
	}
	
	public ClienteBuilder conRol(String pRol){
		this.rol = pRol;
		return this;
	}
	
	public ClienteBuilder conNotificaciones(List<Notificacion> pNotificaciones){
		this.notificaciones = pNotificaciones;
		return this;
	}
	
	public ClienteBuilder conIsRoot(Boolean bool){
		this.isRoot = bool;
		return this;
	}
	
	public ClienteBuilder conImagenPerfil(String pImagen){
		this.imagenPerfil = pImagen;
		return this;
	}
	
	public ClienteBuilder conIdDispositivo(String pIdDispositivo){
		this.idDIspositivo = pIdDispositivo;
		return this;
	}
	
	public ClienteBuilder conEnabled (boolean pEnabled){
		this.enabled = pEnabled;
		return this;
	}
	
	public ClienteBuilder conHistorial(Historial pHistorial){
		this.historialPedidos = pHistorial;
		return this;
	}
	
	public ClienteBuilder conDireccionesAlternativas(List<Direccion> pDirecciones){
		this.direccionesAlternativas = pDirecciones;
		return this;
	}
	
	public ClienteBuilder conNickName(String pNickName){
		this.nickName = pNickName;
		return this;
	}
	
	public ClienteBuilder conTelefonoMovil(String pTelefonoMovil){
		this.telefonoMovil = pTelefonoMovil;
		return this;
	}
	
	public ClienteBuilder conTelefonoFijo(String pTelefonoFijo){
		this.telefonoFijo = pTelefonoFijo;
		return this;
	}
	
	public ClienteBuilder conApellid(String pApellido){
		this.apellido = pApellido;
		return this;
	}
	
	public ClienteBuilder conNombre(String pNombre){
		this.nombreCliente = pNombre;
		return this;
	}
	
	public ClienteBuilder conEmail(String pEmail){
		this.EMAIL_CLIENTE = pEmail;
		return this;
	}

	public ClienteBuilder conPassword(String pPassword){
		this.password = pPassword;
		return this;
	}
	
	public ClienteBuilder conToken(String pToken){
		this.token = pToken;
		return this;
	}
	
	public ClienteBuilder conDireccion(Direccion direccion){
		this.direccionesAlternativas.add(direccion);
		return this;
	}
}
