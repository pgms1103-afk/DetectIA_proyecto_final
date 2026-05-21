package co.edu.unbosque.detectia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UsuarioService implements CRUDoperation<UsuarioDTO> {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditoriaService auditoriaSer;

    public UsuarioService() {
    }

    @Override
    public int create(UsuarioDTO data) {
        return create(data, null);
    }

   
    public int create(UsuarioDTO data, HttpServletRequest request) {

        String ip = request != null ? request.getRemoteAddr() : "0.0.0.0";
        String navegador = request != null ? request.getHeader("User-Agent") : "desconocido";

        if (data.getNombreUsuario() == null || data.getNombreUsuario().isBlank()) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: nombre vacío",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new NombreInvalidoException("El nombre no puede estar vacío");
        }
        if (data.getNombreUsuario().contains("  ")) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: espacios dobles en nombre",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new NombreInvalidoException("El nombre no puede contener espacios dobles");
        }
        if (!data.getNombreUsuario().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: nombre con caracteres inválidos",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new NombreInvalidoException("El nombre solo debe contener letras y espacios");
        }
        String[] palabras = data.getNombreUsuario().trim().split("\\s+");
        if (palabras.length < 2) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: nombre sin apellido",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new NombreInvalidoException("El nombre debe tener Nombre y Apellido");
        }
        if (data.getContrasena() == null || data.getContrasena().isBlank()) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: contraseña vacía",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new PasswordNotValidException("La contrasena no puede estar vacia");
        }
        if (data.getContrasena().length() < 8) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: contraseña menor a 8 caracteres",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new PasswordNotValidException("La contrasena debe tener minimo 8 caracteres");
        }
        if (!data.getContrasena().matches(".*[A-Z].*")) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: contraseña sin mayúscula",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new PasswordNotValidException("La contrasena debe tener al menos una letra mayuscula");
        }
        if (!data.getContrasena().matches(".*[0-9].*")) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: contraseña sin número",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new PasswordNotValidException("La contrasena debe tener al menos un numero");
        }
        if (!data.getCorreo().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: formato de correo inválido",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new CorreoInvalidoException("Correo invalido. Debe tener formato ejemplo@correo.com y solo letras minusculas");
        }
        if (usuarioRepo.existsByCorreo(data.getCorreo())) {
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", "USER", "Intento fallido: correo ya existente",
                    ip, navegador, null, null, "no disponible", null, false);
            throw new CorreoInvalidoException("Correo ya existente");
        }

        Usuario entity = new Usuario();
        entity.setNombreUsuario(data.getNombreUsuario());
        entity.setCorreo(data.getCorreo());

        if (findUsernameAlreadyTaken(entity.getNombreUsuario())) {
            return 1;
        } else {
            entity.setContrasena(passwordEncoder.encode(data.getContrasena()));
            if (data.getRole() != null) {
                entity.setRole(data.getRole());
            }
            usuarioRepo.save(entity);

            String modulo = (data.getRole() != null) ? data.getRole().name() : "USER";
            auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                    "CREAR_USUARIO", modulo, "Usuario creado exitosamente con correo: " + data.getCorreo(),
                    ip, navegador, null, null, "no disponible", null, true);
            return 0;
        }
    }

    @Override
    public List<UsuarioDTO> getAll() {
        List<Usuario> entityList = (List<Usuario>) usuarioRepo.findAll();
        List<UsuarioDTO> dtoList = new ArrayList<>();
        entityList.forEach((entity) -> {
            UsuarioDTO dto = mapper.map(entity, UsuarioDTO.class);
            dtoList.add(dto);
        });
        return dtoList;
    }

    @Override
    public int delateById(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new IdExistException("El id no existe");
        }
        Usuario usuario = usuarioRepo.findById(id).get();
        String modulo = usuario.getRole() != null ? usuario.getRole().name() : "USER";
        usuarioRepo.delete(usuario);

        auditoriaSer.registrarAuditoria(usuario.getCorreo(), usuario.getNombreUsuario(),
                "ELIMINAR_USUARIO", modulo, "Usuario eliminado con id: " + id,
                "0.0.0.0", "desconocido", null, null, "no disponible", null, true);
        return 0;
    }

    @Override
    public int updateById(Long id, UsuarioDTO data) {
        if (!usuarioRepo.existsById(id)) {
            throw new IdExistException("El id no existe");
        }
        if (data.getNombreUsuario() == null || data.getNombreUsuario().isBlank()) {
            throw new NombreInvalidoException("El nombre no puede estar vacío");
        }
        if (data.getNombreUsuario().contains("  ")) {
            throw new NombreInvalidoException("El nombre no puede contener espacios dobles");
        }
        if (!data.getNombreUsuario().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
            throw new NombreInvalidoException("El nombre solo debe contener letras y espacios");
        }
        String[] palabras = data.getNombreUsuario().trim().split("\\s+");
        if (palabras.length < 2) {
            throw new NombreInvalidoException("El nombre debe tener Nombre y Apellido");
        }
        if (data.getContrasena() == null || data.getContrasena().isBlank()) {
            throw new PasswordNotValidException("La contrasena no puede estar vacia");
        }
        if (data.getContrasena().length() < 8) {
            throw new PasswordNotValidException("La contrasena debe tener minimo 8 caracteres");
        }
        if (!data.getContrasena().matches(".*[A-Z].*")) {
            throw new PasswordNotValidException("La contrasena debe tener al menos una letra mayuscula");
        }
        if (!data.getContrasena().matches(".*[0-9].*")) {
            throw new PasswordNotValidException("La contrasena debe tener al menos un numero");
        }
        if (!data.getCorreo().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            throw new CorreoInvalidoException("Correo invalido. Debe tener formato ejemplo@correo.com y solo letras minusculas");
        }
        if (usuarioRepo.existsByCorreo(data.getCorreo())) {
            throw new CorreoInvalidoException("Correo ya existente");
        }

        Optional<Usuario> encontrado = usuarioRepo.findById(id);
        Usuario temp = encontrado.get();
        temp.setNombreUsuario(data.getNombreUsuario());
        temp.setCorreo(data.getCorreo());
        temp.setContrasena(passwordEncoder.encode(data.getContrasena()));
        if (data.getRole() != null) {
            temp.setRole(data.getRole());
        }
        usuarioRepo.save(temp);

        String modulo = (data.getRole() != null) ? data.getRole().name() : "USER";
        auditoriaSer.registrarAuditoria(data.getCorreo(), data.getNombreUsuario(),
                "ACTUALIZAR_USUARIO", modulo, "Usuario actualizado con id: " + id,
                "0.0.0.0", "desconocido", null, null, "no disponible", null, true);
        return 0;
    }

    public UsuarioDTO getLoginUser(String nombre) {
        Optional<Usuario> entity = usuarioRepo.findByNombreUsuario(nombre);
        if (entity.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return mapper.map(entity.get(), UsuarioDTO.class);
    }

    public int updateLoginPassword(String correo, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            throw new PasswordNotValidException("La contrasena no puede estar vacia");
        }
        if (nuevaContrasena.length() < 8) {
            throw new PasswordNotValidException("La contrasena debe tener minimo 8 caracteres");
        }
        if (!nuevaContrasena.matches(".*[A-Z].*")) {
            throw new PasswordNotValidException("La contrasena debe tener al menos una letra mayuscula");
        }
        if (!nuevaContrasena.matches(".*[0-9].*")) {
            throw new PasswordNotValidException("La contrasena debe tener al menos un numero");
        }

        Optional<Usuario> encontrado = usuarioRepo.findByCorreo(correo);
        if (encontrado.isEmpty()) {
            throw new IdExistException("No existe ningún usuario con el correo: " + correo);
        }

        Usuario temp = encontrado.get();
        String modulo = temp.getRole() != null ? temp.getRole().name() : "USER";
        temp.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepo.save(temp);

        auditoriaSer.registrarAuditoria(correo, temp.getNombreUsuario(),
                "ACTUALIZAR_CONTRASENA", modulo, "Contraseña actualizada para: " + correo,
                "0.0.0.0", "desconocido", null, null, "no disponible", null, true);
        return 0;
    }

    public boolean findUsernameAlreadyTaken(String newUser) {
        Optional<Usuario> found = usuarioRepo.findByNombreUsuario(newUser);
        return found.isPresent();
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepo.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}