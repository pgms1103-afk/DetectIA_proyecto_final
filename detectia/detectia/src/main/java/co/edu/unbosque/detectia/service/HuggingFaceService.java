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

import co.edu.unbosque.detectia.dto.HuggingFaceDTO;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public HuggingFaceDTO detectarIA(String texto) throws Exception {

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("inputs", texto);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("HuggingFace status: " + respuesta.statusCode());
        System.out.println("HuggingFace body: " + respuesta.body());

        if (respuesta.statusCode() != 200) {
            return new HuggingFaceDTO(0.0, "ERROR_CONEXION");
        }

        return procesarRespuesta(respuesta.body());
    }

    private HuggingFaceDTO procesarRespuesta(String body) {
        try {
            Gson gson = new Gson();
            JsonArray resultado = gson.fromJson(body, JsonArray.class);
            
            JsonArray clasificaciones = resultado.get(0).getAsJsonArray();
            
            double porcentajeIA = 0.0;
            for (int i = 0; i < clasificaciones.size(); i++) {
                JsonObject clasificacion = clasificaciones.get(i).getAsJsonObject();
                String label = clasificacion.get("label").getAsString();
                double score = clasificacion.get("score").getAsDouble();
                
                if (label.equalsIgnoreCase("Fake") || label.equalsIgnoreCase("AI") || label.equalsIgnoreCase("ChatGPT") || label.equalsIgnoreCase("LABEL_1")) {
                    porcentajeIA = score * 100;
                }
                
            }

            String veredicto = porcentajeIA >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";
            return new HuggingFaceDTO(porcentajeIA, veredicto);

        } catch (Exception e) {
            System.err.println("Error al parsear HuggingFace: " + e.getMessage());
            return new HuggingFaceDTO(0.0, "ERROR_PARSEO");
        }
    }
}