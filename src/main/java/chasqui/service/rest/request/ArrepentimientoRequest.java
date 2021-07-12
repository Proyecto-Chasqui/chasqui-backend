package chasqui.service.rest.request;

public class ArrepentimientoRequest {
  private String nombre;
  private String email;
  private String telefono;
  private String comentario;
  private String nombreVendedor;


  public String getNombre() {
    return this.nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefono() {
    return this.telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getComentario() {
    return this.comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
  }

  public String getNombreVendedor() {
    return this.nombreVendedor;
  }

  public void setNombreVendedor(String nombreVendedor) {
    this.nombreVendedor = nombreVendedor;
  }




}
