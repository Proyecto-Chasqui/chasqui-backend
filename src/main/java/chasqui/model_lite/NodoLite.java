package chasqui.model_lite;

public class NodoLite extends GrupoCCLite {
	private String tipo;
  private String emailAdministradorNodo;
	private String barrio;
  private boolean activo;

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

  public NodoLite emailAdministradorNodo(String emailAdministradorNodo) {
    setEmailAdministradorNodo(emailAdministradorNodo);
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

  public String getEmailAdministradorNodo() {
    return this.emailAdministradorNodo;
  }

  public void setEmailAdministradorNodo(String emailAdministradorNodo) {
    this.emailAdministradorNodo = emailAdministradorNodo;
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


}
