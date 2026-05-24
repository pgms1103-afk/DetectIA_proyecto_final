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

import co.edu.unbosque.detectia.dto.GrokDTO;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

@Service
public class GrokService {
	
    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String modelText;

    @Value("${groq.model.vision}")
    private String modelVision;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Análisis avanzado de Texto
     */
    public GrokDTO detectarIA(String texto) throws Exception {
        JsonArray messages = new JsonArray();
        
        // Prompt optimizado para análisis lingüístico formal
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
            "Eres un analizador forense de texto especializado en detectar contenido generado por modelos de lenguaje (LLMs como ChatGPT, Gemini, Claude, Llama, Mistral).\n" +
            "Evalúa el texto aplicando los siguientes criterios:\n\n" +
            "[SEÑALES DE IA - aumentan el score]\n" +
            "- Baja 'burstiness': oraciones de longitud uniforme y ritmo constante y predecible.\n" +
            "- Transiciones sobreusadas: 'en resumen', 'es importante destacar', 'en conclusión', 'cabe mencionar', 'sin embargo', 'en este sentido', 'es fundamental'.\n" +
            "- Listas perfectamente paralelas y simétricas sin necesidad narrativa real.\n" +
            "- Vocabulario inflado o rebuscado donde un humano usaría términos más simples y directos.\n" +
            "- Estructura rígida de introducción-desarrollo-conclusión incluso en textos cortos.\n" +
            "- Sobreexplicaciones de conceptos básicos que el lector ya conoce.\n" +
            "- Hipercorrección gramatical excesiva y ausencia total de informalidad.\n" +
            "- Afirmaciones genéricas y seguras que evitan toda controversia o postura personal.\n\n" +
            "[SEÑALES HUMANAS - reducen el score]\n" +
            "- Errores tipográficos, ortográficos o gramaticales naturales.\n" +
            "- Expresiones coloquiales, jerga, humor, ironía o sarcasmo.\n" +
            "- Narrativa no lineal, digresiones o cambios abruptos de tema.\n" +
            "- Subjetividad emocional clara: opiniones fuertes, frustración, entusiasmo genuino.\n" +
            "- Uso de primera persona con experiencias personales concretas y específicas.\n" +
            "- Repetición involuntaria de palabras o ideas (los humanos no somos editores perfectos).\n\n" +
            "Calibración de escala: 0=Humano con certeza absoluta | 25=Probablemente humano | 50=Ambiguo | 75=Probablemente IA | 100=IA sintética con certeza absoluta.\n\n" +
            "REGLA DE SALIDA ESTRICTA: Devuelve ÚNICAMENTE el número entero entre 0 y 100. Sin texto, sin '%', sin explicaciones, sin saltos de línea."
        );
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", "Analiza el siguiente bloque de texto:\n\"\"\"\n" + texto + "\n\"\"\"");
        messages.add(userMessage);

        return ejecutarPeticion(messages, modelText);
    }

    /**
     * Análisis avanzado de Imágenes (Multimodal)
     */
    public GrokDTO detectarIAImagen(byte[] archivoBytes, String contentType) throws Exception {
    	
    	if(!contentType.equals("image/gif") && !contentType.equals("image/jpeg") &&
    	   !contentType.equals("image/png") && !contentType.equals("image/webp")) {
    		throw new ExtensionInvalidaException("Groq no permite este tipo de imagen "+contentType+" Formatos acepetados:JPEG,PNG,GIF,WEPB");
    	}
    	
    	if(archivoBytes.length > 20L*1024*1024) {
    		double mb = archivoBytes.length/ (1024*1024);
    		throw new TamanoInvalidoException(String.format("Groq el archivo(%.1f MB) supera el limite permitibo de MB", mb));
    	}
    	
        String base64Imagen = Base64.getEncoder().encodeToString(archivoBytes);
        String dataUrl = "data:" + contentType + ";base64," + base64Imagen;

        JsonArray messages = new JsonArray();
        
        // Prompt optimizado para visión artificial forense
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
            "Eres un sistema pericial de visión artificial especializado en la detección de imágenes generadas por IA (Diffusion Models como Midjourney, DALL-E, Stable Diffusion). " +
            "Debes escanear la imagen del usuario buscando los siguientes artefactos críticos: " +
            "1. Incoherencias en el espectro de iluminación, sombras invertidas o reflejos imposibles.\n" +
            "2. Texturas de piel excesivamente suavizadas ('efecto cera') o ruido digital homogéneo anormal.\n" +
            "3. Deformaciones geométricas en fondos complejos, líneas rectas que se curvan o patrones repetitivos de ruido.\n" +
            "4. Errores anatómicos sutiles (ojos asimétricos, fusiones extrañas de objetos, detalles incoherentes en bordes).\n" +
            "Calcula la probabilidad acumulada de fraude de 0 a 100 (0 = Fotografía/Arte Humano Real, 100 = IA Evidente).\n" +
            "REGLA DE SALIDA ESTRICTA: Analiza mentalmente, pero responde ÚNICAMENTE con el número entero final. No agregues explicaciones, ni símbolos, ni formato Markdown."
        );
        messages.add(systemMessage);

        JsonArray userContentArray = new JsonArray();

        // Prompt del usuario con orden pericial directo
        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", "Ejecuta el protocolo de análisis forense visual sobre este archivo multimedia.");
        userContentArray.add(textContent);

        JsonObject imageUrlContent = new JsonObject();
        imageUrlContent.addProperty("type", "image_url");
        JsonObject imageUrlObj = new JsonObject();
        imageUrlObj.addProperty("url", dataUrl);
        imageUrlContent.add("image_url", imageUrlObj);
        userContentArray.add(imageUrlContent);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.add("content", userContentArray);
        messages.add(userMessage);

        return ejecutarPeticion(messages, modelVision);
    }
    
 // 3. Análisis de Imagen por URL Pública
    public GrokDTO detectarIAImagenUrl(String urlImagen) throws Exception {
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
            "Eres un experto en detección de contenido generado por IA. " +
            "Tu única tarea es analizar la imagen y devolver un número entre 0 y 100 " +
            "que represente la probabilidad de que haya sido generado por IA. " +
            "0 = completamente humano, 100 = completamente generado por IA. " +
            "Responde ÚNICAMENTE con el número, sin explicaciones ni texto adicional."
        );

        JsonArray userContentArray = new JsonArray();

        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", "Analiza esta imagen para buscar patrones sintéticos.");
        userContentArray.add(textContent);

        JsonObject imageUrlContent = new JsonObject();
        imageUrlContent.addProperty("type", "image_url");
        JsonObject imageUrlObj = new JsonObject();
        imageUrlObj.addProperty("url", urlImagen); // 💡 Aquí pasa la URL directa
        imageUrlContent.add("image_url", imageUrlObj);
        userContentArray.add(imageUrlContent);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.add("content", userContentArray);

        JsonArray messages = new JsonArray();
        messages.add(systemMessage);
        messages.add(userMessage);

        return ejecutarPeticion(messages, modelVision);
    }

    private GrokDTO ejecutarPeticion(JsonArray messages, String modeloUtilizado) throws Exception {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", modeloUtilizado);
        jsonBody.add("messages", messages);
        jsonBody.addProperty("temperature", 0.2); // 💡 Reducido a 0.0 para obligar al modelo a ser determinista y seguir la regla del número entero
        jsonBody.addProperty("max_completion_tokens", 5); // 💡 Reducido a 5 tokens para recortar cualquier intento de explicación verbal

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        HttpResponse<String> respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());

        System.out.println("Groq status: " + respuesta.statusCode());

        if (respuesta.statusCode() != 200) {
            System.err.println("Error Groq API: " + respuesta.body());
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

            // 💡 Por seguridad eliminamos cualquier caracter residual no numérico que el modelo intente colar
            texto = texto.replaceAll("[^0-9]", "");

            double porcentaje = Double.parseDouble(texto);
            String veredicto = porcentaje >= 50 ? "PROBABLE IA" : "PROBABLE HUMANO";
            return new GrokDTO(porcentaje, veredicto);

        } catch (Exception e) {
            System.err.println("Error al parsear Groq: " + e.getMessage());
            return new GrokDTO(0.0, "ERROR_PARSEO");
        }
    }
}