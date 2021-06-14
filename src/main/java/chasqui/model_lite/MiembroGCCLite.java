package chasqui.model_lite;

public class MiembroGCCLite {
  private Integer id;
	private String avatar;
	private String nickname;
	private String email;
	private String estadoInvitacion; 
	private String estadoPedido; 
	private Integer idGrupo;
  private ClienteLite cliente;


  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAvatar() {
    return this.avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEstadoInvitacion() {
    return this.estadoInvitacion;
  }

  public void setEstadoInvitacion(String estadoInvitacion) {
    this.estadoInvitacion = estadoInvitacion;
  }

  public String getEstadoPedido() {
    return this.estadoPedido;
  }

  public void setEstadoPedido(String estadoPedido) {
    this.estadoPedido = estadoPedido;
  }

  public Integer getIdGrupo() {
    return this.idGrupo;
  }

  public void setIdGrupo(Integer idGrupo) {
    this.idGrupo = idGrupo;
  }

  public ClienteLite getCliente() {
    return this.cliente;
  }

  public void setCliente(ClienteLite cliente) {
    this.cliente = cliente;
  }

}
