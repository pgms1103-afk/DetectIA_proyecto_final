import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuditoriaService } from './auditoria.service';
import { AuditoriaLogModel } from '../models/auditoria.model';

/**
 * Suite de pruebas unitarias para {@link AuditoriaService}.
 *
 * Verifica que cada método realice la petición HTTP correcta al backend
 * con los parámetros esperados.
 */
describe('AuditoriaService', () => {
  let service: AuditoriaService;
  let httpMock: HttpTestingController;

  const urlBase = 'http://localhost:8080/admin/auditoria';

  const mockLog: AuditoriaLogModel = {
    id: 1,
    correo: 'juan@test.com',
    nombreUsuario: 'Juan Mendez',
    accion: 'CREAR_USUARIO',
    modulo: 'USUARIO',
    descripcion: 'Usuario creado correctamente',
    ip: '0.0.0.0',
    navegador: 'Chrome',
    datoAnterior: '',
    datoNuevo: 'Juan Mendez',
    recurso: 'usuario',
    idRecurso: '1',
    exitoso: true,
    fecha: '2026-05-21T20:13:42.3298'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuditoriaService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuditoriaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('getTodos() debería hacer GET a /todos', () => {
    service.getTodos().subscribe(resp => {
      expect(resp).toEqual([mockLog]);
    });

    const req = httpMock.expectOne(`${urlBase}/todos`);
    expect(req.request.method).toBe('GET');
    req.flush([mockLog]);
  });

  it('getPorCorreo() debería hacer GET a /porcorreo con el correo correcto', () => {
    service.getPorCorreo('juan@test.com').subscribe(resp => {
      expect(resp).toEqual([mockLog]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/porcorreo/` &&
      r.params.get('correo') === 'juan@test.com'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockLog]);
  });

  it('getPorAccion() debería hacer GET a /poraccion con la acción correcta', () => {
    service.getPorAccion('CREAR_USUARIO').subscribe(resp => {
      expect(resp).toEqual([mockLog]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/poraccion` &&
      r.params.get('accion') === 'CREAR_USUARIO'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockLog]);
  });

  it('getPorModulo() debería hacer GET a /pormodulo con el módulo correcto', () => {
    service.getPorModulo('USUARIO').subscribe(resp => {
      expect(resp).toEqual([mockLog]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/pormodulo` &&
      r.params.get('modulo') === 'USUARIO'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockLog]);
  });

  it('getPorExitoso(true) debería hacer GET a /porexitoso con exitoso=true', () => {
    service.getPorExitoso(true).subscribe(resp => {
      expect(resp).toEqual([mockLog]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/porexitoso` &&
      r.params.get('exitoso') === 'true'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockLog]);
  });

  it('getPorExitoso(false) debería hacer GET a /porexitoso con exitoso=false', () => {
    const mockLogFallido = { ...mockLog, exitoso: false };
    service.getPorExitoso(false).subscribe(resp => {
      expect(resp).toEqual([mockLogFallido]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/porexitoso` &&
      r.params.get('exitoso') === 'false'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockLogFallido]);
  });
});
