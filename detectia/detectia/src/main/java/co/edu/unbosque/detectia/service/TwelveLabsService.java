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

@Service
public class TwelveLabsService {

	@Value("${twelvelabs.api.key}")
	private String apiKey;

	@Value("${twelvelabs.api.url}")
	private String apiUrl;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(30)).build();

	public TwelveLabsDTO detectarIA(byte[] videoBytes, String contentType) throws Exception {

		String assetId = crearAsset(videoBytes, contentType);
		if (assetId == null) {
			return new TwelveLabsDTO(0.0, "ERROR_SUBIDA");
		}

		boolean listo = esperarAsset(assetId);
		if (!listo) {
			return new TwelveLabsDTO(0.0, "ERROR_PROCESAMIENTO");
		}

		return analizarVideo(assetId);
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
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("video_id", assetId);
		jsonBody.addProperty("prompt",
				"Eres un sistema forense digital especializado en detectar contenido generado por IA. "
						+ "Analiza este video y responde ÚNICAMENTE con un número entre 0 y 100 que represente "
						+ "la probabilidad de que haya sido generado por IA o sea un deepfake, donde: "
						+ "0 = completamente real y grabado por un humano, 100 = completamente generado por IA. "
						+ "Para VIDEOS analiza: movimientos antinaturales, iluminación inconsistente, "
						+ "bordes difusos alrededor de personas, sincronización labial incorrecta, "
						+ "parpadeo antinatural, texturas de piel demasiado perfectas. "
						+ "IMPORTANTE: Responde ÚNICAMENTE con el número. Sin explicaciones, solo el número.");
		jsonBody.addProperty("stream", false);

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/analyze"))
				.header("Content-Type", "application/json").header("x-api-key", apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString())).build();

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
