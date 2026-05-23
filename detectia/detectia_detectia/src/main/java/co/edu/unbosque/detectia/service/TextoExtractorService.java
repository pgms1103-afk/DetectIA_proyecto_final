package co.edu.unbosque.detectia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;

@Service
public class TextoExtractorService {

    private final Tika tika = new Tika();

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

    public String detectarTipo(MultipartFile archivo) throws IOException {
        try {
            return tika.detect(archivo.getInputStream());
        } catch (Exception e) {
            throw new IOException("Error detectando tipo con Tika", e);
        }
    }
}