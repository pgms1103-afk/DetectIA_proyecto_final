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

import co.edu.unbosque.detectia.dto.MistralDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Servicio de detección de contenido generado por IA mediante el agente Pixtral
 * de Mistral AI.
 * <p>
 * Admite texto plano, imágenes en Base64 (JPEG, PNG, WEBP, GIF) y PDFs con un
 * límite de 512 MB. Construye un mensaje multimodal combinando el texto extraído
 * por Tika y los bytes del archivo para que el agente evalúe ambas modalidades.
 * El agente devuelve un JSON con {@code fakePercentage} e {@code isAiGenerated}
 * que se mapea a {@link co.edu.unbosque.detectia.dto.MistralDTO}.
 * </p>
 * <p>
 * Lanza {@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException} si
 * se envía una imagen en formato no soportado, y
 * {@link co.edu.unbosque.detectia.exception.TamanoInvalidoException} si el
 * archivo supera el límite de 512 MB.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.dto.MistralDTO
 */
@Service
public class MistralService {

    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.agent.id}")
    private String agentId;
    
    @Value("${mistral.api.url}")
    private String apiUrl;

    private Gson gson = new Gson();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(45))
            .build();

    /**
     * MÉTODO 1: Enviar a la API
     * Recibe los datos ya procesados por EleccionService para decidir qué enviar a Mistral.
     */
    public MistralDTO detectarIA(String contenido, byte[] archivoBytes, String mimeType) throws Exception {
    	
    	if(archivoBytes.length > 512L*1024*1024) {
    		double mb = archivoBytes.length / (1024*1024);
    		throw new TamanoInvalidoException(
					String.format("Gemini: el archivo(%.1f MB) supera el limite permitibo de MB", mb));
    	}
    	
    	if(mimeType != null && mimeType.startsWith("image/") &&
    	   !mimeType.equals("image/gif") && !mimeType.equals("image/jpeg") &&
    	   !mimeType.equals("image/png") && !mimeType.equals("image/webp")) {
    		
    		throw new ExtensionInvalidaException("Mistral no soporta el tipo de imagen: "+ mimeType 
    				+" Formatos aceptados: JPEG, PNG, WEBP, GIF");
    		
    	}
        
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        
        JsonArray contentArray = new JsonArray();	

        JsonObject textPart = new JsonObject();
        textPart.addProperty("type", "text");
        textPart.addProperty("text", contenido);
        contentArray.add(textPart);

        if (mimeType.startsWith("image/") && archivoBytes != null) {
        	JsonObject imagePart = new JsonObject();
            imagePart.addProperty("type", "image_url");
            
            JsonObject urlObj = new JsonObject();
            urlObj.addProperty("url", "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(archivoBytes));
            
            imagePart.add("image_url", urlObj);
            contentArray.add(imagePart);
        }

        userMessage.add("content", contentArray);
        JsonArray messages = new JsonArray();
        messages.add(userMessage);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("agent_id", agentId);
        jsonBody.add("messages", messages);
        
        JsonObject respFormat = new JsonObject();
        respFormat.addProperty("type", "json_object");
        jsonBody.add("response_format", respFormat);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

        System.out.println("Mistral status: " + respuesta.statusCode());
        System.out.println("Mistral body: " + respuesta.body());
        
        if (respuesta.statusCode() != 200) {
            System.err.println("Error Mistral API: " + respuesta.body());
            return new MistralDTO(0.0, false);
        }

        return procesarRespuesta(respuesta.body());
    }


    private MistralDTO procesarRespuesta(String body) {
        try {
            JsonObject root = gson.fromJson(body, JsonObject.class);

            String contentString = root.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            JsonObject innerJson = gson.fromJson(contentString, JsonObject.class);

            return new MistralDTO(
                innerJson.get("fakePercentage").getAsDouble(),
                innerJson.get("isAiGenerated").getAsBoolean()
            );
        } catch (Exception e) {
            System.err.println("Error al procesar JSON de Mistral: " + e.getMessage());
            return new MistralDTO(0.0, false);
        }
    }
}