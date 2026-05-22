package co.edu.unbosque.detectia.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria_log")
public class AuditoriaLog {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo")
    private String correo;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @Column(name = "accion")
    private String accion;

    @Column(name = "modulo")
    private String modulo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "ip")
    private String ip;

    @Column(name = "navegador", length = 300)
    private String navegador;

    @Column(name = "dato_anterior", length = 500)
    private String datoAnterior;

    @Column(name = "dato_nuevo", length = 500)
    private String datoNuevo;

    @Column(name = "recurso")
    private String recurso;

    @Column(name = "id_recurso")
    private String idRecurso;

    @Column(name = "exitoso")
    private boolean exitoso;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    public AuditoriaLog() {}

    public AuditoriaLog(String correo, String nombreUsuario, String accion, String modulo,
            String descripcion, String ip, String navegador, String datoAnterior,
            String datoNuevo, String recurso, String idRecurso, boolean exitoso) {
        this.correo = correo;
        this.nombreUsuario = nombreUsuario;
        this.accion = accion;
        this.modulo = modulo;
        this.descripcion = descripcion;
        this.ip = ip;
        this.navegador = navegador;
        this.datoAnterior = datoAnterior;
        this.datoNuevo = datoNuevo;
        this.recurso = recurso;
        this.idRecurso = idRecurso;
        this.exitoso = exitoso;
        this.fecha = LocalDateTime.now();
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

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNavegador() {
		return navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	public String getDatoAnterior() {
		return datoAnterior;
	}

	public void setDatoAnterior(String datoAnterior) {
		this.datoAnterior = datoAnterior;
	}

	public String getDatoNuevo() {
		return datoNuevo;
	}

	public void setDatoNuevo(String datoNuevo) {
		this.datoNuevo = datoNuevo;
	}

	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}

	public String getIdRecurso() {
		return idRecurso;
	}

	public void setIdRecurso(String idRecurso) {
		this.idRecurso = idRecurso;
	}

	public boolean isExitoso() {
		return exitoso;
	}

	public void setExitoso(boolean exitoso) {
		this.exitoso = exitoso;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
}
