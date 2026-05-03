package co.edu.unbosque.detectia.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.ZeroGPTResponseDTO;
import co.edu.unbosque.detectia.service.ZeroGPTService;

@RestController
@RequestMapping("/public/zerogpt")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class ZeroGptController {
	
	@Autowired
    private ZeroGPTService zeroGPTService;

    @PostMapping("/detectar")
    public ResponseEntity<ZeroGPTResponseDTO> detectar(@RequestParam String texto) throws IOException {
        ZeroGPTResponseDTO resultado = zeroGPTService.detectarIA(texto);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

}
