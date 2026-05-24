package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase {@link WinstonService}.
 * <p>
 * Verifica que el servicio de detección de IA mediante Winston AI rechace
 * correctamente entradas de texto inválidas: valores nulos, cadenas vacías y
 * textos que no cumplen la longitud mínima requerida (500 caracteres) para
 * realizar un análisis significativo.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see WinstonService
 */
class WinstonServiceTest {

    /** Instancia del servicio bajo prueba. */
    private WinstonService service;

    /**
     * Crea una nueva instancia del servicio e inyecta valores de API ficticios
     * antes de cada prueba. Las pruebas de validación se ejecutan antes de
     * cualquier llamada HTTP, por lo que los valores no importan.
     */
    @BeforeEach
    void setUp() {
        service = new WinstonService();
        try {
            var f1 = WinstonService.class.getDeclaredField("apiKey");
            f1.setAccessible(true);
            f1.set(service, "test-key");

            var f2 = WinstonService.class.getDeclaredField("apiUrlTexto");
            f2.setAccessible(true);
            f2.set(service, "https://api.test.com/texto");

            var f3 = WinstonService.class.getDeclaredField("apiUrlImagen");
            f3.setAccessible(true);
            f3.set(service, "https://api.test.com/imagen");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link IllegalArgumentException} cuando el texto
     * proporcionado es {@code null}.
     */
    @Test
    void testDetectarIA_TextoNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.detectarIA(null));
    }

    /**
     * Verifica que se lanza {@link IllegalArgumentException} cuando el texto
     * proporcionado es una cadena vacía.
     */
    @Test
    void testDetectarIA_TextoVacio() {
        assertThrows(IllegalArgumentException.class, () -> service.detectarIA(""));
    }

    /**
     * Verifica que se lanza {@link IllegalArgumentException} cuando el texto
     * es demasiado corto (menos de 500 caracteres), que es el mínimo requerido
     * por Winston AI para un análisis fiable.
     */
    @Test
    void testDetectarIA_TextoMuyCorto() {
        assertThrows(IllegalArgumentException.class, () -> service.detectarIA("texto corto"));
    }

    /**
     * Verifica que no se lanza {@link IllegalArgumentException} cuando el texto
     * cumple la longitud mínima. La excepción de red es ignorada en este
     * contexto de prueba unitaria.
     */
    @Test
    void testDetectarIA_TextoSuficientementeLargo() {
        // Generar texto de al menos 500 caracteres
        String textoLargo = "Lorem ipsum dolor sit amet. ".repeat(20);

        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(textoLargo);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception ignored) {
                // Excepción de red ignorada — no hay API real en el test
            }
        });
    }
}
