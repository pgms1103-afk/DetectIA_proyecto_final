import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP funcional que inyecta el token de autenticación en las peticiones salientes.
 * Si existe un token en la sesión, clona la petición original y añade la cabecera 'Authorization' con el esquema 'Bearer'.
 * * @type {HttpInterceptorFn}
 * @param {HttpRequest<unknown>} req - La petición HTTP saliente que será interceptada.
 * @param {HttpHandlerFn} next - La función manejadora que pasa la petición al siguiente interceptor o al backend.
 * @returns {Observable<HttpEvent<unknown>>} Un Observable con el evento de respuesta HTTP.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.obtenerToken();

  if (token) {
    const peticionClonada = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(peticionClonada);
  }

  return next(req);
};
