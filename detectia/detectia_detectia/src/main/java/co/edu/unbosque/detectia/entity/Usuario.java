package co.edu.unbosque.detectia.entity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	@Column(unique=true)
	private String nombreUsuario;
	private String correo;
	private String contrasena;
	private long totalArchivos;
	@Enumerated(EnumType.STRING)
	private Role role;
	/**
	 * Indica si la cuenta del usuario no ha expirado.
	 */
	private boolean accountNonExpired;

	/**
	 * Indica si la cuenta del usuario no está bloqueada.
	 */
	private boolean accountNonLocked;

	/**
	 * Indica si las credenciales del usuario no han expirado.
	 */
	private boolean credentialsNonExpired;

	/**
	 * Indica si la cuenta del usuario está habilitada.
	 */
	private boolean enabled;
	
	// Usuario.java
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Archivo> archivos = new ArrayList<>(); 
	
	public Usuario() {
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
		this.role = Role.USER;
	}

	
	
	public Usuario(String nombreUsuario, String correo, String contrasena, Role role, long totalArchivos) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.correo = correo;
		this.contrasena = contrasena;
		this.role = role;
		this.totalArchivos = totalArchivos;
	}

	


	public Usuario(String nombreUsuario, String correo, Role role) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.correo = correo;
		this.role = role;
	}




	public enum Role {
		/** Usuario regular con permisos básicos */
		USER, 
		/** Administrador con permisos completos */
		ADMIN
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}



	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public Role getRole() {
		return role;
	}



	public void setRole(Role role) {
		this.role = role;
	}



	


	public long getTotalArchivos() {
		return totalArchivos;
	}



	public void setTotalArchivos(long totalArchivos) {
		this.totalArchivos = totalArchivos;
	}





	@Override
	public int hashCode() {
		return Objects.hash(accountNonExpired, accountNonLocked, archivos, contrasena, correo, credentialsNonExpired,
				enabled, id, nombreUsuario, role, totalArchivos);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return accountNonExpired == other.accountNonExpired && accountNonLocked == other.accountNonLocked
				&& Objects.equals(archivos, other.archivos) && Objects.equals(contrasena, other.contrasena)
				&& Objects.equals(correo, other.correo) && credentialsNonExpired == other.credentialsNonExpired
				&& enabled == other.enabled && Objects.equals(id, other.id)
				&& Objects.equals(nombreUsuario, other.nombreUsuario) && role == other.role
				&& totalArchivos == other.totalArchivos;
	}



	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombreUsuario=" + nombreUsuario + ", correo=" + correo + ", contrasena="
				+ contrasena + ", totalArchivos=" + totalArchivos + ", role=" + role + ", accountNonExpired="
				+ accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
				+ credentialsNonExpired + ", enabled=" + enabled + ", archivos=" + archivos + "]";
	}



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Verifica si la cuenta del usuario no está bloqueada.
	 * Implementación del método de la interfaz UserDetails.
	 * 
	 * @return true si la cuenta no está bloqueada, false en caso contrario
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Verifica si las credenciales del usuario no han expirado.
	 * Implementación del método de la interfaz UserDetails.
	 * 
	 * @return true si las credenciales no han expirado, false en caso contrario
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Verifica si la cuenta del usuario está habilitada.
	 * Implementación del método de la interfaz UserDetails.
	 * 
	 * @return true si la cuenta está habilitada, false en caso contrario
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}



	@Override
	public String getPassword() {
		return contrasena;
	
	}

	@Override
	public String getUsername() {
		return nombreUsuario;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}



	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}



	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}



	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
		

	
	


	
	
	
}
