package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.AuditoriaDTO;
import co.edu.unbosque.detectia.service.AuditoriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/auditoria")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaLogSer;

    @GetMapping("/todos")
    public ResponseEntity<List<AuditoriaDTO>> obtenerTodos() {
        List<AuditoriaDTO> lista = auditoriaLogSer.getAll();
        if (lista.isEmpty()) {
            return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/porcorreo")
    public ResponseEntity<List<AuditoriaDTO>> obtenerPorCorreo(@RequestParam String correo) {
        List<AuditoriaDTO> lista = auditoriaLogSer.getByCorreo(correo);
        if (lista.isEmpty()) {
            return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/pornombre")
    public ResponseEntity<List<AuditoriaDTO>> obtenerPorNombre(@RequestParam String nombre) {
        List<AuditoriaDTO> lista = auditoriaLogSer.getByNombre(nombre);
        if (lista.isEmpty()) {
            return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/pormodulo")
    public ResponseEntity<List<AuditoriaDTO>> obtenerPorModulo(@RequestParam String modulo) {
        List<AuditoriaDTO> lista = auditoriaLogSer.getByModulo(modulo);
        if (lista.isEmpty()) {
            return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/porexitoso")
    public ResponseEntity<List<AuditoriaDTO>> obtenerPorExitoso(@RequestParam boolean exitoso) {
        List<AuditoriaDTO> lista = auditoriaLogSer.getByExitoso(exitoso);
        if (lista.isEmpty()) {
            return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
}