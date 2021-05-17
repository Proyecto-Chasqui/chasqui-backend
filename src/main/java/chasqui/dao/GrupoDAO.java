package chasqui.dao;

import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import chasqui.model.GrupoCC;
import chasqui.model_lite.MiembroGCCLite;

public interface GrupoDAO {
	public void altaGrupo(GrupoCC grupo);

	public List<GrupoCC> obtenerGruposDeVendedor(final Integer idVendedor);

	public void eliminarGrupoCC(GrupoCC grupo);

	public void guardarGrupo(GrupoCC grupo);

	GrupoCC obtenerGrupoPorId(Integer idGrupoCC);

	public List<GrupoCC> obtenerGruposDelClienteParaVendedor(String email, Integer idVendedor);

	List<GrupoCC> obtenerGruposEnUnArea(Geometry area);

	List<GrupoCC> obtenerGruposDeVendedorCon(Integer idVendedor, Date d, Date h, String estadoSeleccionado);

	/**
	 * Este metodo obtiene todos los grupos, independientemente de si es un nodo o solo GCC.
	 * Usar solo en casos en los que se use la funcionalidad de GCC desde Nodos.
	 * @param idGrupo
	 * @return
	 */
	public GrupoCC obtenerGrupoAbsolutoPorId(Integer idGrupo);

	List<MiembroGCCLite> obtenerMiembrosDeGrupo(Integer idGrupo);
}
