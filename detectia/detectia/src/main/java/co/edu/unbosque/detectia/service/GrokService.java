package co.edu.unbosque.detectia.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.GrokDTO;

@Service
public class GrokService {
	
	  @Value("${groq.api.key}")
	    private String apiKey;

	    @Value("${groq.api.url}")
	    private String apiUrl;

	    @Value("${groq.model}")
	    private String model;

	    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(20))
	            .build();

	    public GrokDTO detectarIA(String texto) throws Exception {

	        // Mensaje del sistema
	        JsonObject systemMessage = new JsonObject();
	        systemMessage.addProperty("role", "system");
	        systemMessage.addProperty("content",
	            "Eres un experto en detección de contenido generado por IA. " +
	            "Tu única tarea es analizar el texto y devolver un número entre 0 y 100 " +
	            "que represente la probabilidad de que haya sido generado por IA. " +
	            "0 = completamente humano, 100 = completamente generado por IA. " +
	            "Responde ÚNICAMENTE con el número, sin explicaciones ni texto adicional."
	        );

	        // Mensaje del usuario
	        JsonObject userMessage = new JsonObject();
	        userMessage.addProperty("role", "user");
	        userMessage.addProperty("content", "Analiza este texto: " + texto);

	        // Array de mensajes
	        JsonArray messages = new JsonArray();
	        messages.add(systemMessage);
	        messages.add(userMessage);

	        // Cuerpo de la solicitud
	        JsonObject jsonBody = new JsonObject();
	        jsonBody.addProperty("model", model);
	        jsonBody.add("messages", messages);
	        jsonBody.addProperty("temperature", 0.1);
	        jsonBody.addProperty("max_completion_tokens", 10);

	        // Petición HTTP
	        HttpRequest solicitud = HttpRequest.newBuilder()
	                .uri(URI.create(apiUrl))
	                .header("Content-Type", "application/json")
	                .header("Authorization", "Bearer " + apiKey)
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
	                .build();

	        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud,
	                HttpResponse.BodyHandlers.ofString());

	        System.out.println("Groq status: " + respuesta.statusCode());
	        System.out.println("Groq body: " + respuesta.body());

	        if (respuesta.statusCode() != 200) {
	            return new GrokDTO(0.0, "ERROR_CONEXION");
	        }

	        return procesarRespuesta(respuesta.body());
	    }

	    private GrokDTO procesarRespuesta(String body) {
	        try {
	            Gson gson = new Gson();
	            JsonObject root = gson.fromJson(body, JsonObject.class);
	            String texto = root.getAsJsonArray("choices")
	                    .get(0).getAsJsonObject()
	                    .getAsJsonObject("message")
	                    .get("content").getAsString()
	                    .trim();

	            double porcentaje = Double.parseDouble(texto);
	            String veredicto = porcentaje >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";
	            return new GrokDTO(porcentaje, veredicto);

	        } catch (Exception e) {
	            System.err.println("Error al parsear Groq: " + e.getMessage());
	            return new GrokDTO(0.0, "ERROR_PARSEO");
	        }
	    }

}
