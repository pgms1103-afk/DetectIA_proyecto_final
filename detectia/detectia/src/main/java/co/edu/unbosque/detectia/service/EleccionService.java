package co.edu.unbosque.detectia.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Servicio orquestador de análisis de contenido generado por IA.
 * <p>
 * Determina el tipo MIME del archivo o URL recibido y delega el análisis al
 * conjunto de servicios externos más adecuado para cada modalidad:
 * </p>
 * <ul>
 *   <li><strong>PDF</strong>: Gemini, Mistral, Grok, Winston</li>
 *   <li><strong>Texto / Word</strong>: Grok, Gemini, Mistral, Winston</li>
 *   <li><strong>Imagen</strong>: Sightengine, Gemini, Hive Moderation, Grok Visión</li>
 *   <li><strong>Video</strong>: TwelveLabs, Hive Moderation, Gemini</li>
 *   <li><strong>Audio</strong>: ACRCloud, Gemini</li>
 *   <li><strong>URL de imagen</strong>: Winston, Sightengine, Gemini, Grok Visión</li>
 * </ul>
 * <p>
 * Cada servicio es invocado de forma independiente; un fallo en uno de ellos
 * no impide que los demás continúen. Los errores de validación
 * ({@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException},
 * {@link co.edu.unbosque.detectia.exception.TamanoInvalidoException}) son
 * registrados en {@code System.err} y el servicio es omitido del resultado.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see TextoExtractorService
 * @see GrokService
 * @see GeminiService
 * @see MistralService
 * @see SightengineService
 * @see TwelveLabsService
 * @see WinstonService
 * @see ACRCloudService
 * @see HiveModerationService
 */
@Service
public class EleccionService {

	@Autowired
	private TextoExtractorService extractor;

	@Autowired
	private GrokService grok;

	@Autowired
	private GeminiService gemini;

	@Autowired
	private MistralService mistral;

	@Autowired
	private SightengineService sightengine;

	@Autowired
	private TwelveLabsService twelveLabs;

	@Autowired
	private WinstonService winston;

	@Autowired
	private ACRCloudService acrCloude;

	@Autowired
	private HiveModerationService hiveModeration;

	/**
	 * Analiza un archivo multipart detectando su tipo MIME y enrutando el análisis
	 * al conjunto de servicios de IA correspondiente.
	 *
	 * @param archivo archivo multipart a analizar (PDF, texto, imagen, video o audio)
	 * @return mapa con los resultados de cada servicio de IA exitoso; las claves son
	 *         los nombres de los servicios y los valores los porcentajes de detección
	 *         (0-100). Retorna un mapa vacío si el tipo no es reconocido.
	 * @throws Exception si ocurre un error irrecuperable al leer el archivo o
	 *                   detectar su tipo
	 */
	public Map<String, Double> analizar(MultipartFile archivo) throws Exception {
		String tipo = extractor.detectarTipo(archivo);

		if (tipo.contains("pdf")) {
			return analizarPDF(archivo);
		} else if (esDocumento(tipo)) {
			return analizarTexto(archivo);
		} else if (tipo.startsWith("image")) {
			return analizarImagen(archivo);
		} else if (tipo.startsWith("video")) {
			return analizarVideo(archivo);
		} else if (tipo.startsWith("audio") || tipo.contains("mpeg") || tipo.contains("wav") || tipo.contains("ogg")) {
			return analizarMusica(archivo);
		}
		return new HashMap<>();
	}

	/**
	 * Analiza una URL pública de imagen (JPG, JPEG, PNG, WEBP) enrutando el
	 * análisis a los servicios de IA compatibles con URL de imagen.
	 *
	 * @param url URL pública del archivo de imagen a analizar
	 * @return mapa con los resultados de cada servicio de IA exitoso; retorna un
	 *         mapa vacío si la URL no tiene extensión de imagen reconocida
	 * @throws Exception si ocurre un error irrecuperable durante el análisis
	 */
	public Map<String, Double> analizar(String url) throws Exception {
		String urlMinuscula = url.toLowerCase();
		if (urlMinuscula.endsWith(".jpg") || urlMinuscula.endsWith(".jpeg") || urlMinuscula.endsWith(".png")
				|| urlMinuscula.endsWith(".webp")) {

			return analizarImagenURL(url);
		}

		return new HashMap<>();
	}

