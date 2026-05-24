package co.edu.unbosque.detectia.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.TwelveLabsDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Servicio de detección de deepfakes y contenido sintético en video mediante
 * la API de TwelveLabs (modelo Pegasus 1.5).
 * <p>
 * El flujo de análisis consta de tres etapas: (1) subida del video como asset
 * a la plataforma, (2) polling asíncrono hasta que el asset esté listo, y (3)
 * invocación del endpoint de análisis para obtener un score de 0-100 que indica
 * la probabilidad de que el video sea generado o manipulado por IA.
 * </p>
 * <p>
 * Solo acepta formatos de video e imagen; el tamaño máximo es 200 MB. Lanza
 * {@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException} o
 * {@link co.edu.unbosque.detectia.exception.TamanoInvalidoException} en caso de
 * incumplimiento.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.dto.TwelveLabsDTO
 */
@Service
public class TwelveLabsService {

	@Value("${twelvelabs.api.key}")
	private String apiKey;

	@Value("${twelvelabs.api.url}")
	private String apiUrl;
	
	@Value("${twelvelabs.index.id}")
	private String indexId;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(30)).build();

	public TwelveLabsDTO detectarIA(byte[] videoBytes, String contentType) throws Exception {

		if (!esFormatoSoportado(contentType)) {
			throw new ExtensionInvalidaException("TwelveLabs no permite este tipo de formato " + contentType
					+ " Formatos aceptados: JPEG, PNG, GIF, WEBP, BMP, MP4, MOV, AVI, MKV, WEBM");
		}
		
		if(videoBytes.length > 200L*1024*1024) {
			double mb = videoBytes.length/ (1024*1024);
    		throw new TamanoInvalidoException(String.format("TwelveLabs el archivo(%.1f MB) supera el limite permitibo de MB", mb));
		}

		String assetId = crearAsset(videoBytes, contentType);
		if (assetId == null) {
			throw new RuntimeException("Error al subir el video a la plataforma");
		}

		boolean listo = esperarAsset(assetId);
		if (!listo) {
			throw new RuntimeException("Tiempo de espera agotado o error en el procesamiento del video");
		}
		
		return analizarVideo(assetId);
	}

	private boolean esFormatoSoportado(String contentType) {
		return contentType.equals("image/gif") || contentType.equals("image/jpeg") ||
			   contentType.equals("image/png") || contentType.equals("image/webp") ||
			   contentType.equals("image/bmp") || contentType.equals("video/mp4") ||
			   contentType.equals("video/mov") || contentType.equals("video/avi") ||
			   contentType.equals("video/mkv") || contentType.equals("video/webm") ||
			   contentType.equals("video/quicktime") || contentType.equals("video/x-msvideo");
	}

	private String crearAsset(byte[] videoBytes, String contentType) throws Exception {
		String boundary = "Boundary" + System.currentTimeMillis();
		byte[] cuerpo = construirMultipart(boundary, "archivo", contentType, videoBytes);

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/assets"))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary).header("x-api-key", apiKey)
				.POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo)).build();

		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("TwelveLabs crear asset status: " + respuesta.statusCode());
		System.out.println("TwelveLabs crear asset body: " + respuesta.body());

		if (respuesta.statusCode() != 200 && respuesta.statusCode() != 201) {
			return null;
		}

		Gson gson = new Gson();
		JsonObject json = gson.fromJson(respuesta.body(), JsonObject.class);
		return json.get("_id").getAsString();
	}
	
	

	private boolean esperarAsset(String assetId) throws Exception {
		int intentos = 0;
		int maxIntentos = 36;

		while (intentos < maxIntentos) {
			HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/assets/" + assetId))
					.header("x-api-key", apiKey).GET().build();

			HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

			Gson gson = new Gson();
			JsonObject json = gson.fromJson(respuesta.body(), JsonObject.class);
			String status = json.get("status").getAsString();

			System.out.println("TwelveLabs asset status: " + status);

			if (status.equals("ready")) {
				return true;
			} else if (status.equals("failed")) {
				return false;
			}

			Thread.sleep(5000);
			intentos++;
		}

		return false;
	}

	private TwelveLabsDTO analizarVideo(String assetId) throws Exception {
	    JsonObject video = new JsonObject();
	    video.addProperty("type", "asset_id");
	    video.addProperty("asset_id", assetId);

	    JsonObject jsonBody = new JsonObject();
	    jsonBody.add("video", video);
	    jsonBody.addProperty("model_name", "pegasus1.5");
	    jsonBody.addProperty("stream", false);
	    jsonBody.addProperty("prompt",
	            "Eres un sistema forense digital especializado en detectar contenido generado por IA. "
	            + "Analiza este video y responde ÚNICAMENTE con un número entre 0 y 100 que represente "
	            + "la probabilidad de que haya sido generado por IA o sea un deepfake, donde: "
	            + "0 = completamente real y grabado por un humano, 100 = completamente generado por IA. "
	            + "Para VIDEOS analiza: movimientos antinaturales, iluminación inconsistente, "
	            + "bordes difusos alrededor de personas, sincronización labial incorrecta, "
	            + "parpadeo antinatural, texturas de piel demasiado perfectas. "
	            + "IMPORTANTE: Responde ÚNICAMENTE con el número. Sin explicaciones, solo el número.");

	    HttpRequest solicitud = HttpRequest.newBuilder()
	            .uri(URI.create(apiUrl + "/analyze"))
	            .header("Content-Type", "application/json")
	            .header("x-api-key", apiKey)
	            .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
	            .build();

	    HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

	    System.out.println("TwelveLabs analyze status: " + respuesta.statusCode());
	    System.out.println("TwelveLabs analyze body: " + respuesta.body());

	    if (respuesta.statusCode() != 200) {
	        return new TwelveLabsDTO(0.0, "ERROR_ANALISIS");
	    }

	    return procesarRespuesta(respuesta.body());
	}

	private TwelveLabsDTO procesarRespuesta(String body) {
		try {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(body, JsonObject.class);
			String texto = root.get("data").getAsString().trim();

			double porcentaje = Double.parseDouble(texto);
			String veredicto = porcentaje >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";

			return new TwelveLabsDTO(porcentaje, veredicto);
		} catch (Exception e) {
			System.err.println("Error al parsear TwelveLabs: " + e.getMessage());
			return new TwelveLabsDTO(0.0, "ERROR_PARSEO");
		}
	}

	private byte[] construirMultipart(String boundary, String fileName, String contentType, byte[] fileBytes) {
		String delimiter = "--" + boundary + "\r\n";
		String closing = "--" + boundary + "--\r\n";

		StringBuilder sb = new StringBuilder();
		sb.append(delimiter);
		sb.append("Content-Disposition: form-data; name=\"method\"\r\n\r\n");
		sb.append("direct\r\n");
		sb.append(delimiter);
		sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
		sb.append("Content-Type: ").append(contentType).append("\r\n\r\n");

		byte[] header = sb.toString().getBytes();
		byte[] footer = ("\r\n" + closing).getBytes();

		byte[] resultado = new byte[header.length + fileBytes.length + footer.length];
		System.arraycopy(header, 0, resultado, 0, header.length);
		System.arraycopy(fileBytes, 0, resultado, header.length, fileBytes.length);
		System.arraycopy(footer, 0, resultado, header.length + fileBytes.length, footer.length);

		return resultado;
	}

}
