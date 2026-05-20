package co.edu.unbosque.detectia.service;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class EleccionService {

	@Autowired
	private TextoExtractorService extractor;

	@Autowired
	private GrokService grok;

	@Autowired
	private GeminiService gemini;

	@Autowired
	private MistralService mistral;

	@Autowired
	private SightengineService sightengine;

	@Autowired
	private TwelveLabsService twelveLabs;

//	@Autowired
//	private HiveVideoService hiveVideo;

	@Autowired
	private WinstonService winston;
	
	@Autowired
	private ACRCloudService acrCloude;
	
	@Autowired
	private HiveModerationService hiveModeration;


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
		}else if (tipo.startsWith("audio") || tipo.contains("mpeg") || tipo.contains("wav") || tipo.contains("ogg")) {
			return analizarMusica(archivo);
		}
		return new HashMap<>();
	}
	
	public Map<String, Double> analizar(String url) throws Exception {
	    String urlMinuscula = url.toLowerCase();
	    if (urlMinuscula.endsWith(".jpg") || urlMinuscula.endsWith(".jpeg") || 
	        urlMinuscula.endsWith(".png") || urlMinuscula.endsWith(".webp")) {
	        
	        return analizarImagenURL(url);
	    }
	    
	    return new HashMap<>();
	}

	private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);
		Map<String, Double> resultadosIA = new HashMap<>();
		/*SIRVE*///resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
		/*SIRVE*///resultadosIA.put("Gemini", gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
		/*SIRVE*///resultadosIA.put("Mistral",mistral.detectarIA(texto, archivo.getBytes(), archivo.getContentType()).getFakePercentage());
		/*SIRVE*///resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
		return resultadosIA;
	}

	private Map<String, Double> analizarPDF(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();

		Map<String, Double> resultadosIA = new HashMap<>();
		/*no se si si sirva*///resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		/*no se si si sirva*///resultadosIA.put("Mistral", mistral.detectarIA(texto, bytes, mimeType).getFakePercentage());
		if (texto != null && !texto.isBlank()) {
			/*no se si si sirva*///resultadosIA.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			/*no se si si sirva*///resultadosIA.put("Winston", winston.detectarIA(texto).getScore());
		}

		return resultadosIA;
	}


	private Map<String, Double> analizarImagen(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();
		/*SIRVE*///resultadosIA.put("Sightengine", sightengine.detectarIA(bytes, mimeType).getAi_generated());
		/*SIRVE*///resultadosIA.put("Gemini", gemini.detectarIA(bytes, mimeType).getPorcentajeIA());
		/*SIRVE*///resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated())
		/*SIRVE MEH PASANDO A BIEN*///resultadosIA.put("Groq", grok.detectarIAImagen(bytes, mimeType).getPorcentajeIA());
		return resultadosIA;
	}

	private Map<String, Double> analizarImagenURL(String url) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		/*SIRVE*///resultadosIA.put("Winston", winston.detectarIAImagen(url).getScore());
		/*SIRVE*///resultadosIA.put("Sightengine", sightengine.detectarIAUrl(url).getAi_generated());
		/*SIRVE*///resultadosIA.put("Gemini", gemini.detectarIAPorUrl(url).getPorcentajeIA());
		/*SIRVE MEH PASANDO A BIEN*///resultadosIA.put("Groq", grok.detectarIAImagenUrl(url).getPorcentajeIA());
		
		return resultadosIA;
	}

	private Map<String, Double> analizarVideo(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		byte[] bytes = archivo.getBytes();
		String mimeType = archivo.getContentType();
		/*SIRVE A MEDIAS*///resultadosIA.put("TwelveLabs", twelveLabs.detectarIA(bytes, mimeType).getPorcentajeIA());
		/*SIRVE*///resultadosIA.put("Hive Moderation", hiveModeration.detectarIA(bytes, mimeType).getAi_generated());

		return resultadosIA;
	}
	
	
	private Map<String, Double> analizarMusica(MultipartFile archivo) throws Exception {
		Map<String, Double> resultadosIA = new HashMap<>();
		/*SIRVE*///resultadosIA.put("ACRCloud", acrCloude.detectarIAArchivo(archivo).getAi_probability());
		return resultadosIA;
	}
	
	private boolean esDocumento(String tipo) {
		return tipo.startsWith("text") || tipo.contains("word");
	}

}
