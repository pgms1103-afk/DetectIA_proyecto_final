import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';
import { of } from 'rxjs';

describe('authInterceptor', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let nextFn: jasmine.Spy<HttpHandlerFn>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['obtenerToken']);
    nextFn = jasmine.createSpy('next').and.returnValue(of({} as HttpEvent<unknown>));

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService }
      ]
    });
  });

  it('debería agregar el header Authorization si hay token', () => {
    authService.obtenerToken.and.returnValue('mi-token-123');

    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => authInterceptor(req, nextFn));

    const reqCapturada: HttpRequest<unknown> = nextFn.calls.mostRecent().args[0] as HttpRequest<unknown>;
    expect(reqCapturada.headers.get('Authorization')).toBe('Bearer mi-token-123');
  });

  it('debería pasar la petición sin modificar si no hay token', () => {
    authService.obtenerToken.and.returnValue(null);

    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => authInterceptor(req, nextFn));

    const reqCapturada: HttpRequest<unknown> = nextFn.calls.mostRecent().args[0] as HttpRequest<unknown>;
    expect(reqCapturada.headers.get('Authorization')).toBeNull();
  });

  it('debería llamar a next con la petición clonada cuando hay token', () => {
    authService.obtenerToken.and.returnValue('token-abc');

    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => authInterceptor(req, nextFn));

    expect(nextFn).toHaveBeenCalledTimes(1);
  });

  it('debería llamar a next cuando no hay token', () => {
    authService.obtenerToken.and.returnValue(null);

    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => authInterceptor(req, nextFn));

    expect(nextFn).toHaveBeenCalledTimes(1);
  });
});
