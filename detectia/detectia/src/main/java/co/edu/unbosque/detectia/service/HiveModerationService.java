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

/**
 * Servicio de detección de contenido sintético mediante la API de Hive Moderation.
 * <p>
 * Analiza imágenes y videos (JPEG, PNG, GIF, WEBP, BMP, MP4, MOV, AVI, MKV,
 * WEBM) enviados como bytes o por URL pública, extrayendo la probabilidad de
 * generación artificial de la clase {@code "ai_generated"} de la respuesta de
 * la API y expresándola en escala 0-100.
 * </p>
 * <p>
 * Lanza {@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException} si
 * el tipo MIME no está entre los formatos admitidos por el endpoint de
 * imagen/video.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.dto.HiveModerationDTO
 */
@Service
public class HiveModerationService {

    @Value("${hive.api.key}")
    private String apiKey;
    
    @Value("${hive.api.url}")
    private String apiUrl;
    
    @Value("${hive.api.url.audio}")
    private String apiUrlAudio;

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
    
//    public HiveModerationDTO detectarIAAudio(byte[] archivo, String contentType) throws Exception {
//
//        System.out.println("---------- HIVE AUDIO DEBUG ----------");
//        System.out.println("Content-Type recibido: " + contentType);
//        System.out.println("Tamaño del archivo en bytes: " + archivo.length);
//
//        if (!contentType.equals("audio/mpeg") && !contentType.equals("audio/mp3") &&
//            !contentType.equals("audio/wav") && !contentType.equals("audio/ogg") &&
//            !contentType.equals("audio/flac") && !contentType.equals("audio/m4a") &&
//            !contentType.equals("audio/x-m4a")) {
//            throw new ExtensionInvalidaException("Hive Audio no permite este formato: " + contentType
//                    + ". Formatos aceptados: MP3, WAV, OGG, FLAC, M4A");
//        }
//
//        // 🟢 1. Convertimos los bytes del audio a un String en Base64
//        String base64Audio = java.util.Base64.getEncoder().encodeToString(archivo);
//        
//        // 🟢 2. Creamos el formato "Data URI" que exige Hive (ej. "data:audio/mpeg;base64,SUQz...")
//        String dataUri = "data:" + contentType + ";base64," + base64Audio;
//
//        // 🟢 3. Armamos exactamente el mismo JSON que usas para la URL, 
//        // pero pasándole el audio codificado en la propiedad "media"
//        JsonObject input = new JsonObject();
//        input.addProperty("media", dataUri);
//
//        JsonObject body = new JsonObject();
//        body.add("input", new JsonArray());
//        body.getAsJsonArray("input").add(input);
//
//        System.out.println("Enviando JSON (Base64) a URL: " + apiUrlAudio);
//
//        HttpRequest solicitud = HttpRequest.newBuilder()
//                .uri(URI.create(apiUrlAudio))
//                .header("Authorization", "Bearer " + apiKey) 
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json") // Aseguramos que Hive sepa que hablamos JSON
//                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
//                .build();
//
//        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
//        
//        System.out.println("Hive Audio status: " + respuesta.statusCode());
//        System.out.println("Hive Audio body: " + respuesta.body());
//        System.out.println("--------------------------------------");
//
//        if (respuesta.statusCode() != 200) {
//            System.err.println("Error Hive Audio API: " + respuesta.body());
//            return new HiveModerationDTO(0.0);
//        }
//
//        return procesarRespuestaAudio(respuesta.body());
//    }
//    private HiveModerationDTO procesarRespuestaAudio(String responseBody) {
//        try {
//            Gson gson = new Gson();
//            JsonObject root = gson.fromJson(responseBody, JsonObject.class);
//
//            JsonArray output = root.getAsJsonArray("status")
//                    .get(0).getAsJsonObject()
//                    .getAsJsonObject("response")
//                    .getAsJsonArray("output");
//
//            double totalScore = 0.0;
//            int count = 0;
//
//            for (int i = 0; i < output.size(); i++) {
//                JsonArray classes = output.get(i).getAsJsonObject().getAsJsonArray("classes");
//                for (int j = 0; j < classes.size(); j++) {
//                    JsonObject clase = classes.get(j).getAsJsonObject();
//                    if (clase.get("class").getAsString().equals("ai_generated")) {
//                        totalScore += clase.get("score").getAsDouble(); // ← score, no value
//                        count++;
//                    }
//                }
//            }
//
//            double promedio = count > 0 ? (totalScore / count) * 100 : 0.0;
//            return new HiveModerationDTO(promedio);
//
//        } catch (Exception e) {
//            System.err.println("Error al parsear Hive Audio: " + e.getMessage());
//            return new HiveModerationDTO(0.0);
//        }
//    }
//    
//    // Por URL - audio
//    public HiveModerationDTO detectarIAAudioUrl(String mediaUrl) throws IOException, InterruptedException {
//
//        JsonObject input = new JsonObject();
//        input.addProperty("media_url", mediaUrl);
//
//        JsonObject body = new JsonObject();
//        body.add("input", new com.google.gson.JsonArray());
//        body.getAsJsonArray("input").add(input);
//
//        HttpRequest solicitud = HttpRequest.newBuilder()
//                .uri(URI.create(apiUrlAudio))
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
//                .build();
//
//        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Hive Audio URL status: " + respuesta.statusCode());
//        System.out.println("Hive Audio URL body: " + respuesta.body());
//
//        if (respuesta.statusCode() != 200) {
//            System.err.println("Error Hive Audio API: " + respuesta.body());
//            return new HiveModerationDTO(0.0);
//        }
//
//        return procesarRespuestaAudio(respuesta.body());
//    }
//    
    

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