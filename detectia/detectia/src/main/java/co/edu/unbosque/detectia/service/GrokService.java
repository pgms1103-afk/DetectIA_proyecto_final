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

import co.edu.unbosque.detectia.dto.GrokDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Servicio de detección de contenido generado por IA mediante la API de Groq
 * (LLM y visión).
 * <p>
 * Proporciona tres modalidades de análisis: texto plano mediante el modelo de
 * lenguaje configurado, imagen como bytes en Base64 y imagen por URL pública
 * mediante el modelo de visión configurado. El resultado es un porcentaje de
 * 0-100 extraído de la respuesta en texto plano del modelo.
 * </p>
 * <p>
 * Para imágenes, acepta JPEG, PNG, GIF y WEBP con un límite de 20 MB. Lanza
 * {@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException} o
 * {@link co.edu.unbosque.detectia.exception.TamanoInvalidoException} en caso de
 * incumplimiento.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.dto.GrokDTO
 */
@Service
public class GrokService {

	@Value("${groq.api.key}")
	private String apiKey;

	@Value("${groq.api.url}")
	private String apiUrl;

	@Value("${groq.model}")
	private String modelText;

	@Value("${groq.model.vision}")
	private String modelVision;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(30)).build();

	/**
	 * Análisis avanzado de Texto
	 */
	public GrokDTO detectarIA(String texto) throws Exception {
		JsonArray messages = new JsonArray();

		JsonObject systemMessage = new JsonObject();
		systemMessage.addProperty("role", "system");
		systemMessage.addProperty("content", "Eres un analizador forense de texto especializado en detectar contenido generado por modelos de lenguaje (LLMs como ChatGPT, Gemini, Claude, Llama, Mistral).\n" +
	            "Evalúa el texto aplicando los siguientes criterios:\n\n" +
	            "[SEÑALES DE IA - aumentan el score]\n" +
	            "- Baja 'burstiness': oraciones de longitud uniforme y ritmo constante y predecible.\n" +
	            "- Transiciones sobreusadas: 'en resumen', 'es importante destacar', 'en conclusión', 'cabe mencionar', 'sin embargo', 'en este sentido', 'es fundamental'.\n" +
	            "- Listas perfectamente paralelas y simétricas sin necesidad narrativa real.\n" +
	            "- Vocabulario inflado o rebuscado donde un humano usaría términos más simples y directos.\n" +
	            "- Estructura rígida de introducción-desarrollo-conclusión incluso en textos cortos.\n" +
	            "- Sobreexplicaciones de conceptos básicos que el lector ya conoce.\n" +
	            "- Hipercorrección gramatical excesiva y ausencia total de informalidad.\n" +
	            "- Afirmaciones genéricas y seguras que evitan toda controversia o postura personal.\n\n" +
	            "[SEÑALES HUMANAS - reducen el score]\n" +
	            "- Errores tipográficos, ortográficos o gramaticales naturales.\n" +
	            "- Expresiones coloquiales, jerga, humor, ironía o sarcasmo.\n" +
	            "- Narrativa no lineal, digresiones o cambios abruptos de tema.\n" +
	            "- Subjetividad emocional clara: opiniones fuertes, frustración, entusiasmo genuino.\n" +
	            "- Uso de primera persona con experiencias personales concretas y específicas.\n" +
	            "- Repetición involuntaria de palabras o ideas (los humanos no somos editores perfectos).\n\n" +
	            "Calibración de escala: 0=Humano con certeza absoluta | 25=Probablemente humano | 50=Ambiguo | 75=Probablemente IA | 100=IA sintética con certeza absoluta.\n\n" +
	            "REGLA DE SALIDA ESTRICTA: Devuelve ÚNICAMENTE el número entero entre 0 y 100. Sin texto, sin '%', sin explicaciones, sin saltos de línea.");
		messages.add(systemMessage);

		JsonObject userMessage = new JsonObject();
		userMessage.addProperty("role", "user");
		userMessage.addProperty("content", "Analiza el siguiente bloque de texto:\n\"\"\"\n" + texto + "\n\"\"\"");
		messages.add(userMessage);

		return ejecutarPeticion(messages, modelText);
	}

	/**
	 * Análisis avanzado de Imágenes (Multimodal)
	 */
	public GrokDTO detectarIAImagen(byte[] archivoBytes, String contentType) throws Exception {

		if (!contentType.equals("image/gif") && !contentType.equals("image/jpeg") && !contentType.equals("image/png")
				&& !contentType.equals("image/webp")) {
			throw new ExtensionInvalidaException(
					"Groq no permite este tipo de imagen " + contentType + " Formatos acepetados:JPEG,PNG,GIF,WEPB");
		}

		if (archivoBytes.length > 20L * 1024 * 1024) {
			double mb = archivoBytes.length / (1024 * 1024);
			throw new TamanoInvalidoException(
					String.format("Groq el archivo(%.1f MB) supera el limite permitibo de MB", mb));
		}

		String base64Imagen = Base64.getEncoder().encodeToString(archivoBytes);
		String dataUrl = "data:" + contentType + ";base64," + base64Imagen;

		JsonArray messages = new JsonArray();

		// Prompt optimizado para visión artificial forense
		JsonObject systemMessage = new JsonObject();
		systemMessage.addProperty("role", "system");
		systemMessage.addProperty("content",
				"Eres un perito forense de visión artificial especializado en detectar imágenes generadas por modelos de difusión (Midjourney, DALL-E, Stable Diffusion, Firefly, Flux, Leonardo AI).\n\n" +

				"[SEÑALES DE IA - aumentan el score]\n" +
				"- Manos y dedos: número incorrecto, fusionados, deformados o con anatomía imposible.\n" +
				"- Texto dentro de la imagen: letras ilegibles, distorsionadas, mezcladas o sin sentido.\n" +
				"- Ojos: demasiado simétricos, reflejos de luz idénticos en ambos, pupila anormal o uncanny valley.\n" +
				"- Piel: textura excesivamente suavizada ('efecto cera'), poros ausentes, uniformidad antinatural.\n" +
				"- Iluminación: fuentes de luz inconsistentes, sombras que no corresponden al ángulo de luz, reflejos imposibles.\n" +
				"- Fondos: patrones que se repiten, objetos que se fusionan entre sí de forma extraña, bordes difusos entre sujeto y fondo.\n" +
				"- Accesorios: aretes, gafas o joyas asimétricas, distorsionadas o con geometría imposible.\n" +
				"- Líneas rectas: se curvan sutilmente en arquitectura, marcos o superficies planas.\n" +
				"- Colores: saturación excesiva, demasiado 'perfectos' o sin variación tonal natural.\n" +
				"- Cabello: hebras que desaparecen, se fusionan o tienen transiciones artificiales.\n\n" +

				"[SEÑALES HUMANAS - reducen el score]\n" +
				"- Grano de película o ruido de sensor digital visible.\n" +
				"- Aberración cromática o distorsión de lente real.\n" +
				"- Desenfoque de movimiento natural y coherente.\n" +
				"- Imperfecciones de piel reales: poros, arrugas, pecas, manchas.\n" +
				"- Sombras y reflejos físicamente coherentes con una sola fuente de luz.\n" +
				"- Artefactos JPEG propios de cámaras reales.\n" +
				"- Profundidad de campo real con bokeh natural.\n\n" +

				"Calibración: 0=Foto/arte humano con certeza | 25=Probablemente humano | 50=Ambiguo | 75=Probablemente IA | 100=IA sintética con certeza.\n\n" +
				"REGLA DE SALIDA ESTRICTA: Analiza mentalmente todos los criterios y responde ÚNICAMENTE con el número entero entre 0 y 100. Sin texto, sin '%', sin explicaciones, sin Markdown.");
		
		messages.add(systemMessage);

		JsonArray userContentArray = new JsonArray();

		JsonObject textContent = new JsonObject();
		textContent.addProperty("type", "text");
		textContent.addProperty("text",
				"Ejecuta el protocolo de análisis forense visual sobre este archivo multimedia.");
		userContentArray.add(textContent);

		JsonObject imageUrlContent = new JsonObject();
		imageUrlContent.addProperty("type", "image_url");
		JsonObject imageUrlObj = new JsonObject();
		imageUrlObj.addProperty("url", dataUrl);
		imageUrlContent.add("image_url", imageUrlObj);
		userContentArray.add(imageUrlContent);

		JsonObject userMessage = new JsonObject();
		userMessage.addProperty("role", "user");
		userMessage.add("content", userContentArray);
		messages.add(userMessage);

		return ejecutarPeticion(messages, modelVision);
	}

	public GrokDTO detectarIAImagenUrl(String urlImagen) throws Exception {
		JsonObject systemMessage = new JsonObject();
		systemMessage.addProperty("role", "system");
		systemMessage.addProperty("content",
				"Eres un perito forense de visión artificial especializado en detectar imágenes generadas por modelos de difusión (Midjourney, DALL-E, Stable Diffusion, Firefly, Flux, Leonardo AI).\n\n" +

				"[SEÑALES DE IA - aumentan el score]\n" +
				"- Manos y dedos: número incorrecto, fusionados, deformados o con anatomía imposible.\n" +
				"- Texto dentro de la imagen: letras ilegibles, distorsionadas, mezcladas o sin sentido.\n" +
				"- Ojos: demasiado simétricos, reflejos de luz idénticos en ambos, pupila anormal o uncanny valley.\n" +
				"- Piel: textura excesivamente suavizada ('efecto cera'), poros ausentes, uniformidad antinatural.\n" +
				"- Iluminación: fuentes de luz inconsistentes, sombras que no corresponden al ángulo de luz, reflejos imposibles.\n" +
				"- Fondos: patrones que se repiten, objetos que se fusionan entre sí de forma extraña, bordes difusos entre sujeto y fondo.\n" +
				"- Accesorios: aretes, gafas o joyas asimétricas, distorsionadas o con geometría imposible.\n" +
				"- Líneas rectas: se curvan sutilmente en arquitectura, marcos o superficies planas.\n" +
				"- Colores: saturación excesiva, demasiado 'perfectos' o sin variación tonal natural.\n" +
				"- Cabello: hebras que desaparecen, se fusionan o tienen transiciones artificiales.\n\n" +

				"[SEÑALES HUMANAS - reducen el score]\n" +
				"- Grano de película o ruido de sensor digital visible.\n" +
				"- Aberración cromática o distorsión de lente real.\n" +
				"- Desenfoque de movimiento natural y coherente.\n" +
				"- Imperfecciones de piel reales: poros, arrugas, pecas, manchas.\n" +
				"- Sombras y reflejos físicamente coherentes con una sola fuente de luz.\n" +
				"- Artefactos JPEG propios de cámaras reales.\n" +
				"- Profundidad de campo real con bokeh natural.\n\n" +

				"Calibración: 0=Foto/arte humano con certeza | 25=Probablemente humano | 50=Ambiguo | 75=Probablemente IA | 100=IA sintética con certeza.\n\n" +
				"REGLA DE SALIDA ESTRICTA: Analiza mentalmente todos los criterios y responde ÚNICAMENTE con el número entero entre 0 y 100. Sin texto, sin '%', sin explicaciones, sin Markdown.");
		JsonArray userContentArray = new JsonArray();

		JsonObject textContent = new JsonObject();
		textContent.addProperty("type", "text");
		textContent.addProperty("text", "Analiza esta imagen para buscar patrones sintéticos.");
		userContentArray.add(textContent);

		JsonObject imageUrlContent = new JsonObject();
		imageUrlContent.addProperty("type", "image_url");
		JsonObject imageUrlObj = new JsonObject();
		imageUrlObj.addProperty("url", urlImagen); 
		imageUrlContent.add("image_url", imageUrlObj);
		userContentArray.add(imageUrlContent);

		JsonObject userMessage = new JsonObject();
		userMessage.addProperty("role", "user");
		userMessage.add("content", userContentArray);

		JsonArray messages = new JsonArray();
		messages.add(systemMessage);
		messages.add(userMessage);

		return ejecutarPeticion(messages, modelVision);
	}

	private GrokDTO ejecutarPeticion(JsonArray messages, String modeloUtilizado) throws Exception {
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("model", modeloUtilizado);
		jsonBody.add("messages", messages);
		jsonBody.addProperty("temperature", 0.2); 
		jsonBody.addProperty("max_completion_tokens", 5); 

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl))
				.header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString())).build();

		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("Groq status: " + respuesta.statusCode());
		System.out.println(respuesta.body());

		if (respuesta.statusCode() != 200) {
			System.err.println("Error Groq API: " + respuesta.body());
			return new GrokDTO(0.0, "ERROR_CONEXION");
		}

		return procesarRespuesta(respuesta.body());
	}

	private GrokDTO procesarRespuesta(String body) {
		try {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(body, JsonObject.class);
			String texto = root.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message")
					.get("content").getAsString().trim();

			texto = texto.replaceAll("[^0-9]", "");

			double porcentaje = Double.parseDouble(texto);
			String veredicto = porcentaje >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";
			return new GrokDTO(porcentaje, veredicto);

		} catch (Exception e) {
			System.err.println("Error al parsear Groq: " + e.getMessage());
			return new GrokDTO(0.0, "ERROR_PARSEO");
		}
	}
}