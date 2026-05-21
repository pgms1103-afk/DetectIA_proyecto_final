package co.edu.unbosque.detectia.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.GeminiDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	@Value("${gemini.api.url}")
	private String apiUrl;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(20)).build();

	/**
	 * Analiza archivos de forma multimodal (Texto, PDF, Imagen, Audio, Video)
	 * 
	 * @param archivoBytes El contenido del archivo en crudo
	 * @param mimeType     El tipo MIME (ej: "application/pdf", "audio/mpeg")
	 * @return GeminiDTO con el porcentaje y veredicto
	 */
	public GeminiDTO detectarIA(byte[] archivoBytes, String mimeType) throws Exception {

		if (!mimeType.equals("image/gif") && !mimeType.equals("image/jpeg") && !mimeType.equals("image/png")
				&& !mimeType.equals("image/webp") && !mimeType.equals("image/heic") && !mimeType.equals("image/heif")
				&& !mimeType.equals("aplication/pdf") && !mimeType.equals("text/plain") && !mimeType.equals("video/mp4")
				&& !mimeType.equals("video/mov") && !mimeType.equals("video/avi") && !mimeType.equals("video/mkv")
				&& !mimeType.equals("video/webm") && !mimeType.equals("video/3gpp")
				&& !mimeType.equals("video/quicktime")) {
			throw new ExtensionInvalidaException("Gemini no permite este tipo de formato " + mimeType
					+ " Formatos acepetados:JPEG,PNG,GIF,WEPB,HEIF,HEIC,PDF,texto,MP4,MOV,AVI,MKV,WEBM");
		}

		if (archivoBytes.length > 2048L * 1024 * 1024) {
			double mb = archivoBytes.length / (1024 * 1024);
			throw new TamanoInvalidoException(
					String.format("Gemini: el video(%.1f MB) supera el limite permitibo de MB", mb));
		}

		if (archivoBytes.length > 100L * 1024 * 1024) {
			double mb = archivoBytes.length / (1024 * 1024);
			throw new TamanoInvalidoException(
					String.format("Gemini: el archivo(%.1f MB) supera el limite permitibo de MB", mb));
		}

		// 1. Convertir archivo a Base64 para el envío inline_data
		String base64Data = Base64.getEncoder().encodeToString(archivoBytes);

		// 2. Construir el cuerpo de la solicitud JSON
		JsonObject jsonBody = new JsonObject();

		// --- SECCIÓN: SYSTEM INSTRUCTION (Define el ROL) ---
		JsonObject systemInstruction = new JsonObject();
		JsonArray siParts = new JsonArray();
		JsonObject siTextPart = new JsonObject();
		siTextPart.addProperty("text",
				"Eres un sistema forense digital especializado en detectar contenido generado por Inteligencia Artificial. "
						+ "Tu única tarea es analizar el archivo proporcionado y devolver un número entre 0 y 100 que represente "
						+ "la probabilidad de que haya sido generado por IA, donde: "
						+ "0 = completamente creado por un humano, 100 = completamente generado por IA. " +

						"Para TEXTO y DOCUMENTOS analiza: "
						+ "coherencia gramatical perfecta, frases repetitivas o genéricas, ausencia de errores naturales, "
						+ "estructura demasiado ordenada, uso excesivo de conectores formales, falta de opiniones personales, "
						+ "lenguaje neutral sin personalidad, párrafos de longitud uniforme. " +

						"Para IMÁGENES analiza: "
						+ "dedos malformados o con número incorrecto, ojos asimétricos o con brillo artificial, "
						+ "fondos con texturas inconsistentes o elementos que se repiten, texto ilegible o distorsionado, "
						+ "transiciones de luz poco naturales, bordes difusos alrededor de personas, "
						+ "orejas, cabello o ropa con detalles irreales. " +

						"Para AUDIO analiza: "
						+ "entonación monótona o demasiado perfecta, ausencia de respiración natural, "
						+ "pausas artificiales, pronunciación perfecta sin variaciones naturales. " +

						"Para VIDEOS analiza: "
						+ "movimientos faciales poco naturales, parpadeo irregular o inexistente, "
						+ "desincronización entre labios y voz, cambios extraños entre frames, "
						+ "manos o dedos deformes en movimiento, iluminación inconsistente entre escenas, "
						+ "bordes inestables alrededor de personas u objetos, "
						+ "expresiones faciales rígidas o demasiado suaves, "
						+ "transiciones artificiales, físicas imposibles o animaciones poco naturales, "
						+ "artefactos visuales temporales, deformaciones repentinas y pérdida de consistencia entre cuadros. "
						+

						"IMPORTANTE: Responde ÚNICAMENTE con un número entre 0 y 100. "
						+ "Sin explicaciones, sin texto adicional, solo el número.");
		siParts.add(siTextPart);
		systemInstruction.add("parts", siParts);
		jsonBody.add("system_instruction", systemInstruction);

		// --- SECCIÓN: CONTENTS (Define los DATOS) ---
		JsonArray contents = new JsonArray();
		JsonObject contentObject = new JsonObject();
		JsonArray parts = new JsonArray();

		// Parte 1: El archivo en base64
		JsonObject inlineData = new JsonObject();
		inlineData.addProperty("mime_type", mimeType);
		inlineData.addProperty("data", base64Data);
		JsonObject filePart = new JsonObject();
		filePart.add("inline_data", inlineData);
		parts.add(filePart);

		// Parte 2: El prompt de ejecución
		JsonObject promptPart = new JsonObject();
		promptPart.addProperty("text", "Analiza este archivo y responde SOLO con un número entre 0 y 100 "
				+ "indicando la probabilidad de que sea generado por IA. " + "Solo el número, " + "nada más.");
		parts.add(promptPart);

		contentObject.add("parts", parts);
		contents.add(contentObject);
		jsonBody.add("contents", contents);

		// --- SECCIÓN: CONFIGURACIÓN (Opcional pero recomendada) ---
		JsonObject generationConfig = new JsonObject();
		generationConfig.addProperty("temperature", 0.2); // Baja temperatura para mayor precisión
		jsonBody.add("generationConfig", generationConfig);

		// 3. Crear la solicitud HTTP
		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl))
				.header("Content-Type", "application/json").header("x-goog-api-key", apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString())).build();

		// 4. Enviar y procesar respuesta
		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("Gemini status: " + respuesta.statusCode());
		System.out.println("Gemini body: " + respuesta.body());

		if (respuesta.statusCode() != 200) {
			System.err.println("Error en API Gemini: " + respuesta.body());
			return new GeminiDTO(0.0, "ERROR_CONEXION");
		}

		return procesarRespuestaGemini(respuesta.body());
	}

	/**
	 * Descarga un archivo desde una URL pública y lo analiza con la API de Gemini
	 * * @param urlPublica La dirección web del archivo (ej:
	 * "https://sitio.com/gato.jpg")
	 * 
	 * @return GeminiDTO con el resultado del peritaje
	 * @throws Exception Si ocurre un fallo en la descarga o en la API de Gemini
	 */
	/**
	 * Descarga un archivo (Imagen o Audio) desde una URL pública y lo analiza con
	 * Gemini * @param urlPublica La dirección web del archivo
	 * 
	 * @return GeminiDTO con el resultado del peritaje
	 * @throws Exception Si ocurre un fallo en la descarga o en la API de Gemini
	 */
	public GeminiDTO detectarIAPorUrl(String urlPublica) throws Exception {

		// A. Descargar los bytes del archivo usando el HTTP_CLIENT del servicio
		HttpRequest solicitudDescarga = HttpRequest.newBuilder().uri(URI.create(urlPublica)).GET().build();

		HttpResponse<byte[]> respuestaDescarga = HTTP_CLIENT.send(solicitudDescarga,
				HttpResponse.BodyHandlers.ofByteArray());

		if (respuestaDescarga.statusCode() != 200) {
			System.err.println("Error al descargar el archivo de la URL: " + respuestaDescarga.statusCode());
			return new GeminiDTO(0.0, "ERROR_DESCARGA_URL");
		}

		byte[] archivoBytes = respuestaDescarga.body();

		// 💡 B. CORREGIDO: Deducir el MIME type de forma nativa y automática (Soporta
		// imágenes, audios y videos)
		String mimeType = java.net.URLConnection.guessContentTypeFromName(urlPublica);

		// Salvavidas por si la URL es extraña o no tiene extensión clara
		if (mimeType == null) {
			if (urlPublica.toLowerCase().contains("mp3"))
				mimeType = "audio/mpeg";
			else if (urlPublica.toLowerCase().contains("wav"))
				mimeType = "audio/wav";
			else
				mimeType = "image/jpeg"; // Fallback por defecto
		}

		// C. Reutilizar tu método local pasándole los datos limpios y el tipo correcto
		return detectarIA(archivoBytes, mimeType);
	}

	private GeminiDTO procesarRespuestaGemini(String responseBody) {
		try {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(responseBody, JsonObject.class);

			// Navegación por el JSON de Gemini
			String textoRespuesta = root.getAsJsonArray("candidates").get(0).getAsJsonObject()
					.getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text")
					.getAsString().trim();

			double porcentaje = Double.parseDouble(textoRespuesta);
			String veredicto = (porcentaje >= 50) ? "PROBABLE IA" : "PROBABLE HUMANO";

			return new GeminiDTO(porcentaje, veredicto);

		} catch (Exception e) {
			System.err.println("Error al parsear respuesta: " + e.getMessage());
			return new GeminiDTO(0.0, "ERROR_PARSEO");
		}
	}
}