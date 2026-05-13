package co.edu.unbosque.detectia.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.unbosque.detectia.dto.HuggingFaceResponseDTO;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String hfToken;

    @Value("${huggingface.api.url}")
    private String baseUrl;

    @Value("${hf.model.name}")
    private String modelName;

    public HuggingFaceResponseDTO analizarArchivo(byte[] archivoBytes) throws Exception {
        // Esto construye: https://api-inference.huggingface.co/models/openai-community/roberta-base-openai-detector
        String urlFinal = baseUrl.trim() + "/" + modelName.trim();
        
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal))
                .header("Authorization", "Bearer " + hfToken)
                // IMPORTANTE: Algunos modelos de HF requieren este header para procesar archivos
                .header("Content-Type", "application/octet-stream")
                .header("x-use-cache", "false") // Forzamos a que no use caché para ver resultados reales
                .POST(HttpRequest.BodyPublishers.ofByteArray(archivoBytes))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Si vuelve a salir HTML, imprimimos la URL en consola para que tú la veas
        if (response.body().contains("<!DOCTYPE html>")) {
            System.err.println("URL FALLIDA: " + urlFinal);
            throw new Exception("Hugging Face devolvió HTML. Revisa que el modelo sea correcto en la consola.");
        }
        
        // ... resto de tu lógica de ObjectMapper ...
        // Si devuelve HTML (empieza con <!DOCTYPE), es que la URL está mal o el modelo no existe
        if (response.body().trim().startsWith("<!DOCTYPE")) {
            throw new Exception("Error de Endpoint: Hugging Face no reconoce la ruta " + urlFinal);
        }

        if (response.statusCode() != 200) {
            throw new Exception("Error " + response.statusCode() + ": " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        // Intentar leer como lista (formato estándar de HF)
        List<HuggingFaceResponseDTO> lista = mapper.readValue(
            response.body(), 
            new TypeReference<List<HuggingFaceResponseDTO>>() {}
        );

        return (lista != null && !lista.isEmpty()) ? lista.get(0) : null;
    }
}