package chasqui.model_lite;

import chasqui.model.Direccion;
import chasqui.model.Zona;

public class NodoLite extends GrupoCCLite {
  private String tipo;
  private String emailAdministrador;
  private String barrio;
  private boolean activo;
  private Direccion direccionDelNodo;
  private Zona zona;

  public NodoLite alias(String alias) {
    setAlias(alias);
    return this;
  }

  public NodoLite descripcion(String descripcion) {
    setDescripcion(descripcion);
    return this;
  }

  public NodoLite tipo(String tipo) {
    setTipo(tipo);
    return this;
  }

  public NodoLite emailAdministrador(String emailAdministrador) {
    setEmailAdministrador(emailAdministrador);
    return this;
  }

  public NodoLite barrio(String barrio) {
    setBarrio(barrio);
    return this;
  }

  public NodoLite activo(boolean activo) {
    setActivo(activo);
    return this;
  }

  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getEmailAdministrador() {
    return this.emailAdministrador;
  }

  public void setEmailAdministrador(String emailAdministrador) {
    this.emailAdministrador = emailAdministrador;
  }

  public String getBarrio() {
    return this.barrio;
  }

  public void setBarrio(String barrio) {
    this.barrio = barrio;
  }

  public boolean getActivo() {
    return this.activo;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }

  public Direccion getDireccionDelNodo() {
    return this.direccionDelNodo;
  }

  public void setDireccionDelNodo(Direccion direccionDelNodo) {
    this.direccionDelNodo = direccionDelNodo;
  }

  public Zona getZona() {
    return this.zona;
  }

  public void setZona(Zona zona) {
    this.zona = zona;
  }
}