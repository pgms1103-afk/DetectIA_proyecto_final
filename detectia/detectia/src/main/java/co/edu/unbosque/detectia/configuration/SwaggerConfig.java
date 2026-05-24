package co.edu.unbosque.detectia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
/**
 * Configuración de la documentación OpenAPI (Swagger) para la plataforma
 * DetectIA.
 * <p>
 * Construye el objeto {@link OpenAPI} con metadatos de la API (título, versión,
 * descripción, contacto y licencia), el esquema de seguridad Bearer JWT y un
 * conjunto de respuestas de error reutilizables (401, 403, 404, 400, 409).
 * La documentación es accesible en {@code /swagger-ui/index.html}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 */
@Configuration

public class SwaggerConfig {
	
	 @Bean
	    public OpenAPI customOpenAPI() {

	        String mainDescription =
	            "<h2>Guia para principiantes de la API REST de Detectia</h2>" +
	            "<p>Esta API permite registrar usuarios, subir archivos y analizarlos con multiples " +
	            "modelos de inteligencia artificial para detectar si el contenido fue generado por IA.</p>" +

	            "<h3>Conceptos basicos:</h3><ul>" +
	            "    <li><strong>JWT (JSON Web Token)</strong>: Cuando inicias sesion, recibes un token " +
	            "        que debes incluir en las cabeceras de tus peticiones posteriores.</li>" +
	            "    <li><strong>Autenticacion</strong>: Proceso de verificar tu identidad mediante " +
	            "        credenciales (nombre de usuario y contrasena).</li>" +
	            "    <li><strong>Autorizacion</strong>: Determina que acciones puede realizar un usuario " +
	            "        autenticado segun su rol.</li>" +
	            "    <li><strong>Analisis IA</strong>: Cada archivo subido es analizado por multiples " +
	            "        modelos de IA. El resultado final es un consenso de todos los modelos.</li>" +
	            "</ul>" +

	            "<h3>Flujo basico de uso:</h3><ol>" +
	            "    <li>Registra un nuevo usuario usando <code>/public/registrarusuario</code></li>" +
	            "    <li>Inicia sesion con <code>/public/login</code> para obtener un token JWT</li>" +
	            "    <li>Incluye el token en el encabezado de autorizacion: " +
	            "        <code>Authorization: Bearer tu_token_jwt</code></li>" +
	            "    <li>Sube un archivo con <code>/private/archivo/analizar</code> para analizarlo</li>" +
	            "    <li>Consulta los resultados con <code>/private/resultadoporia/mostrarresultadoporarchivo</code></li>" +
	            "</ol>" +

	            "<h3>Roles de usuario:</h3><ul>" +
	            "    <li><strong>USUARIO</strong>: Puede subir archivos, analizarlos y ver sus propios resultados</li>" +
	            "    <li><strong>ADMIN</strong>: Puede gestionar usuarios, ver todos los registros de auditoria " +
	            "        y realizar todas las operaciones de USUARIO</li>" +
	            "</ul>" +

	            "<h3>Modelos de IA disponibles:</h3><ul>" +
	            "    <li><strong>Gemini</strong>: Modelo de Google para deteccion de contenido IA</li>" +
	            "    <li><strong>Mistral</strong>: Modelo de lenguaje para analisis de texto e imagen</li>" +
	            "    <li><strong>Grok</strong>: Modelo de xAI para deteccion avanzada</li>" +
	            "    <li><strong>Hive Moderation</strong>: Especializado en moderacion de contenido visual</li>" +
	            "    <li><strong>Sightengine</strong>: Motor de vision para deteccion de imagenes IA</li>" +
	            "    <li><strong>ACR Cloud</strong>: Especializado en deteccion de audio generado por IA</li>" +
	            "    <li><strong>TwelveLabs</strong>: Especializado en analisis de video</li>" +
	            "    <li><strong>Winston</strong>: Especializado en deteccion de texto generado por IA</li>" +
	            "</ul>" +

	            "<h3>Codigos de estado HTTP comunes:</h3><ul>" +
	            "    <li><strong>200/201</strong>: Operacion exitosa</li>" +
	            "    <li><strong>400</strong>: Error en la solicitud (datos incorrectos o archivo invalido)</li>" +
	            "    <li><strong>401</strong>: No autenticado (token invalido o expirado)</li>" +
	            "    <li><strong>403</strong>: No autorizado (no tienes permisos suficientes)</li>" +
	            "    <li><strong>404</strong>: Recurso no encontrado</li>" +
	            "    <li><strong>409</strong>: Conflicto (por ejemplo, nombre de usuario ya existente)</li>" +
	            "    <li><strong>204</strong>: Sin contenido (la consulta fue exitosa pero no hay datos)</li>" +
	            "</ul>";

	        String securityDescription =
	            "Autenticacion mediante JWT (JSON Web Token)." +
	            "<p>Para autenticarte, sigue estos pasos:</p>" +
	            "<ol>" +
	            "    <li>Obtén un token JWT usando el endpoint <code>/public/login</code></li>" +
	            "    <li>Copia el token recibido en la respuesta</li>" +
	            "    <li>Haz clic en el boton <strong>Authorize</strong> en la parte superior de esta pagina</li>" +
	            "    <li>En el campo <strong>Value</strong>, escribe: <code>Bearer tu_token_jwt</code></li>" +
	            "    <li>Haz clic en <strong>Authorize</strong> y luego en <strong>Close</strong></li>" +
	            "</ol>" +
	            "<p>Ahora podras acceder a los endpoints protegidos.</p>";

	        io.swagger.v3.oas.models.info.Info info =
	            new io.swagger.v3.oas.models.info.Info()
	                .title("Detectia API")
	                .version("1.0")
	                .description(mainDescription)
	                .contact(
	                    new io.swagger.v3.oas.models.info.Contact()
	                        .name("Equipo de Desarrollo Detectia")
	                        .email("soporte@detectia.com")
	                        .url("https://github.com/tu-usuario/detectia"))
	                .license(
	                    new io.swagger.v3.oas.models.info.License()
	                        .name("Licencia MIT")
	                        .url("https://opensource.org/licenses/MIT"));

	        io.swagger.v3.oas.models.security.SecurityScheme securityScheme =
	            new io.swagger.v3.oas.models.security.SecurityScheme()
	                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
	                .scheme("bearer")
	                .bearerFormat("JWT")
	                .description(securityDescription);

	        return new OpenAPI()
	            .info(info)
	            .components(
	                new Components()
	                    .addSecuritySchemes("bearerAuth", securityScheme)
	                    .addResponses(
	                        "UnauthorizedError",
	                        new ApiResponse()
	                            .description("No autenticado - Token JWT invalido o expirado")
	                            .content(
	                                new Content()
	                                    .addMediaType(
	                                        "application/json",
	                                        new MediaType()
	                                            .addExamples(
	                                                "error",
	                                                new Example()
	                                                    .value(
	                                                        "{\"error\": \"No autorizado\", \"mensaje\": " +
	                                                        "\"Token invalido o expirado\"}")))))
	                    .addResponses(
	                        "ForbiddenError",
	                        new ApiResponse()
	                            .description("Acceso prohibido - No tienes permisos suficientes")
	                            .content(
	                                new Content()
	                                    .addMediaType(
	                                        "application/json",
	                                        new MediaType()
	                                            .addExamples(
	                                                "error",
	                                                new Example()
	                                                    .value(
	                                                        "{\"error\": \"Acceso prohibido\", \"mensaje\": " +
	                                                        "\"No tienes permisos para esta operacion\"}")))))
	                    .addResponses(
	                        "NotFoundError",
	                        new ApiResponse()
	                            .description("Recurso no encontrado")
	                            .content(
	                                new Content()
	                                    .addMediaType(
	                                        "application/json",
	                                        new MediaType()
	                                            .addExamples(
	                                                "error",
	                                                new Example()
	                                                    .value(
	                                                        "{\"error\": \"No encontrado\", \"mensaje\": " +
	                                                        "\"El recurso solicitado no existe\"}")))))
	                    .addResponses(
	                        "BadRequestError",
	                        new ApiResponse()
	                            .description("Datos invalidos - Extension de archivo no permitida o datos incorrectos")
	                            .content(
	                                new Content()
	                                    .addMediaType(
	                                        "application/json",
	                                        new MediaType()
	                                            .addExamples(
	                                                "error",
	                                                new Example()
	                                                    .value(
	                                                        "{\"error\": \"Solicitud invalida\", \"mensaje\": " +
	                                                        "\"Extension de archivo no permitida\"}")))))
	                    .addResponses(
	                        "ConflictError",
	                        new ApiResponse()
	                            .description("Conflicto - El nombre de usuario ya existe")
	                            .content(
	                                new Content()
	                                    .addMediaType(
	                                        "application/json",
	                                        new MediaType()
	                                            .addExamples(
	                                                "error",
	                                                new Example()
	                                                    .value(
	                                                        "{\"error\": \"Conflicto\", \"mensaje\": " +
	                                                        "\"El nombre de usuario ya existe\"}"))))));
	    }
	
	
}