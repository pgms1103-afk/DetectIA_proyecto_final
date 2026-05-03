package co.edu.unbosque.detectia.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private String nombreUsuario;
	private String correo;
	private String contrasena;
	private String rol;
	
	public Usuario() {
		// TODO Auto-generated constructor stub
	}

	public Usuario(String nombreUsuario, String correo, String contrasena, String rol) {
		this.nombreUsuario = nombreUsuario;
		this.correo = correo;
		this.contrasena = contrasena;
		this.rol = rol;
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

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombreUsuario + ", correo=" + correo + ", contrasena=" + contrasena
				+ ", rol=" + rol + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasena, correo, id, nombreUsuario, rol);
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
		return Objects.equals(contrasena, other.contrasena) && Objects.equals(correo, other.correo)
				&& Objects.equals(id, other.id) && Objects.equals(nombreUsuario, other.nombreUsuario)
				&& Objects.equals(rol, other.rol);
	}


	
	
	
}
