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

import co.edu.unbosque.detectia.dto.ZeroGPTResponseDTO;
import io.jsonwebtoken.io.IOException;

@Service
public class ZeroGPTService {
	
	    @Value("${zerogpt.api.key}")
	    private String apiKey;

	    @Value("${zerogpt.api.host}")
	    private String apiHost;

	    @Value("${zerogpt.api.url}")
	    private String apiUrl;

	    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(10))
	            .build();

	    public ZeroGPTResponseDTO detectarIA(String texto) throws java.io.IOException {

	        JsonObject jsonObject = new JsonObject();
	        jsonObject.addProperty("input_text", texto);
	        String jsonBody = jsonObject.toString();
	        
	        HttpRequest solicitud = HttpRequest.newBuilder()
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	                .uri(URI.create(apiUrl))
	                .setHeader("Content-Type", "application/json")
	                .setHeader("X-RapidAPI-Key", apiKey)
	                .setHeader("X-RapidAPI-Host", apiHost)
	                .build();

	        HttpResponse<String> respuesta = null;
	        try {
	            respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	        }
	        System.out.println("ZeroGPT status: " + respuesta.statusCode());
	        System.out.println("ZeroGPT body: " + respuesta.body());
	        Gson gson = new Gson();
	        return gson.fromJson(respuesta.body(), ZeroGPTResponseDTO.class);
	    }
	}

