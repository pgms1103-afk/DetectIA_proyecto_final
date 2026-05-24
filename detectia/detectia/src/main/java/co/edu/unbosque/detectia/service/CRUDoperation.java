package co.edu.unbosque.detectia.service;

import java.util.List;

/**
 * Contrato genérico de operaciones CRUD para los servicios de la aplicación.
 * <p>
 * Define las cuatro operaciones básicas de persistencia (crear, listar,
 * eliminar y actualizar) parametrizadas por el tipo de DTO que maneja cada
 * implementación concreta.
 * </p>
 *
 * @param <T> el tipo de objeto de transferencia de datos (DTO) que gestiona
 *            la implementación
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 */
public interface CRUDoperation<T> {

    /**
     * Crea y persiste un nuevo registro a partir del DTO proporcionado.
     *
     * @param data DTO con los datos del nuevo recurso
     * @return {@code 0} si la operación fue exitosa; {@code 1} si hubo algún
     *         conflicto o el recurso ya existe
     */
    public int create(T data);

    /**
     * Recupera todos los registros existentes y los devuelve como lista de DTOs.
     *
     * @return lista con todos los registros; lista vacía si no hay ninguno
     */
    public List<T> getAll();

    /**
     * Elimina el registro identificado por el {@code id} dado.
     *
     * @param id identificador único del recurso a eliminar
     * @return {@code 0} si la eliminación fue exitosa; {@code 1} si el recurso
     *         no fue encontrado
     */
    public int delateById(Long id);

    /**
     * Actualiza los datos del registro identificado por {@code id} con la
     * información contenida en {@code data}.
     *
     * @param id   identificador único del recurso a actualizar
     * @param data DTO con los nuevos valores para el recurso
     * @return {@code 0} si la actualización fue exitosa; {@code 1} si el recurso
     *         no fue encontrado
     */
    public int updateById(Long id, T data);

}
