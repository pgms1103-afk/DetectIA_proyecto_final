package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando la contraseña proporcionada no cumple con los
 * requisitos de seguridad definidos en la capa de servicio.
 * <p>
 * Se lanza cuando la contraseña:
 * <ul>
 *   <li>Es nula o vacía</li>
 *   <li>Tiene menos de 8 caracteres</li>
 *   <li>No contiene al menos una letra mayúscula</li>
 *   <li>No contiene al menos un número</li>
 * </ul>
 * </p>
 *
 * @author David Alejandro Velasquez Salamanca
 * @version 1.0
 * @since 1.0
 */
public class PasswordNotValidException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo del problema.
     *
     * @param mensaje descripción del problema con la contraseña
     *                (ej. "La contrasena debe tener al menos una letra mayuscula")
     */
    public PasswordNotValidException(String mensaje) {
        super(mensaje);
    }
}