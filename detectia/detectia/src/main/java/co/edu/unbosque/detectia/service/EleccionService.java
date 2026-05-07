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

		if (esDocumento(tipo)) {
			// DELEGAMOS la responsabilidad al método especializado
			return analizarTexto(archivo);
		} else if (tipo.startsWith("image")) {
			// DELEGAMOS a la lógica de imagen
			return analizarImagen(archivo);
		}

		return new HashMap<>();
	}


	// ===============================
		// 📄 TEXTO (Ahora centraliza la lógica)
		// ===============================
		private Map<String, Double> analizarTexto(MultipartFile archivo) throws Exception {
			String texto = extractor.extraerTexto(archivo);

			System.out.println("TEXTO EXTRAIDO: [" + texto + "]");
			System.out.println("LONGITUD: " + texto.length());

			if (texto == null || texto.isBlank()) {
				return new HashMap<>();
			}

			Map<String, Double> resultadosIA = new HashMap<>();
			
			// Invocación a las APIs de texto
			resultadosIA.put("ZeroGPT", zeroGPT.detectarIA(texto).getData().getIs_gpt_generated());
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
			
			// Invocación a la API de visión
			HuggingFaceResponseDTO r = huggingFace.analizarArchivo(archivo.getBytes());
			resultadosIA.put("HuggingFace", r.getScore());

			return resultadosIA;
		}

	// ===============================
	// 🔍 HELPERS
	// ===============================
	private boolean esDocumento(String tipo) {
		return tipo.startsWith("text") || tipo.contains("pdf") || tipo.contains("word");
	}
}