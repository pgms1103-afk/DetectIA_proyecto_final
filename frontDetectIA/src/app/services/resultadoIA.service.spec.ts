import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ResultadoIAService } from './resultadoIA.service';
import { ResultadoIAModel } from '../models/resultadoIA.model';
import { AnalisisModel } from '../models/analisis.model';

/**
 * Suite de pruebas unitarias para {@link ResultadoIAService}.
 *
 * Verifica que cada método realice la petición HTTP correcta al backend
 * con los parámetros esperados.
 */
describe('ResultadoIAService', () => {
  let service: ResultadoIAService;
  let httpMock: HttpTestingController;

  const urlBase = 'https://gpcueb.org/detectiaIA/private/resultadoporia';

  const mockResultado: ResultadoIAModel = {
    id: 1,
    nombreIA: 'Winston',
    porcentajeIA: 85.5,
    fechaAnalisis: '2026-05-23',
    nombreArchivo: 'test.mp3'
  };

  const mockAnalisis: AnalisisModel = {
    id: 1,
    porcentajeFinal: 75,
    veredicto: 'PROBABLE IA',
    iaRecomendada: 'Winston',
    fechaAnalisis: '2026-05-23',
    archivoId: 1,
    nombreArchivo: 'test.mp3',
    resultadosIAs: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ResultadoIAService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ResultadoIAService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('getMostrarResultadosPorCorreo() debería hacer GET a /mostrarresultadosporcorreo', () => {
    service.getMostrarResultadosPorCorreo().subscribe(resp => {
      expect(resp.body).toEqual([mockResultado]);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrarresultadosporcorreo`);
    expect(req.request.method).toBe('GET');
    req.flush([mockResultado]);
  });

  it('getMostrarAnalisis() debería hacer GET a /mostraranalisis', () => {
    service.getMostrarAnalisis().subscribe(resp => {
      expect(resp.body).toEqual([mockAnalisis]);
    });

    const req = httpMock.expectOne(`${urlBase}/mostraranalisis`);
    expect(req.request.method).toBe('GET');
    req.flush([mockAnalisis]);
  });

  it('getMostrarResultadosPorId() debería hacer GET a /mostrarresultadoporarchivo con id correcto', () => {
    service.getMostrarResultadosPorId(5).subscribe(resp => {
      expect(resp).toEqual([mockResultado]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/mostrarresultadoporarchivo` &&
      r.params.get('id') === '5'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockResultado]);
  });

  it('getMostrarAnalisisPorId() debería hacer GET a /mostraranalisisporarchivo con id correcto', () => {
    service.getMostrarAnalisisPorId(3).subscribe(resp => {
      expect(resp).toEqual([mockAnalisis]);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/mostraranalisisporarchivo` &&
      r.params.get('id') === '3'
    );
    expect(req.request.method).toBe('GET');
    req.flush([mockAnalisis]);
  });

  it('getAllResultados() debería hacer GET a /admin/resultados', () => {
    service.getAllResultados().subscribe(resp => {
      expect(resp).toEqual([mockResultado]);
    });

    const req = httpMock.expectOne('https://gpcueb.org/detectiaIA/admin/resultados');
    expect(req.request.method).toBe('GET');
    req.flush([mockResultado]);
  });

  it('listaResultados$ debería emitir cuando se llama a listaResultados.next()', () => {
    const lista = [mockResultado];
    service.listaResultados$.subscribe(resp => {
      expect(resp).toEqual(lista);
    });
    service.listaResultados.next(lista);
  });
});
