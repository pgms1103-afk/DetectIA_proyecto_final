package co.edu.unbosque.detectia.dto;

import java.util.Objects;

public class UsuarioDTO {
	
	private long id;
	private String correo;
	private String contrasena;
	private String rol;
	
	public UsuarioDTO() {
		// TODO Auto-generated constructor stub
	}

	public UsuarioDTO(String correo, String contrasena, String rol) {
		super();
		this.correo = correo;
		this.contrasena = contrasena;
		this.rol = rol;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	@Override
	public String toString() {
		return "UsuarioDTO [id=" + id + ", correo=" + correo + ", contrasena=" + contrasena + ", rol=" + rol + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasena, correo, id, rol);
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
				&& Objects.equals(rol, other.rol);
	}
	
	

}
