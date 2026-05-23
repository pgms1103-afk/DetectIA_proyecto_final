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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.HuggingFaceDTO;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.key}")
    private String apiKey;
    
    @Value("${huggingface.api.url}")
    private String apiUrl;

    

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    public HuggingFaceDTO detectarIAAudio(byte[] audioBytes) throws Exception {
        
        // Verificar tamaño máximo 5MB
        if (audioBytes.length > 5 * 1024 * 1024) {
            System.err.println("Archivo muy grande (" + audioBytes.length + " bytes). Máximo permitido: 5MB.");
            return new HuggingFaceDTO(0.0);
        }

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(audioBytes))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("HuggingFace Audio status: " + respuesta.statusCode());
        System.out.println("HuggingFace Audio body: " + respuesta.body());

        if (respuesta.statusCode() != 200) {
            return new HuggingFaceDTO(0.0);
        }

        return procesarRespuestaAudio(respuesta.body());
    }

    private HuggingFaceDTO procesarRespuestaAudio(String body) {
        try {
            Gson gson = new Gson();
            JsonArray resultado = gson.fromJson(body, JsonArray.class);

            double porcentajeIA = 0.0;
            for (int i = 0; i < resultado.size(); i++) {
                JsonObject item = resultado.get(i).getAsJsonObject();
                String label = item.get("label").getAsString();
                double score = item.get("score").getAsDouble();

                if (label.equalsIgnoreCase("AI") || 
                    label.equalsIgnoreCase("fake") || 
                    label.equalsIgnoreCase("synthetic")) {
                    porcentajeIA = score * 100;
                }
            }
            return new HuggingFaceDTO(porcentajeIA);

        } catch (Exception e) {
            System.err.println("Error al parsear audio HuggingFace: " + e.getMessage());
            return new HuggingFaceDTO(0.0);
        }
    }
}