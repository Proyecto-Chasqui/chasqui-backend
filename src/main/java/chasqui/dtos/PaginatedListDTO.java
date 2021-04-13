package chasqui.dtos;

import java.util.ArrayList;
import java.util.List;

public class PaginatedListDTO<T> {
  private List<T> list;
  private Integer total;
  private Integer pageSize;
  private Integer skip;

  public List<T> getList() {
    return this.list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public Integer getTotal() {
    return this.total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public void setTotal(Long total) {
    this.total = Integer.valueOf(total.intValue());
  }

  public Integer getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getSkip() {
    return this.skip;
  }

  public void setSkip(Integer skip) {
    this.skip = skip;
  }

}