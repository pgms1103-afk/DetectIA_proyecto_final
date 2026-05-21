package co.edu.unbosque.detectia.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuarioCorreo;
    private String usuarioNombre;
    private String accion;
    private String modulo;

    @Column(length = 500)
    private String descripcion;

    private LocalDateTime fechaAccion;
    private String ip;
    private String navegador;
    private Double latitud;
    private Double longitud;

    @Column(length = 500)
    private String ubicacion;

    private String conversacionId;
    private boolean exitoso;

    public Auditoria() {
    }

    public Auditoria(String usuarioCorreo, String usuarioNombre, String accion, String modulo,
            String descripcion, LocalDateTime fechaAccion, String ip, String navegador,
            Double latitud, Double longitud, String ubicacion, String conversacionId, boolean exitoso) {
        this.usuarioCorreo = usuarioCorreo;
        this.usuarioNombre = usuarioNombre;
        this.accion = accion;
        this.modulo = modulo;
        this.descripcion = descripcion;
        this.fechaAccion = fechaAccion;
        this.ip = ip;
        this.navegador = navegador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.conversacionId = conversacionId;
        this.exitoso = exitoso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuarioCorreo() { return usuarioCorreo; }
    public void setUsuarioCorreo(String usuarioCorreo) { this.usuarioCorreo = usuarioCorreo; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getNavegador() { return navegador; }
    public void setNavegador(String navegador) { this.navegador = navegador; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getConversacionId() { return conversacionId; }
    public void setConversacionId(String conversacionId) { this.conversacionId = conversacionId; }

    public boolean isExitoso() { return exitoso; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }

    @Override
    public String toString() {
        return "AuditoriaLog [id=" + id + ", usuarioCorreo=" + usuarioCorreo + ", usuarioNombre="
                + usuarioNombre + ", accion=" + accion + ", modulo=" + modulo + ", descripcion="
                + descripcion + ", fechaAccion=" + fechaAccion + ", ip=" + ip + ", navegador="
                + navegador + ", latitud=" + latitud + ", longitud=" + longitud + ", ubicacion="
                + ubicacion + ", conversacionId=" + conversacionId + ", exitoso=" + exitoso + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioCorreo, usuarioNombre, accion, modulo, descripcion,
                fechaAccion, ip, navegador, latitud, longitud, ubicacion, conversacionId, exitoso);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Auditoria other = (Auditoria) obj;
        return Objects.equals(id, other.id);
    }
}
