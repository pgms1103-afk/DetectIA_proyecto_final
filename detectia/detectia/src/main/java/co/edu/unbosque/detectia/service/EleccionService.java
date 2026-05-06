package co.edu.unbosque.detectia.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.GeminiDTO;
import co.edu.unbosque.detectia.dto.GrokDTO;
import co.edu.unbosque.detectia.dto.HuggingFaceResponseDTO;
import co.edu.unbosque.detectia.dto.ResultadoAnalisisDTO;
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

	public Map<String, Double> analizar(MultipartFile archivo) throws Exception {
		String tipo = extractor.detectarTipo(archivo);

		// El mapa que guardará los "votos" individuales
		Map<String, Double> resultadosIndividuales = new HashMap<>();

		if (esDocumento(tipo)) {
			String texto = extractor.extraerTexto(archivo);

			// Guardamos los votos directamente
			resultadosIndividuales.put("ZeroGPT", zeroGPT.detectarIA(texto).getData().getIs_gpt_generated());
			resultadosIndividuales.put("Grok", grok.detectarIA(texto).getPorcentajeIA());
			resultadosIndividuales.put("Gemini",
					gemini.detectarIA(archivo.getBytes(), archivo.getContentType()).getPorcentajeIA());
		} else if (tipo.startsWith("image")) {
			// Guardamos el voto de la IA de imagen
			resultadosIndividuales.put("HuggingFace", huggingFace.analizarArchivo(archivo.getBytes()).getScore());
		}

		return resultadosIndividuales;
	}

	// ===============================
	// 📄 TEXTO
	// ===============================
	private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
		String texto = extractor.extraerTexto(archivo);

		// Si no hay texto, devolvemos el mapa vacío o con una marca
		if (texto == null || texto.isBlank()) {
			return new HashMap<>();
		}

		// Llamamos a las IAs
		ZeroGPTResponseDTO r1 = zeroGPT.detectarIA(texto);
		GrokDTO r2 = grok.detectarIA(texto);
		GeminiDTO r3 = gemini.detectarIA(archivo.getBytes(), archivo.getContentType());

		// Creamos el mapa de "votos" individuales
		Map<String, Double> resultadosIA = new HashMap<>();

		// De ZeroGPT solo tomamos el porcentaje de IA
		resultadosIA.put("ZeroGPT", r1.getData().getIs_gpt_generated());

		// De Grok tomamos su puntaje de detección
		resultadosIA.put("Grok", r2.getPorcentajeIA());

		// De Gemini tomamos su puntaje de detección
		resultadosIA.put("Gemini", r3.getPorcentajeIA());

		return resultadosIA;
	}

	// ===============================
	// 🖼 IMAGEN
	// ===============================
	private ResultadoAnalisisDTO analizarImagen(MultipartFile archivo) throws Exception {

		// 🔜 aquí luego irá OCR
		HuggingFaceResponseDTO r = huggingFace.analizarArchivo(archivo.getBytes());

		return new ResultadoAnalisisDTO(r.getScore(), "HuggingFace");
	}

	// ===============================
	// 🔍 HELPERS
	// ===============================
	private boolean esDocumento(String tipo) {
		return tipo.startsWith("text") || tipo.contains("pdf") || tipo.contains("word");
	}
}