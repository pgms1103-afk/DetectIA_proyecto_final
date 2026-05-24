import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

/**
 * Interfaz que representa la respuesta del servidor al iniciar sesión.
 */
export interface AuthResponse {
  /** Token JWT generado por el servidor. */
  token: string;
  /** Rol del usuario autenticado (ej. 'ADMIN', 'USER'). */
  role: string;
}

/**
 * Servicio de autenticación de DetectIA.
 *
 * Gestiona el inicio de sesión, registro, almacenamiento del token JWT
 * y cierre de sesión del usuario.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  /** URL base del endpoint público de autenticación. */
  private urlServidor = 'http://localhost:8080/public';

  /**
   * @param http Cliente HTTP para realizar peticiones al backend.
   */
  constructor(private http: HttpClient) {}

  /**
   * Inicia sesión con las credenciales del usuario.
   * Si el servidor responde con un token, lo guarda en localStorage
   * junto con el rol y el nombre de usuario.
   * @param usuario Nombre de usuario.
   * @param contrasena Contraseña del usuario.
   * @returns Observable con la respuesta de autenticación (token y rol).
   */
  iniciarSesion(usuario: string, contrasena: string): Observable<AuthResponse> {
    const body = { nombreUsuario: usuario, contrasena };
    return this.http.post<AuthResponse>(`${this.urlServidor}/login`, body).pipe(
      tap(respuesta => {
        if (respuesta?.token) {
          this.guardarToken(respuesta.token);
          if (respuesta?.role) {
            localStorage.setItem('rol_diario', respuesta.role);
          }
          localStorage.setItem('usuario_diario', usuario);
        }
      })
    );
  }

  /**
   * Registra un nuevo usuario en el sistema.
   * @param nombreUsuario Nombre de usuario del nuevo usuario.
   * @param correo Correo electrónico del nuevo usuario.
   * @param contrasena Contraseña del nuevo usuario.
   * @returns Observable con la respuesta en texto plano del servidor.
   */
  registrarUsuario(
    nombreUsuario: string,
    correo: string,
    contrasena: string
  ): Observable<string> {

    const body = { nombreUsuario, correo, contrasena };

    return this.http.post(`${this.urlServidor}/registrarusuario`, body, {
      responseType: 'text'
    });
  }

  /**
   * Guarda el token JWT en localStorage.
   * @param token Token JWT a guardar.
   */
  guardarToken(token: string) {
    localStorage.setItem('token_diario', token);
  }

  /**
   * Obtiene el token JWT almacenado en localStorage.
   * @returns El token JWT o null si no existe.
   */
  obtenerToken(): string | null {
    return localStorage.getItem('token_diario');
  }

  /**
   * Cierra la sesión del usuario eliminando el token, rol y nombre
   * de usuario del localStorage.
   */
  cerrarSesion() {
    localStorage.removeItem('token_diario');
    localStorage.removeItem('rol_diario');
    localStorage.removeItem('usuario_diario');
  }

  /**
   * Alias de {@link cerrarSesion}. Cierra la sesión del usuario.
   */
  logout() {
    this.cerrarSesion();
  }
}
