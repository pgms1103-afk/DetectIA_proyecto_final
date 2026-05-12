package co.edu.unbosque.detectia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Service
public class UsuarioService implements CRUDoperation<UsuarioDTO>, UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsuarioService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int create(UsuarioDTO data) {
	    // 1. Creamos la entidad vacía
	    Usuario entity = new Usuario();
	    
	    // 2. Pasamos los datos del DTO a la entidad (excepto el ID)
	    entity.setNombreUsuario(data.getNombreUsuario());
	    entity.setCorreo(data.getCorreo());
	    entity.setContrasena(passwordEncoder.encode(data.getContrasena()));
	    
	    // 3. Manejamos el rol (usando el del DTO o el predeterminado)
	    if (data.getRole() != null) {
	        entity.setRole(data.getRole());
	    }

	    // 4. Validación y Guardado
	    if (findUsernameAlreadyTaken(entity.getNombreUsuario())) {
	        return 1;
	    } else {
	        usuarioRepo.save(entity); // Aquí JPA generará el ID (1, 2, 3...)
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
		
		Optional<Usuario> encontrado = usuarioRepo.findById(id);
		
		if(encontrado.isPresent()) {
			UsuarioDTO dto = mapper.map(encontrado.get(), UsuarioDTO.class);
			Usuario entity = mapper.map(dto, Usuario.class);
			usuarioRepo.delete(entity);
			return 0;
		}
		
		return 1;
	}

	@Override
	public int updateById(Long id, UsuarioDTO data) {
		
		Optional<Usuario> encontrado = (Optional<Usuario>) usuarioRepo.findById(id);
		
		if(encontrado.isPresent()) {
			Usuario temp = encontrado.get();
			temp.setNombreUsuario(data.getNombreUsuario());
			temp.setCorreo(data.getCorreo());
			temp.setContrasena(passwordEncoder.encode(data.getContrasena())); 
			temp.setRole(data.getRole());
			usuarioRepo.save(temp);
			return 0;
		}
		
		return 1;
	}

	
	
	public UsuarioDTO getLoginUser(String correo) {
		Optional<Usuario> entity = usuarioRepo.findByCorreo(correo);
				if(entity.isEmpty()) {
					throw new UsernameNotFoundException("Usuario no encontrado");
				}
	    return mapper.map(entity, UsuarioDTO.class);
	}
	
	public int updateLoginPassword(String correo, String nuevaContrasena) {
		
		Optional<Usuario> encontrado = usuarioRepo.findByCorreo(correo);

		if(encontrado.isPresent()) {
			Usuario temp = encontrado.get();
			temp.setContrasena(passwordEncoder.encode(nuevaContrasena)); 
			usuarioRepo.save(temp);
			return 0;
		}
		
		return 1;
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

	  @Override
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	      return usuarioRepo.findByNombreUsuario(username)
	              .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
	  }
	
	
	
	

}
