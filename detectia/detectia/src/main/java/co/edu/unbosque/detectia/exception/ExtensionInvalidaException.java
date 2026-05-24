package co.edu.unbosque.detectia.exception;

/**
 * Excepción lanzada cuando el tipo MIME o la extensión del archivo enviado no
 * está entre los formatos admitidos por el servicio de IA receptor.
 * <p>
 * Cada servicio externo (Sightengine, Groq, Gemini, Mistral, Hive, TwelveLabs,
 * ACRCloud) define su propia lista de formatos aceptados. Cuando el tipo MIME
 * detectado por Apache Tika no coincide con dicha lista, se lanza esta excepción
 * unchecked para interrumpir el flujo de análisis con un mensaje descriptivo.
 * </p>
 *
 * @author David Alejandro Velasquez Salamanca
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.EleccionService
 */
public class ExtensionInvalidaException extends RuntimeException{

	/**
	 * Construye la excepción con un mensaje que indica el formato rechazado y
	 * los formatos aceptados por el servicio.
	 *
	 * @param mensaje descripción del problema
	 *                (p. ej. {@code "Groq no permite este tipo de imagen image/tiff.
	 *                Formatos aceptados: JPEG, PNG, GIF, WEBP"})
	 */
	public ExtensionInvalidaException(String mensaje) {
		super(mensaje);
	}

}
