import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

export interface AuthResponse {
  token: string;
  role: string;
}
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private urlServidor = 'http://localhost:8080/public';

  constructor(private http: HttpClient) {}

  iniciarSesion(usuario: string, contrasena: string): Observable<AuthResponse> {
    const body = { nombreUsuario: usuario, contrasena: contrasena };

    return this.http.post<AuthResponse>(`${this.urlServidor}/login`, body).pipe(
      tap(respuesta => {
        if (respuesta && respuesta.token) {
          this.guardarToken(respuesta.token);
          if (respuesta.role) {
            localStorage.setItem('rol_diario', respuesta.role);
          }
          localStorage.setItem('usuario_diario', usuario);
        }
      })
    );
  }

  registrarUsuario(nombreUsuario: string, correo: string, contrasena: string): Observable<any> {
    const body = { nombreUsuario: nombreUsuario, correo: correo, contrasena: contrasena };

    return this.http.post(`${this.urlServidor}/registrarusuario`, body, {
      responseType: 'text'
    });
  }

  guardarToken(token: string) {
    localStorage.setItem('token_diario', token);
  }


  obtenerToken(): string | null {
    return localStorage.getItem('token_diario');
  }

  cerrarSesion() {
    localStorage.removeItem('token_diario');
    localStorage.removeItem('rol_diario');
    localStorage.removeItem('usuario_diario');
  }
}
