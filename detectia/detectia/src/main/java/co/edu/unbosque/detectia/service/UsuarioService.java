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
import co.edu.unbosque.detectia.entity.SecurityUser;
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
		Usuario entity = mapper.map(data, Usuario.class);
		entity.setId(null);
		entity.setContrasena(passwordEncoder.encode(data.getContrasena()));
		entity.setRol("USUARIO");
		usuarioRepo.save(entity);
		return 0;
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
			usuarioRepo.save(temp);
			return 0;
		}
		
		return 1;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> entity = usuarioRepo.findByCorreo(username);
		if(entity.isEmpty()) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		}
		return new SecurityUser(entity.get());
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
	
	
	
	
	

}
