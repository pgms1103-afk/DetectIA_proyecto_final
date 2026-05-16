package co.edu.unbosque.detectia.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.GeminiDTO;
import co.edu.unbosque.detectia.dto.GrokDTO;
import co.edu.unbosque.detectia.dto.HuggingFaceDTO;
import co.edu.unbosque.detectia.dto.HiveVideoDTO;
import co.edu.unbosque.detectia.dto.ResultadoAnalisisDTO;
import co.edu.unbosque.detectia.dto.TwelveLabsDTO;
import co.edu.unbosque.detectia.dto.ZeroGPTResponseDTO;

@Service
public class EleccionService {

	@Autowired
	private TextoExtractorService extractor;

	@Autowired
	private ZeroGPTService zeroGPT;

	@Autowired
	private GrokService grok;

	@Autowired
	private GeminiService gemini;

	@Autowired
	private HuggingFaceService huggingFace;

	@Autowired
	private MistralService mistral;

	@Autowired
	private SightengineService sightengine;

	@Autowired
	private TwelveLabsService twelveLabs;

	@Autowired
	private HiveVideoService hiveVideo;

	@Autowired
	private WinstonService winston;

	public Map<String, Double> analizar(MultipartFile archivo) throws Exception {
		String tipo = extractor.detectarTipo(archivo);

		if (tipo.contains("pdf")) {
			return analizarPDF(archivo);
		} else if (esDocumento(tipo)) {
			return analizarTexto(archivo);
		} else if (tipo.startsWith("image")) {
			return analizarImagen(archivo);
		} else if (tipo.startsWith("video")) {
			return analizarVideo(archivo);
		}
		return new HashMap<>();
	}
	
	public Map<String, Double> analizar(String url) throws Exception {
	    String urlMinuscula = url.toLowerCase();
	    
	    if (urlMinuscula.endsWith(".jpg") || urlMinuscula.endsWith(".jpeg") || 
	        urlMinuscula.endsWith(".png") || urlMinuscula.endsWith(".webp")) {
	        
	        return analizarImagenURL(url);
	    }
	    
	    // Si en el futuro manejas URLs de videos o páginas web, pondrías los otros else if aquí
	    return new HashMap<>();
	}

	private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);

		System.out.println("TEXTO EXTRAIDO: [" + texto + "]");
		System.out.println("LONGITUD: " + texto.length());

		Map<String, Double> resultadosIA = new HashMap<>();

		// Invocación a las APIs de texto
		//resultadosIA.put("ZeroGPT", zeroGPT.detectarIA(texto).getData().getIs_gpt_generated());
		/*SIRVE*/resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
		/*SIRVE*/resultadosIA.put("Gemini", gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
		/*pienso en quitarla, muy malos resultados*/resultadosIA.put("HuggingFace", huggingFace.detectarIA(texto).getPorcentajeIA());
		/*SIRVE*/resultadosIA.put("Mistral",mistral.detectarIA(texto, archivo.getBytes(), archivo.getContentType()).getFakePercentage());
		/*SIRVE*/resultadosIA.put("Winstral", winston.detectarIA(texto).getScore());
		return resultadosIA;
	}

	private Map<String, Double> analizarPDF(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();

		Map<String, Double> resultadosIA = new HashMap<>();

		// Gemini y Mistral reciben los bytes completos del PDF (analizan texto +
		// imágenes)
		resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());

		// Groq y HuggingFace solo pueden analizar el texto extraído
		if (texto != null && !texto.isBlank()) {
			/*SIRVE*/resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			/*pienso en quitarla, muy malos resultados*/resultadosIA.put("HuggingFace", huggingFace.detectarIA(texto).getPorcentajeIA());
			/*SIRVE*/resultadosIA.put("Mistral", mistral.detectarIA(texto, bytes, mimeType).getFakePercentage());
			/*SIRVE*/resultadosIA.put("Winstral", winston.detectarIA(texto).getScore());
		}

		return resultadosIA;
	}

	// 🖼 IMAGEN (Ajustado para retornar Map)
	// ===============================

	private Map<String, Double> analizarImagen(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();
		/*SIRVE*/resultadosIA.put("Sightengine", sightengine.detectarIA(bytes, mimeType).getAi_generated());
		/*SIRVE*/resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());

		return resultadosIA;
	}

	private Map<String, Double> analizarImagenURL(String url) throws IOException, InterruptedException {
		Map<String, Double> resultadosIA = new HashMap<>();
		/*SIRVE*/resultadosIA.put("Winstral", winston.detectarIAImagen(url).getScore());
		
		return resultadosIA;
	}

	private Map<String, Double> analizarVideo(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();

		TwelveLabsDTO tl = twelveLabs.detectarIA(archivo.getBytes(), archivo.getOriginalFilename(),
				archivo.getContentType());
		resultadosIA.put("TwelveLabs", tl.getPorcentajeIA());

		HiveVideoDTO hv = hiveVideo.detectarIA(archivo.getBytes(), archivo.getOriginalFilename(),
				archivo.getContentType());
		resultadosIA.put("HiveVideo", hv.getPorcentajeIA());

		return resultadosIA;
	}

	// ===============================
	// 🔍 HELPERS
	// ===============================
	private boolean esDocumento(String tipo) {
		return tipo.startsWith("text") || tipo.contains("word");
	}

}
