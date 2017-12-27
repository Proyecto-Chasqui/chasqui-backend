package chasqui.dao;

import java.util.List;

import chasqui.model.MiembroDeGCC;

public interface MiembroDeGCCDAO {

	public void actualizarMiembroDeGCC(MiembroDeGCC miembro);

	/**
	 * Un mismo cliente pertenece a varios GCC en uno o varios Vendedores, pero deben actualizarse todos.
	 * @ret Los objetos MiembroDeGCC del cliente.
	 */
	public List<MiembroDeGCC> obtenerMiembrosDeGCCParaCliente(Integer idCliente);

	List<MiembroDeGCC> obtenerMiembrosDeGCCParaClientePorMail(String emailCliente);
}
