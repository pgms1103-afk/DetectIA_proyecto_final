import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private urlServidor = 'http://localhost:8080/public';

  constructor(private http: HttpClient) {}

  iniciarSesion(usuario: string, contrasena: string): Observable<any> {
    const body = { username: usuario, password: contrasena };

    return this.http.post<any>(`${this.urlServidor}/login`, body).pipe(
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

  registrarUsuario(nombreUsuario: string, contrasena: string): Observable<any> {
    const body = { username: nombreUsuario, password: contrasena };

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
