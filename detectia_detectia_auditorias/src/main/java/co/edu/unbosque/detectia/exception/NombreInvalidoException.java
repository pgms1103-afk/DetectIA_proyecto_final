package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando el nombre de usuario no cumple con las reglas de validación.
 * <p>
 * Se lanza en los siguientes casos:
 * <ul>
 *   <li>El nombre de usuario es nulo o vacío</li>
 *   <li>Contiene caracteres numéricos o especiales no permitidos</li>
 *   <li>Contiene espacios dobles consecutivos</li>
 *   <li>No tiene al menos dos palabras (nombre y apellido)</li>
 *   <li>El nombre ya está siendo usado por otro usuario (conflicto de unicidad)</li>
 * </ul>
 * </p>
 * <p>
 * La validación utiliza la expresión regular: {@code ^[A-Za-záéíóúñÑÁÉÍÓÚ\s]+$}
 * </p>
 *
 * @author Equipo Revista
 * @version 1.0
 * @since 1.0
 */
public class NombreInvalidoException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo del problema.
     *
     * @param mensaje descripción del problema con el nombre
     *                (ej. "El nombre debe tener Nombre y Apellido",
     *                "El nombre solo debe contener letras y espacios")
     */
    public NombreInvalidoException(String mensaje) {
        super(mensaje);
    }
}