package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando el correo electrónico proporcionado no cumple con las reglas de validación
 * definidas en la capa de negocio o servicio.
 * <p>
 * Esta excepción es de tipo {@link RuntimeException} (unchecked), por lo que no obliga
 * al llamador a capturarla explícitamente, aunque se recomienda manejarla en la capa
 * de controlador para devolver respuestas HTTP apropiadas (ej. 400 Bad Request).
 * </p>
 * <p>
 * Casos típicos que la provocan (según la lógica en {@code UsuarioService}):
 * <ul>
 *   <li>Correo nulo o vacío</li>
 *   <li>No cumple con el formato básico de correo electrónico (ej. no contiene @ o dominio válido)</li>
 *   <li>El correo ya está registrado en el sistema (duplicado)</li>
 * </ul>
 * </p>
 * <p>
 * La validación utiliza una expresión regular estándar para correos electrónicos:
 * {@code ^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[a-zA-Z]{2,}$}
 * </p>
 *
 * @author David Alejandro Velasquez Salamanca
 * @since 1.0
 * @see ApellidoInvalidoException
 * @see CedulaInvalidaException
 * @see NombreInvalidoException
 * @see ContrasenaInvalidaException
 * @see TipoUsuarioInvalidoException
 * @see IdExistException
 * @see UsuarioService
 */
public class CorreoInvalidoException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje de error detallado.
     *
     * @param mensaje descripción específica del problema con el correo  
     *                (ej. "El correo no puede estar vacio",  
     *                "Correo invalido. Debe tener formato ejemplo@correo.com",  
     *                "El correo ya esta registrado")
     */
    public CorreoInvalidoException(String mensaje) {
        super(mensaje);
    }
}