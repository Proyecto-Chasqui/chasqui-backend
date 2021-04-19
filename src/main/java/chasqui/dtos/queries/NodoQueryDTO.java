package chasqui.dtos.queries;

import java.util.Date;

public class NodoQueryDTO extends PaginatedQuery {
  private Integer idVendedor;
  private Integer idNodo;
  private Date desde;
  private Date hasta;
  private String estado;
  private String nombre;
  private String emailCoordinador;
  private String barrio;
  private String tipo;
  private Integer idZona;

  public Integer getIdVendedor() {
    return this.idVendedor;
  }

  public void setIdVendedor(Integer idVendedor) {
    this.idVendedor = idVendedor;
  }

  public Integer getIdNodo() {
    return this.idNodo;
  }

  public void setIdNodo(Integer idNodo) {
    this.idNodo = idNodo;
  }

  public Date getDesde() {
    return this.desde;
  }

  public void setDesde(Date desde) {
    this.desde = desde;
  }

  public Date getHasta() {
    return this.hasta;
  }

  public void setHasta(Date hasta) {
    this.hasta = hasta;
  }

  public String getEstado() {
    return this.estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getNombre() {
    return this.nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEmailCoordinador() {
    return this.emailCoordinador;
  }

  public void setEmailCoordinador(String emailCoordinador) {
    this.emailCoordinador = emailCoordinador;
  }

  public String getBarrio() {
    return this.barrio;
  }

  public void setBarrio(String barrio) {
    this.barrio = barrio;
  }

  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Integer getIdZona() {
    return this.idZona;
  }

  public void setIdZona(Integer idZona) {
    this.idZona = idZona;
  }

}