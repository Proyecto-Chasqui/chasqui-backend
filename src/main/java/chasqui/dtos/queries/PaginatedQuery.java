package chasqui.dtos.queries;
import java.util.List;

public class PaginatedQuery {
  private Integer skip;
  private Integer limit;
  private String orderBy;
  private String orderDirection; // asc or desc

  PaginatedQuery() {
    skip = 0;
    limit = 5;
    orderBy = "id";
  }

  public Integer getSkip() {
    return this.skip;
  }

  public PaginatedQuery setSkip(Integer skip) {
    this.skip = skip;
    return this;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public PaginatedQuery setLimit(Integer limit) {
    this.limit = limit;
    return this;
  }

  public String getOrderBy() {
    return this.orderBy;
  }

  public PaginatedQuery setOrderBy(String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  public String getOrderDirection() {
    return this.orderDirection;
  }

  public PaginatedQuery setOrderDirection(String orderDirection) {
    this.orderDirection = orderDirection;
    return this;
  }

  public PaginatedQuery setOrderAsc(Boolean yes) {
    this.orderDirection = (yes) ?  "asc" : "desc";
    return this;
  }

}