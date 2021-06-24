package chasqui.dtos.queries;


public class PedidoQueryDTO extends PaginatedQuery {
  private String keyQuery;
  private Integer idColectivo;
  private String emailCliente;
  private Integer idVendedor;


  public String getKeyQuery() {
    return this.keyQuery;
  }

  public void setKeyQuery(String keyQuery) {
    this.keyQuery = keyQuery;
  }

  public Integer getIdColectivo() {
    return this.idColectivo;
  }

  public void setIdColectivo(Integer idColectivo) {
    this.idColectivo = idColectivo;
  }


  public String getEmailCliente() {
    return this.emailCliente;
  }

  public void setEmailCliente(String emailCliente) {
    this.emailCliente = emailCliente;
  }


  public Integer getIdVendedor() {
    return this.idVendedor;
  }

  public void setIdVendedor(Integer idVendedor) {
    this.idVendedor = idVendedor;
  }



}