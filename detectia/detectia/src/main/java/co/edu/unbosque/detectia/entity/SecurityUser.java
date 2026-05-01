package co.edu.unbosque.detectia.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class SecurityUser implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	
	public SecurityUser() {
		// TODO Auto-generated constructor stub
	}
	
	public SecurityUser(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //Se devuelven los roles y privilegios del usuario
		return Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol()));
	}

	@Override
	public String getPassword() {
		return usuario.getContrasena();
	}

	@Override
	public String getUsername() {
		return usuario.getCorreo();
	}

}
