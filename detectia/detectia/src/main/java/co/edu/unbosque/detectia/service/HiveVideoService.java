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

import co.edu.unbosque.detectia.dto.HiveVideoDTO;

@Service
public class HiveVideoService {

	@Value("${hive.video.api.key}")
	private String apiKey;

	@Value("${hive.video.api.url}")
	private String apiUrl;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(30)).build();

	public HiveVideoDTO detectarIA(byte[] videoBytes, String fileName, String contentType) throws Exception {

		String base64Video = Base64.getEncoder().encodeToString(videoBytes);

		JsonObject mediaObject = new JsonObject();
		mediaObject.addProperty("media_base64", base64Video);

		JsonArray inputArray = new JsonArray();
		inputArray.add(mediaObject);

		JsonObject jsonBody = new JsonObject();
		jsonBody.add("input", inputArray);

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(apiUrl))
				.header("Content-Type", "application/json").header("authorization", "token" + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString())).build();

		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("HiveVideo status: " + respuesta.statusCode());
		System.out.println("HiveVideo body: " + respuesta.body());

		if (respuesta.statusCode() != 200) {
			return new HiveVideoDTO(0.0, "ERROR_CONEXION");
		}

		return procesarRespuesta(respuesta.body());
	}

	private HiveVideoDTO procesarRespuesta(String body) {
		try {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(body, JsonObject.class);

			JsonArray output = root.getAsJsonArray("status").get(0).getAsJsonObject().getAsJsonObject("response")
					.getAsJsonArray("output");

			double porcentaje = 0.0;
			for (int i = 0; i < output.size(); i++) {
				JsonObject item = output.get(i).getAsJsonObject();
				String clase = item.get("class").getAsString();
				if (clase.equals("ai_generated") || clase.equals("deepfake")) {
					double score = item.get("score").getAsDouble() * 100;
					if (score > porcentaje) {
						porcentaje = score;
					}
				}
			}

			String veredicto = porcentaje >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";
			return new HiveVideoDTO(porcentaje, veredicto);

		} catch (Exception e) {
			System.err.println("Error al parsear HiveVideo: " + e.getMessage());
			return new HiveVideoDTO(0.0, "ERROR_PARSEO");
		}
	}

}
