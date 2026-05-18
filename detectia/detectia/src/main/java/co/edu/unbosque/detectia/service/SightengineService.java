package co.edu.unbosque.detectia.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.SightengineDTO;

@Service
public class SightengineService {

	@Value("${sightengine.api.user}")
	private String apiUser;

	@Value("${sightengine.api.key}")
	private String apiKey;

	@Value("${sightengine.models}")
	private String model;

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(20)).build();

	public SightengineDTO detectarIA(byte[] archivo, String contentType) throws Exception {

		String url = String.format("https://api.sightengine.com/1.0/check.json?models=%s&api_user=%s&api_secret=%s",
				model, apiUser, apiKey);

		String boundary = "---------------------------" + System.currentTimeMillis();

		byte[] body = createMultipartBody(boundary, archivo, "archivo", contentType);

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(url))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();

		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("Sightengine status: " + respuesta.statusCode());
		System.out.println("Sightengine body: " + respuesta.body());

		if (respuesta.statusCode() != 200) {
			System.err.println("Error en API sightengine: " + respuesta.body());
		}
		return procesarRespuesta(respuesta.body());

	}

	public SightengineDTO detectarIAUrl(String imagenUrl) throws IOException, InterruptedException {

		String url = String.format(
				"https://api.sightengine.com/1.0/check.json?url=%s&models=%s&api_user=%s&api_secret=%s", imagenUrl,
				model, apiUser, apiKey);

		HttpRequest solicitud = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

		System.out.println("Sightengine URL status: " + respuesta.statusCode());
		System.out.println("Sightengine URL body: " + respuesta.body());

		if (respuesta.statusCode() != 200) {
			System.err.println("Error en API Sightengine: " + respuesta.body());
			return new SightengineDTO(0.0, "error");
		}

		return procesarRespuesta(respuesta.body());
	}
	
	

	
	

	private SightengineDTO procesarRespuesta(String responseBody) {
		try {
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(responseBody, JsonObject.class);

			String estado = root.get("status").getAsString();
			double porcentajeIA = root.getAsJsonObject("type").get("ai_generated").getAsDouble() * 100;

			return new SightengineDTO(porcentajeIA, estado);
		} catch (Exception e) {
			System.err.println("Error al parsear respuesta " + e.getMessage());
			return new SightengineDTO(0.0, "fracaso");
		}
	}

	private byte[] createMultipartBody(String boundary, byte[] fileBytes, String fileName, String contentType)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(("--" + boundary + "\r\n").getBytes());

		// Configuramos el nombre y tipo real
		baos.write(("Content-Disposition: form-data; name=\"media\"; filename=\"" + fileName + "\"\r\n").getBytes());

		// Aquí es donde usamos el parámetro dinámico
		baos.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes());

		baos.write(fileBytes);
		baos.write(("\r\n--" + boundary + "--\r\n").getBytes());
		return baos.toByteArray();
	}
	

	

}
