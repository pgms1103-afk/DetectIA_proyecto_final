package co.edu.unbosque.detectia.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import co.edu.unbosque.detectia.dto.ACRCloudeDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;


/**
 * Servicio de detección de audio generado por IA mediante la API de ACRCloud.
 * <p>
 * Envía archivos de audio (MP3, WAV, AAC, OGG, FLAC, M4A, AMR) al endpoint de
 * análisis de ACRCloud y realiza un ciclo de polling hasta que el resultado esté
 * disponible, retornando la probabilidad de generación artificial como
 * {@link co.edu.unbosque.detectia.dto.ACRCloudeDTO}.
 * </p>
 * <p>
 * Solo acepta formatos de audio; cualquier otro tipo MIME lanza
 * {@link co.edu.unbosque.detectia.exception.ExtensionInvalidaException}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.dto.ACRCloudeDTO
 */
@Service
public class ACRCloudService {

    @Value("${acrcloud.api.token}")
    private String token;

    @Value("${acrcloud.container.id}")
    private String containerId;

    
    @Value("${acrcloud.api.url}")
    private String url;


    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public ACRCloudeDTO detectarIAArchivo(MultipartFile archivo) throws IOException, InterruptedException {
    	
    	String mimeType = archivo.getContentType();
    	
    	if (!"audio/mpeg".equals(mimeType) && 
    		!"audio/mp3".equals(mimeType) &&
    		!"audio/wav".equals(mimeType) &&
    		!"audio/ogg".equals(mimeType) &&
    		!"audio/flac".equals(mimeType) &&
    		!"audio/aac".equals(mimeType) &&
    		!"audio/m4a".equals(mimeType) &&
    		!"audio/amr".equals(mimeType) &&
    		!"audio/x-wav".equals(mimeType) &&
    		!"audio/x-flac".equals(mimeType)) {
			throw new ExtensionInvalidaException("ACRCloud no permite este tipo de formato " + mimeType
					+ " Formatos acepetados:MPEG,MP3,WAV,AAC,M4A,AMR");
		}

        String boundary = "----Boundary" + System.currentTimeMillis();

        byte[] archivoBytes = archivo.getBytes();
        String fileName = archivo.getOriginalFilename();

        String parteInicio = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"data_type\"\r\n\r\naudio\r\n"
                + "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n";

        String parteFin = "\r\n--" + boundary + "--\r\n";

        byte[] inicio = parteInicio.getBytes();
        byte[] fin = parteFin.getBytes();
        byte[] cuerpo = new byte[inicio.length + archivoBytes.length + fin.length];
        System.arraycopy(inicio, 0, cuerpo, 0, inicio.length);
        System.arraycopy(archivoBytes, 0, cuerpo, inicio.length, archivoBytes.length);
        System.arraycopy(fin, 0, cuerpo, inicio.length + archivoBytes.length, fin.length);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url + containerId + "/files"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
        System.out.println("ACRCloud POST status: " + respuesta.statusCode());
        System.out.println("ACRCloud POST body: " + respuesta.body());

        if (respuesta.statusCode() != 200) {
            System.err.println("Error ACRCloud POST: " + respuesta.body());
            return new ACRCloudeDTO(0.0);
        }

        Gson gson = new Gson();
        JsonObject root = gson.fromJson(respuesta.body(), JsonObject.class);
        String fileId = root.getAsJsonObject("data").get("id").getAsString();

 
        return polling(fileId);
    }

    private ACRCloudeDTO polling(String fileId) throws IOException, InterruptedException {

        Gson gson = new Gson();
        int intentos = 0;
        int maxIntentos = 10;

        while (intentos < maxIntentos) {
            Thread.sleep(3000);

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url + containerId + "/files/" + fileId))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> getResp = HTTP_CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("ACRCloud GET status: " + getResp.statusCode());
            System.out.println("ACRCloud GET body: " + getResp.body());

            JsonObject root = gson.fromJson(getResp.body(), JsonObject.class);
            JsonObject data = root.getAsJsonArray("data").get(0).getAsJsonObject();
            int state = data.get("state").getAsInt();

            if (state == 1) {
                return parsearResultado(data);
            } else if (state == -1 || state == -2 || state == -3) {
                System.err.println("ACRCloud error en procesamiento, state: " + state);
                return new ACRCloudeDTO(0.0);
            }

            intentos++;
        }

        return new ACRCloudeDTO(0.0);
    }

    private ACRCloudeDTO parsearResultado(JsonObject data) {
        try {
            JsonArray aiDetection = data.getAsJsonObject("results")
                    .getAsJsonArray("ai_detection");

            double aiProbability = aiDetection.get(0).getAsJsonObject()
                    .get("ai_probability").getAsDouble();

            return new ACRCloudeDTO(aiProbability);

        } catch (Exception e) {
            System.err.println("Error al parsear ACRCloud: " + e.getMessage());
            return new ACRCloudeDTO(0.0);
        }
    }
}