package chasqui.model_lite;

import java.util.Date;
import java.util.Map;

public class PedidoColectivoStatsByEstado {
  private Map<String, PedidoColectivoStats> stats;
  private Date snapshotDate;

  public Map<String, PedidoColectivoStats> getStats() {
    return this.stats;
  }

  public void setStats(Map<String, PedidoColectivoStats> stats) {
    this.stats = stats;
  }

  public Date getSnapshotDate() {
    return this.snapshotDate;
  }

  public void setSnapshotDate(Date snapshotDate) {
    this.snapshotDate = snapshotDate;
  }

}