	private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);
		Map<String, Double> resultadosIA = new HashMap<>();


		try {
			resultadosIA.put("Groq", grok.detectarIA(texto).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Groq omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Groq fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Gemini",
					gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Mistral",
					mistral.detectarIA(texto, archivo.getBytes(), archivo.getContentType()).getFakePercentage());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Mistral omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Mistral fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Winston omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Winston fallo:" + e.getMessage());
		}

		return resultadosIA;
	}

	/**
	 * Analiza un fragmento de texto plano proporcionado directamente (sin archivo),
	 * invocando los servicios de IA compatibles con texto.
	 *
	 * @param texto cadena de texto a analizar
	 * @return mapa con los resultados de cada servicio de IA exitoso (Gemini,
	 *         Winston); retorna un mapa vacío si ambos fallan
	 */
	public Map<String, Double> analizarTextoPlano(String texto) {

		Map<String, Double> resultadosIA = new HashMap<>();

		try {
			resultadosIA.put("Gemini",gemini.detectarIATexto(texto).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}


		/* SIRVE */
		try {
			resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Winston omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Winston fallo: " + e.getMessage());
		}

		return resultadosIA;
	}

	private Map<String, Double> analizarPDF(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();

		Map<String, Double> resultadosIA = new HashMap<>();

		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Mistral", mistral.detectarIA(texto, bytes, mimeType).getFakePercentage());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Mistral omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Mistral fallo:" + e.getMessage());
		}

		if (texto != null && !texto.isBlank()) {
			try {
				resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
				System.err.println("Groq omitido: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Groq fallo:" + e.getMessage());
			}

			try {
				resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
			} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
				System.err.println("Winston omitido: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Winston fallo:" + e.getMessage());
			}

		}

		return resultadosIA;
	}

	private Map<String, Double> analizarImagen(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();


		try {
			resultadosIA.put("Sightengine", sightengine.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Sightengine omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Sightengine fallo:" + e.getMessage());
		}


		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}


		try {
			resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Hive Moderation omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Hive Moderation fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Groq", grok.detectarIAImagen(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Groq omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Groq fallo:" + e.getMessage());
		}

		return resultadosIA;
	}

	private Map<String, Double> analizarImagenURL(String url) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();


		try {
			resultadosIA.put("Winston", winston.detectarIAImagen(url).getScore());
		} catch (Exception e) {
			System.err.println("Winston fallo:" + e.getMessage());
		}


		try {
			resultadosIA.put("Sightengine", sightengine.detectarIAUrl(url).getAi_generated());
		} catch (Exception e) {
			System.err.println("Sightengine fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Gemini", gemini.detectarIAPorUrl(url).getPorcentajeIA());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Groq", grok.detectarIAImagenUrl(url).getPorcentajeIA());
		} catch (Exception e) {
			System.err.println("Groq fallo:" + e.getMessage());
		}

		return resultadosIA;
	}

	private Map<String, Double> analizarVideo(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();


		try {
			resultadosIA.put("TwelveLabs", twelveLabs.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("TwelveLabs omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("TwelveLabs fallo:" + e.getMessage());
		}
		try {
			resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Hive Moderation omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Hive Moderation fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		return resultadosIA;
	}

	private Map<String, Double> analizarMusica(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();
	
		try {
			resultadosIA.put("ACRCloud", acrCloude.detectarIAArchivo(archivo).getAi_probability());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("ACRCloud omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("ACRCloud fallo:" + e.getMessage());
		}

		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (Exception e) {
			System.err.println("Gemini fallo en música: " + e.getMessage());
		}

		return resultadosIA;
	}

	private boolean esDocumento(String tipo) {
		return tipo.startsWith("text") || tipo.contains("word");
	}

}
