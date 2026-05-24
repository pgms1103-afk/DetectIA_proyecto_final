import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

/**
 * Suite de pruebas unitarias para {@link AuthService}.
 *
 * Verifica el inicio de sesión, registro, almacenamiento del token
 * y cierre de sesión del usuario.
 */
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const urlBase = 'https://gpcueb.org/detectiaIA/public';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('iniciarSesion() debería hacer POST a /login con las credenciales correctas', () => {
    const mockRespuesta = { token: 'jwt-token-123', role: 'USER' };

    service.iniciarSesion('martin', 'pass123').subscribe(resp => {
      expect(resp).toEqual(mockRespuesta);
    });

    const req = httpMock.expectOne(`${urlBase}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ nombreUsuario: 'martin', contrasena: 'pass123' });
    req.flush(mockRespuesta);
  });

  it('iniciarSesion() debería guardar el token en localStorage', () => {
    const mockRespuesta = { token: 'jwt-token-123', role: 'USER' };

    service.iniciarSesion('martin', 'pass123').subscribe();

    const req = httpMock.expectOne(`${urlBase}/login`);
    req.flush(mockRespuesta);

    expect(localStorage.getItem('token_diario')).toBe('jwt-token-123');
  });

  it('iniciarSesion() debería guardar el rol en localStorage', () => {
    const mockRespuesta = { token: 'jwt-token-123', role: 'ADMIN' };

    service.iniciarSesion('admin', 'pass123').subscribe();

    const req = httpMock.expectOne(`${urlBase}/login`);
    req.flush(mockRespuesta);

    expect(localStorage.getItem('rol_diario')).toBe('ADMIN');
  });

  it('iniciarSesion() debería guardar el nombre de usuario en localStorage', () => {
    const mockRespuesta = { token: 'jwt-token-123', role: 'USER' };

    service.iniciarSesion('martin', 'pass123').subscribe();

    const req = httpMock.expectOne(`${urlBase}/login`);
    req.flush(mockRespuesta);

    expect(localStorage.getItem('usuario_diario')).toBe('martin');
  });

  it('registrarUsuario() debería hacer POST a /registrarusuario con los datos correctos', () => {
    service.registrarUsuario('nuevoUser', 'nuevo@test.com', 'pass123').subscribe(resp => {
      expect(resp).toBe('Usuario registrado');
    });

    const req = httpMock.expectOne(`${urlBase}/registrarusuario`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      nombreUsuario: 'nuevoUser',
      correo: 'nuevo@test.com',
      contrasena: 'pass123'
    });
    req.flush('Usuario registrado');
  });

  it('guardarToken() debería guardar el token en localStorage', () => {
    service.guardarToken('mi-token');
    expect(localStorage.getItem('token_diario')).toBe('mi-token');
  });

  it('obtenerToken() debería retornar el token guardado', () => {
    localStorage.setItem('token_diario', 'token-guardado');
    expect(service.obtenerToken()).toBe('token-guardado');
  });

  it('obtenerToken() debería retornar null si no hay token', () => {
    expect(service.obtenerToken()).toBeNull();
  });

  it('cerrarSesion() debería eliminar token, rol y usuario del localStorage', () => {
    localStorage.setItem('token_diario', 'token');
    localStorage.setItem('rol_diario', 'USER');
    localStorage.setItem('usuario_diario', 'martin');

    service.cerrarSesion();

    expect(localStorage.getItem('token_diario')).toBeNull();
    expect(localStorage.getItem('rol_diario')).toBeNull();
    expect(localStorage.getItem('usuario_diario')).toBeNull();
  });

  it('logout() debería funcionar igual que cerrarSesion()', () => {
    localStorage.setItem('token_diario', 'token');
    localStorage.setItem('rol_diario', 'USER');
    localStorage.setItem('usuario_diario', 'martin');

    service.logout();

    expect(localStorage.getItem('token_diario')).toBeNull();
    expect(localStorage.getItem('rol_diario')).toBeNull();
    expect(localStorage.getItem('usuario_diario')).toBeNull();
  });
});
