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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.HiveModerationDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;

@Service
public class HiveModerationService {

    @Value("${hive.api.key}")
    private String apiKey;
    
    @Value("${hive.api.url}")
    private String apiUrl;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    // Por archivo local
    public HiveModerationDTO detectarIA(byte[] archivo, String contentType) throws Exception {
    	
    	if (!contentType.equals("image/gif") && !contentType.equals("image/jpeg") && 
    		!contentType.equals("image/png") && !contentType.equals("image/webp") && 
    		!contentType.equals("image/bmp") && !contentType.equals("video/mp4") && 
			!contentType.equals("video/mov") && !contentType.equals("video/avi") && 
			!contentType.equals("video/mkv") && !contentType.equals("video/webm") && 
			!contentType.equals("video/quicktime") && !contentType.equals("video/x-msvideo")) {
			throw new ExtensionInvalidaException("Hive Ai no permite este tipo de formato " + contentType
					+ " Formatos acepetados:JPEG,PNG,GIF,WEPB,BMP,MP4,MOV,AVI,MKV,WEBM");
		}
    	
        String boundary = "---------------------------" + System.currentTimeMillis();
        byte[] body = createMultipartBody(boundary, archivo, "media", contentType);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
        System.out.println("Hive status: " + respuesta.statusCode());
        System.out.println("Hive body: " + respuesta.body());

        if (respuesta.statusCode() != 200) {
            System.err.println("Error Hive API: " + respuesta.body());
            return new HiveModerationDTO(0.0);
        }

        return procesarRespuesta(respuesta.body());
    }

    // Por URL
    public HiveModerationDTO detectarIAUrl(String mediaUrl) throws IOException, InterruptedException {
        String boundary = "---------------------------" + System.currentTimeMillis();
        String body = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"url\"\r\n\r\n"
                + mediaUrl + "\r\n"
                + "--" + boundary + "--\r\n";

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
        System.out.println("Hive URL status: " + respuesta.statusCode());
        System.out.println("Hive URL body: " + respuesta.body());

        if (respuesta.statusCode() != 200) {
            System.err.println("Error Hive API: " + respuesta.body());
            return new HiveModerationDTO(0.0);
        }

        return procesarRespuesta(respuesta.body());
    }

    private HiveModerationDTO procesarRespuesta(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(responseBody, JsonObject.class);

            JsonArray classes = root.getAsJsonArray("output")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("classes");

            // Buscar la clase "ai_generated"
            for (int i = 0; i < classes.size(); i++) {
                JsonObject clase = classes.get(i).getAsJsonObject();
                if (clase.get("class").getAsString().equals("ai_generated")) {
                    double score = clase.get("value").getAsDouble() * 100;
                    return new HiveModerationDTO(score);
                }
            }

            return new HiveModerationDTO(0.0);

        } catch (Exception e) {
            System.err.println("Error al parsear Hive: " + e.getMessage());
            return new HiveModerationDTO(0.0);
        }
    }

    private byte[] createMultipartBody(String boundary, byte[] fileBytes, String fileName, String contentType)
            throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(("--" + boundary + "\r\n").getBytes());
        baos.write(("Content-Disposition: form-data; name=\"media\"; filename=\"" + fileName + "\"\r\n").getBytes());
        baos.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes());
        baos.write(fileBytes);
        baos.write(("\r\n--" + boundary + "--\r\n").getBytes());
        return baos.toByteArray();
    }
}