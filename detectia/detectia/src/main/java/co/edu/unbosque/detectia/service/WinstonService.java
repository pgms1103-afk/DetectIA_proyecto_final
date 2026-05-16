package co.edu.unbosque.detectia.service;

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
import co.edu.unbosque.detectia.dto.WinstonDTO;

@Service
public class WinstonService {

	@Value("${winston.api.key}")
	private String apiKey;

	@Value("${winston.api.urltexto}")
	private String apiUrlTexto;
	
	@Value("${winston.api.urlimagen}")
	private String apiUrlImagen;
	
	 private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(20))
	            .build();
	
	public WinstonDTO detectarIA (String texto) throws IOException, InterruptedException {
		
		JsonObject body = new JsonObject();
		body.addProperty("text", texto);
		
		HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrlTexto))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
		
		 HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

	        System.out.println("Winston status: " + respuesta.statusCode());
	        System.out.println("Winston body: " + respuesta.body());
	        
	        if (respuesta.statusCode() != 200) {
	            System.err.println("Error Mistral API: " + respuesta.body());
	            return new WinstonDTO(0.0);
	        }
	        
	        Gson gson = new Gson();
	        JsonObject root = gson.fromJson(respuesta.body(), JsonObject.class);
	        double humanScore = root.get("score").getAsDouble();
	        // score es Human Score (0-100), lo invertimos para obtener porcentaje de IA
	        return new WinstonDTO(100 - humanScore);
		
	}
	
	public WinstonDTO detectarIAImagen (String url) throws IOException, InterruptedException {
		
		JsonObject body = new JsonObject();
		body.addProperty("url", url);
		
		HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrlImagen))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
		
		 HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

	        System.out.println("Winston status: " + respuesta.statusCode());
	        System.out.println("Winston body: " + respuesta.body());
	        
	        if (respuesta.statusCode() != 200) {
	            System.err.println("Error Mistral API: " + respuesta.body());
	            return new WinstonDTO(0.0);
	        }
	        
	        Gson gson = new Gson();
	        JsonObject root = gson.fromJson(respuesta.body(), JsonObject.class);
	        double humanScore = root.get("score").getAsDouble()*100;
	        // score es Human Score (0-100), lo invertimos para obtener porcentaje de IA
	        return new WinstonDTO(100 - humanScore);
		
		
	}
	
	

}
