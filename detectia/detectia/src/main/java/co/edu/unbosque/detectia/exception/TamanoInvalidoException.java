package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando el tamaño del archivo supera el límite máximo
 * permitido por el servicio de IA receptor.
 * <p>
 * Cada servicio externo tiene su propio límite de tamaño (p. ej. Groq: 20 MB,
 * Sightengine: 50 MB, TwelveLabs: 200 MB, Mistral: 512 MB). Cuando el archivo
 * recibido supera dicho límite, se lanza esta excepción unchecked con un mensaje
 * que indica el tamaño actual y el máximo permitido.
 * </p>
 *
 * @author David Alejandro Velasquez Salamanca
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.EleccionService
 */
public class TamanoInvalidoException extends RuntimeException{

	/**
	 * Construye la excepción con un mensaje que indica el tamaño actual del
	 * archivo y el límite excedido.
	 *
	 * @param mensaje descripción del problema
	 *                (p. ej. {@code "Sightengine: el archivo (55.3 MB) supera el
	 *                límite permitido de 50 MB"})
	 */
	public TamanoInvalidoException(String mensaje) {
		super(mensaje);
	}

}
