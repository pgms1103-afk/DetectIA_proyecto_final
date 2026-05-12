package co.edu.unbosque.detectia.dto;



import java.util.Objects;

import co.edu.unbosque.detectia.entity.Usuario.Role;



public class UsuarioDTO {
	
	private Long id;
	private String nombreUsuario;
	private String correo;
	private String contrasena;
	private Role role;
	
	public UsuarioDTO() {
		// TODO Auto-generated constructor stub
	}

	public UsuarioDTO(String nombreUsuario, String correo, String contrasena, Role role) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.correo = correo;
		this.contrasena = contrasena;
		this.role = role;
	}
	
	public UsuarioDTO(String nombreUsuario, String correo, Role role) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.correo = correo;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UsuarioDTO [id=" + id + ", nombreUsuario=" + nombreUsuario + ", correo=" + correo + ", contrasena="
				+ contrasena + ", role=" + role + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasena, correo, id, nombreUsuario, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioDTO other = (UsuarioDTO) obj;
		return Objects.equals(contrasena, other.contrasena) && Objects.equals(correo, other.correo) && id == other.id
				&& Objects.equals(nombreUsuario, other.nombreUsuario) && role == other.role;
	}

	

	
	

}
