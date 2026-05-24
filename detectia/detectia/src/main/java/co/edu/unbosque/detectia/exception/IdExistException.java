package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando se intenta operar con un identificador (ID) que no existe
 * en el sistema, o cuando se detecta un conflicto de unicidad con un ID ya registrado.
 * <p>
 * Esta excepción es de tipo {@link RuntimeException} (unchecked), por lo que no obliga
 * al llamador a capturarla explícitamente, aunque se recomienda manejarla en la capa
 * de controlador para retornar respuestas HTTP apropiadas (ej. 404 Not Found).
 * </p>
 * <p>
 * Casos típicos que la provocan:
 * <ul>
 *   <li>Intento de eliminar o actualizar un usuario/publicación/comentario que no existe</li>
 *   <li>Referencia a una publicación inexistente al crear un comentario</li>
 *   <li>Búsqueda de un recurso con un ID que no está en la base de datos</li>
 * </ul>
 * </p>
 *
 * @author David Alejandro Velasquez Salamanca
 * @version 1.0
 * @since 1.0
 */
public class IdExistException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo del problema.
     *
     * @param mensaje descripción del problema con el identificador
     *                (ej. "El id no existe", "El usuario con nombre X no existe")
     */
    public IdExistException(String mensaje) {
        super(mensaje);
    }
}