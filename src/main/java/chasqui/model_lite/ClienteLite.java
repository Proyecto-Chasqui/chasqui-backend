package chasqui.model_lite;

public class ClienteLite {
  private Integer id;
  private String nombre;
  private String apellido;
  private String telefonoFijo;
  private String telefonoMovil;
  private String estado;
  private String imagenPerfil;
  private String email;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNombre() {
    return this.nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return this.apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String getTelefonoFijo() {
    return this.telefonoFijo;
  }

  public void setTelefonoFijo(String telefonoFijo) {
    this.telefonoFijo = telefonoFijo;
  }

  public String getTelefonoMovil() {
    return this.telefonoMovil;
  }

  public void setTelefonoMovil(String telefonoMovil) {
    this.telefonoMovil = telefonoMovil;
  }

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getImagenPerfil() {
    return this.imagenPerfil;
  }

  public void setImagenPerfil(String imagenPerfil) {
    this.imagenPerfil = imagenPerfil;
  }


  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
