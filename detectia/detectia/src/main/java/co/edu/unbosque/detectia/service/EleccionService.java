package co.edu.unbosque.detectia.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

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

		/* SIRVE *///
		try {
			resultadosIA.put("Groq", grok.detectarIA(texto).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Groq omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Groq fallo:" + e.getMessage());
		}

		/* SIRVE *///
		try {
			resultadosIA.put("Gemini",
					gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		/* SIRVE *///
		try {
			resultadosIA.put("Mistral",
					mistral.detectarIA(texto, archivo.getBytes(), archivo.getContentType()).getFakePercentage());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Mistral omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Mistral fallo:" + e.getMessage());
		}

		/* SIRVE *///
		try {
			resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Winston omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Winston fallo:" + e.getMessage());
		}

		return resultadosIA;
	}

	public Map<String, Double> analizarTextoPlano(String texto) {

		Map<String, Double> resultadosIA = new HashMap<>();

		/* SIRVE *///
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

		/* no se si si sirva *///
		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		/* no se si si sirva *///
		try {
			resultadosIA.put("Mistral", mistral.detectarIA(texto, bytes, mimeType).getFakePercentage());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Mistral omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Mistral fallo:" + e.getMessage());
		}

		if (texto != null && !texto.isBlank()) {
			/* no se si si sirva *///
			try {
				resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
				System.err.println("Groq omitido: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Groq fallo:" + e.getMessage());
			}

			/* no se si si sirva *///
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

		/* SIRVE */
		try {
			resultadosIA.put("Sightengine", sightengine.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Sightengine omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Sightengine fallo:" + e.getMessage());
		}

		/* SIRVE */
		try {
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Gemini omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		/* SIRVE */
		try {
			resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Hive Moderation omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Hive Moderation fallo:" + e.getMessage());
		}

		/* SIRVE MEH PASANDO A BIEN */
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

		/* SIRVE *///
		try {
			resultadosIA.put("Winston", winston.detectarIAImagen(url).getScore());
		} catch (Exception e) {
			System.err.println("Winston fallo:" + e.getMessage());
		}

		/* SIRVE *///
		try {
			resultadosIA.put("Sightengine", sightengine.detectarIAUrl(url).getAi_generated());
		} catch (Exception e) {
			System.err.println("Sightengine fallo:" + e.getMessage());
		}

		/* SIRVE *///
		try {
			resultadosIA.put("Gemini", gemini.detectarIAPorUrl(url).getPorcentajeIA());
		} catch (Exception e) {
			System.err.println("Gemini fallo:" + e.getMessage());
		}

		/* SIRVE MEH PASANDO A BIEN *///
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

		/* SIRVE A MEDIAS */
		try {
			resultadosIA.put("TwelveLabs", twelveLabs.detectarIA(bytes, mimeType).getPorcentajeIA());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("TwelveLabs omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("TwelveLabs fallo:" + e.getMessage());
		}

		/* SIRVE */
		try {
			resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated());
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			System.err.println("Hive Moderation omitido: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Hive Moderation fallo:" + e.getMessage());
		}

		/* no se si sirva */
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
		/* SIRVE *///
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
