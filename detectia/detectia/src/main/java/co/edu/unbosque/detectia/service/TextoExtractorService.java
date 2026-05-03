package co.edu.unbosque.detectia.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.io.IOException;
import org.apache.pdfbox.Loader;

@Service
public class TextoExtractorService {

	public String extraerTexto(MultipartFile archivo) throws IOException {
        String nombreArchivo = archivo.getOriginalFilename().toLowerCase();
        
        if (nombreArchivo.endsWith(".txt")) {
            return extraerDeTXT(archivo);
        } else if (nombreArchivo.endsWith(".pdf")) {
            return extraerDePDF(archivo);
        } else if (nombreArchivo.endsWith(".docx")) {
            return extraerDeDOCX(archivo);
        } else {
            throw new IOException("Tipo de archivo no soportado");
        }
    }
	
	 private String extraerDeTXT(MultipartFile archivo) throws IOException {
	        return new String(archivo.getBytes());
	    }
	 
	 private String extraerDePDF(MultipartFile archivo) throws IOException {
		    Loader.loadPDF(archivo.getBytes());
		    PDDocument documento = Loader.loadPDF(archivo.getBytes());
		    PDFTextStripper stripper = new PDFTextStripper();
		    String texto = stripper.getText(documento);
		    documento.close();
		    return texto;
		}

	 private String extraerDeDOCX(MultipartFile archivo) throws IOException {
	        XWPFDocument documento = new XWPFDocument(archivo.getInputStream());
	        XWPFWordExtractor extraer = new XWPFWordExtractor(documento);
	        String texto = extraer.getText();
	        extraer.close();
	        return texto;
	    }
}
