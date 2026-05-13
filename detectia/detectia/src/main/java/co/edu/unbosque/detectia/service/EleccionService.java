package co.edu.unbosque.detectia.service;

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
	
	@Autowired SightengineService sightengine;

	@Autowired
	private TwelveLabsService twelveLabs;

	@Autowired
	private HiveVideoService hiveVideo;

	public Map<String, Double> analizar(MultipartFile archivo) throws Exception {
	    String tipo = extractor.detectarTipo(archivo);
	    
	    if (tipo.contains("pdf")) {
	        return analizarPDF(archivo); // PDFs van a método especial
	    } else if (esDocumento(tipo)) {
	        return analizarTexto(archivo);
	    }else if (tipo.startsWith("image")){
	    	return analizarImagen(archivo);
	    }
	    return new HashMap<>();
	}

	private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);

		System.out.println("TEXTO EXTRAIDO: [" + texto + "]");
		System.out.println("LONGITUD: " + texto.length());

			System.out.println("TEXTO EXTRAIDO: [" + texto + "]");
			System.out.println("LONGITUD: " + texto.length());

			if (texto == null || texto.isBlank()) {
				return new HashMap<>();
			}

			Map<String, Double> resultadosIA = new HashMap<>();
			
			// Invocación a las APIs de texto
			//resultadosIA.put("ZeroGPT", zeroGPT.detectarIA(texto).getData().getIs_gpt_generated());
			resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			resultadosIA.put("Gemini", gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
			resultadosIA.put("HuggingFace", huggingFace.detectarIA(texto).getPorcentajeIA());
			resultadosIA.put("Mistral", mistral.detectarIA(texto, archivo.getBytes(), archivo.getContentType()).getFakePercentage());

			return resultadosIA;
		}
		
		private Map<String, Double> analizarPDF(MultipartFile archivo) throws Exception {
		    String texto = extractor.extraerTexto(archivo);
		    byte[] bytes = archivo.getBytes();
		    String mimeType = archivo.getContentType();
		    
		    Map<String, Double> resultadosIA = new HashMap<>();

		    // Gemini y Mistral reciben los bytes completos del PDF (analizan texto + imágenes)
		    resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());


		    // Groq y HuggingFace solo pueden analizar el texto extraído
		    if (texto != null && !texto.isBlank()) {
		        resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
		        resultadosIA.put("HuggingFace", huggingFace.detectarIA(texto).getPorcentajeIA());
			    resultadosIA.put("Mistral", mistral.detectarIA(texto, bytes, mimeType).getFakePercentage());
		    }

		    return resultadosIA;
		}

		Map<String, Double> resultadosIA = new HashMap<>();

//			resultadosIA.put("ZeroGPT", zeroGPT.detectarIA(texto).getData().getIs_gpt_generated());
		resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
		resultadosIA.put("Gemini", gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());

		return resultadosIA;
	}

	// ===============================
	// 🖼 IMAGEN
	// ===============================
	// ===============================
		// 🖼 IMAGEN (Ajustado para retornar Map)
		// ===============================
		private Map<String, Double> analizarImagen(MultipartFile archivo) throws Exception {
			Map<String, Double> resultadosIA = new HashMap<>();
			byte[] bytes = archivo.getBytes();
			String mimeType = archivo.getContentType();
			resultadosIA.put("Sightengine", sightengine.detectarIA(bytes, mimeType ).getAi_generated());
			resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());

		// Invocación a la API de visión
		HuggingFaceResponseDTO r = huggingFace.analizarArchivo(archivo.getBytes());
		resultadosIA.put("HuggingFace", r.getScore());

		return resultadosIA;
	}
	
	private Map<String, Double> analizarVideo(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();

		TwelveLabsDTO tl = twelveLabs.detectarIA(
				archivo.getBytes(),
				archivo.getOriginalFilename(),
				archivo.getContentType());
		resultadosIA.put("TwelveLabs", tl.getPorcentajeIA());

		HiveVideoDTO hv = hiveVideo.detectarIA(
				archivo.getBytes(),
				archivo.getOriginalFilename(),
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