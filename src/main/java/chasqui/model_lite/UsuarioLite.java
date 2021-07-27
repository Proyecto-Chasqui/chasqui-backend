package chasqui.model_lite;

public class UsuarioLite {
  private Integer id;
  private String username;
  private String email;
  private String imagenPerfil;
  private Boolean enabled;
  private Boolean root;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getImagenPerfil() {
    return this.imagenPerfil;
  }

  public void setImagenPerfil(String imagenPerfil) {
    this.imagenPerfil = imagenPerfil;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean getRoot() {
    return this.root;
  }

  public void setRoot(Boolean root) {
    this.root = root;
  }

}
