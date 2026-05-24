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
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.util.AESUtil;

/**
 * Servicio de negocio para la gestión de usuarios de la plataforma DetectIA.
 * <p>
 * Implementa las operaciones CRUD definidas en
 * {@link CRUDoperation}&lt;{@link UsuarioDTO}&gt; con cifrado AES sobre el
 * campo correo electrónico, validaciones de nombre, contraseña y formato de
 * correo, y codificación BCrypt para las contraseñas en base de datos.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see CRUDoperation
 * @see co.edu.unbosque.detectia.entity.Usuario
 * @see co.edu.unbosque.detectia.repository.UsuarioRepository
 * @see co.edu.unbosque.detectia.util.AESUtil
 */
@Service
public class UsuarioService implements CRUDoperation<UsuarioDTO> {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ArchivoRepository archivoRepo;

	public UsuarioService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int create(UsuarioDTO data) {

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
			throw new CorreoInvalidoException(
					"Correo invalido. Debe tener formato ejemplo@correo.com y solo letras minusculas");
		}

		if (usuarioRepo.existsByCorreo(AESUtil.encrypt(data.getCorreo()))) {
			throw new CorreoInvalidoException("Correo ya existente");
		}

		Usuario entity = new Usuario();

		entity.setNombreUsuario(data.getNombreUsuario());
		entity.setCorreo(AESUtil.encrypt(data.getCorreo()));

		if (findUsernameAlreadyTaken(entity.getNombreUsuario())) {
			return 1;
		} else {
			entity.setContrasena(passwordEncoder.encode(data.getContrasena()));

			if (data.getRole() != null) {
				entity.setRole(data.getRole());
			}

			usuarioRepo.save(entity); 
			return 0;
		}

	}

	@Override
	public List<UsuarioDTO> getAll() {
		List<Usuario> entityList = (List<Usuario>) usuarioRepo.findAll();
		List<UsuarioDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			long totalArchivos = archivoRepo.countByUsuarioId(entity.getId());
			UsuarioDTO dto = mapper.map(entity, UsuarioDTO.class);
			dto.setCorreo(AESUtil.decrypt(dto.getCorreo()));
			dto.setTotalArchivos(totalArchivos);
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Recupera el perfil del usuario actualmente autenticado, descifrando su correo
	 * electrónico y calculando el total de archivos subidos.
	 *
	 * @param nombre nombre de usuario del usuario autenticado
	 * @return {@link UsuarioDTO} con los datos del usuario, incluyendo correo
	 *         descifrado y total de archivos
	 * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
	 *         si no existe ningún usuario con ese nombre
	 */
	public UsuarioDTO getLoginUser(String nombre) {
	    Optional<Usuario> entity = usuarioRepo.findByNombreUsuario(nombre);
	    if (entity.isEmpty()) {
	        throw new UsernameNotFoundException("Usuario no encontrado");
	    }
	    Usuario usuario = entity.get();
	    UsuarioDTO dto = mapper.map(usuario, UsuarioDTO.class);
	    long totalArchivos = archivoRepo.countByUsuarioId(usuario.getId());
	    dto.setTotalArchivos(totalArchivos);
	    try { dto.setCorreo(AESUtil.decrypt(dto.getCorreo())); } catch (Exception ignored) {}
	    return dto;
	}
	
	@Override
	public int delateById(Long id) {
		if (!usuarioRepo.existsById(id)) {
			throw new IdExistException("El id no existe");
		}

		usuarioRepo.delete(usuarioRepo.findById(id).get());
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
			throw new CorreoInvalidoException(
					"Correo invalido. Debe tener formato ejemplo@correo.com y solo letras minusculas");
		}

		if (usuarioRepo.existsByCorreo(AESUtil.encrypt(data.getCorreo()))) {
			throw new CorreoInvalidoException("Correo ya existente");
		}

		Usuario temp = usuarioRepo.findById(id)
				.orElseThrow(() -> new IdExistException("El id no existe"));
		temp.setNombreUsuario(data.getNombreUsuario());
		temp.setCorreo(AESUtil.encrypt(data.getCorreo()));
		temp.setContrasena(passwordEncoder.encode(data.getContrasena()));
		if (data.getRole() != null) {
			temp.setRole(data.getRole());
		}
		usuarioRepo.save(temp);
		return 0;
	}

	

	/**
	 * Actualiza la contraseña del usuario identificado por su correo electrónico
	 * (en texto plano). La contraseña nueva es validada y codificada con BCrypt
	 * antes de persistirla.
	 *
	 * @param correo         correo electrónico en texto plano del usuario
	 * @param nuevaContrasena nueva contraseña (mínimo 8 caracteres, al menos una
	 *                        mayúscula y un número)
	 * @return {@code 0} si la actualización fue exitosa
	 * @throws co.edu.unbosque.detectia.exception.PasswordNotValidException si la
	 *         contraseña no cumple los requisitos mínimos
	 * @throws co.edu.unbosque.detectia.exception.IdExistException si no existe
	 *         ningún usuario con el correo dado
	 */
	public int updateLoginPassword(String correo, String nuevaContrasena) {

		if (nuevaContrasena == null ||nuevaContrasena.isBlank()) {
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
		
		Optional<Usuario> encontrado = usuarioRepo.findByCorreo(AESUtil.encrypt(correo));

		if (encontrado.isEmpty()) {
			throw new IdExistException("No existe ningún usuario con el correo: " + correo);
		}

		Usuario temp = encontrado.get();
		temp.setContrasena(passwordEncoder.encode(nuevaContrasena));
		usuarioRepo.save(temp);
		return 0;

	}

	/**
	 * Verifica si un nombre de usuario ya está en uso.
	 *
	 * @param newUser Usuario con el nombre de usuario a verificar
	 * @return true si el nombre de usuario ya está en uso, false en caso contrario
	 */
	public boolean findUsernameAlreadyTaken(String newUser) {
		Optional<Usuario> found = usuarioRepo.findByNombreUsuario(newUser);
		if (found.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepo.findByNombreUsuario(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
	}


}
