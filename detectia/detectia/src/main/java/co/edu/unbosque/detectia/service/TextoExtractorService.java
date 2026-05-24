package co.edu.unbosque.detectia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;

/**
 * Servicio de extracción de texto y detección de tipo MIME mediante Apache Tika.
 * <p>
 * Actúa como capa de abstracción sobre la biblioteca Tika, proporcionando dos
 * operaciones principales: parsear el contenido textual de cualquier archivo
 * multipart y detectar su tipo MIME, sin importar la extensión declarada.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 */
@Service
public class TextoExtractorService {

    private final Tika tika = new Tika();

    /**
     * Extrae el contenido textual del archivo multipart utilizando Apache Tika.
     * <p>
     * Soporta una amplia variedad de formatos: PDF, DOCX, TXT, HTML, entre otros.
     * Si Tika devuelve {@code null}, retorna una cadena vacía.
     * </p>
     *
     * @param archivo el archivo multipart del que se desea extraer el texto
     * @return el texto extraído del archivo, o cadena vacía si no contiene texto
     * @throws IOException si ocurre un error al leer el flujo de entrada del archivo
     */
    public String extraerTexto(MultipartFile archivo) throws IOException {
        try {
            String texto = tika.parseToString(archivo.getInputStream());

            if (texto == null) {
                return "";
            }

            return texto;

        } catch (Exception e) {
            throw new IOException("Error extrayendo texto con Tika", e);
        }
    }

    /**
     * Detecta el tipo MIME real del archivo multipart utilizando Apache Tika.
     * <p>
     * La detección se basa en la inspección del contenido del archivo (magic bytes),
     * sin depender de la extensión declarada ni del campo {@code Content-Type} del
     * multipart.
     * </p>
     *
     * @param archivo el archivo multipart del que se desea detectar el tipo
     * @return cadena con el tipo MIME detectado (p. ej. {@code "application/pdf"})
     * @throws IOException si ocurre un error al leer el flujo de entrada del archivo
     */
    public String detectarTipo(MultipartFile archivo) throws IOException {
        try {
            return tika.detect(archivo.getInputStream());
        } catch (Exception e) {
            throw new IOException("Error detectando tipo con Tika", e);
        }
    }
}