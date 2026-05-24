import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ArchivoService } from './archivo.service';
import { ArchivoModel } from '../models/archivo.model';
import { AnalisisModel } from '../models/analisis.model';

/**
 * Suite de pruebas unitarias para {@link ArchivoService}.
 *
 * Verifica que cada método HTTP realice la petición correcta al backend
 * con los parámetros esperados.
 */
describe('ArchivoService', () => {
  let service: ArchivoService;
  let httpMock: HttpTestingController;

  const urlBase = 'https://gpcueb.org/detectiaIA/private/archivo';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ArchivoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ArchivoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('postAnalizarArchivo() debería hacer POST a /analizar con FormData', () => {
    const mockRespuesta: AnalisisModel = {
      porcentajeFinal: 85,
      veredicto: 'PROBABLE IA',
      iaRecomendada: 'Winston',
      fechaAnalisis: '2026-05-23',
      archivoId: 1,
      nombreArchivo: 'test.mp3',
      resultadosIAs: []
    };
    const archivo = new File(['contenido'], 'test.mp3', { type: 'audio/mp3' });

    service.postAnalizarArchivo('mi análisis', archivo).subscribe(resp => {
      expect(resp).toEqual(mockRespuesta);
    });

    const req = httpMock.expectOne(`${urlBase}/analizar`);
    expect(req.request.method).toBe('POST');
    req.flush(mockRespuesta);
  });

  it('postAnalizarUrl() debería hacer POST a /analizarimagenurl con params', () => {
    const mockRespuesta: AnalisisModel = {
      porcentajeFinal: 85,
      veredicto: 'PROBABLE IA',
      iaRecomendada: 'Winston',
      fechaAnalisis: '2026-05-23',
      archivoId: 1,
      nombreArchivo: 'test.mp3',
      resultadosIAs: []
    };
    service.postAnalizarUrl('mi análisis', 'https://ejemplo.com/imagen.jpg').subscribe(resp => {
      expect(resp).toEqual(mockRespuesta);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/analizarimagenurl` &&
      r.params.get('nombre') === 'mi análisis' &&
      r.params.get('url') === 'https://ejemplo.com/imagen.jpg'
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockRespuesta);
  });

  it('postAnalizarTexto() debería hacer POST a /analizartexto con body de texto', () => {
    const mockRespuesta: AnalisisModel = {
      porcentajeFinal: 85,
      veredicto: 'PROBABLE IA',
      iaRecomendada: 'Winston',
      fechaAnalisis: '2026-05-23',
      archivoId: 1,
      nombreArchivo: 'test.mp3',
      resultadosIAs: []
    };
    service.postAnalizarTexto('mi texto', 'Este es el texto a analizar').subscribe(resp => {
      expect(resp).toEqual(mockRespuesta);
    });

    const req = httpMock.expectOne(`${urlBase}/analizartexto`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toBe('application/x-www-form-urlencoded');
    req.flush(mockRespuesta);
  });

  it('getMisArchivos() debería hacer GET a /mis-archivos', () => {
    const mockArchivos: ArchivoModel[] = [
      {
        id: 1,
        nombre: 'archivo1',
        rutaAlmacenamiento: 'test.mp3',
        fechaSubida: '',
        usuarioId: 0,
        username: '',
      },
    ];

    service.getMisArchivos().subscribe(resp => {
      expect(resp.body).toEqual(mockArchivos);
    });

    const req = httpMock.expectOne(`${urlBase}/mis-archivos`);
    expect(req.request.method).toBe('GET');
    req.flush(mockArchivos);
  });

  it('deleteArchivos() debería hacer DELETE a /eliminar con el id correcto', () => {
    service.deleteArchivos(5).subscribe(resp => {
      expect(resp).toBe('Eliminado correctamente');
    });

    const req = httpMock.expectOne(`${urlBase}/eliminar?id=5`);
    expect(req.request.method).toBe('DELETE');
    req.flush('Eliminado correctamente');
  });

  it('getBuscarArchivosPorNombre() debería hacer GET a /buscarpornombre con el nombre', () => {
    const mockArchivos: ArchivoModel[] = [
      {
        id: 2,
        nombre: 'cancion',
        rutaAlmacenamiento: 'cancion.mp3',
        fechaSubida: '',
        usuarioId: 0,
        username: '',
      },
    ];

    service.getBuscarArchivosPorNombre('cancion').subscribe(resp => {
      expect(resp.body).toEqual(mockArchivos);
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/buscarpornombre` &&
      r.params.get('nombreArchivo') === 'cancion'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockArchivos);
  });

  it('getAllArchivos() debería hacer GET a /admin/archivos', () => {
    const mockArchivos: ArchivoModel[] = [
      {
        id: 3,
        nombre: 'imagen',
        rutaAlmacenamiento: 'imagen.jpg',
        fechaSubida: '',
        usuarioId: 0,
        username: '',
      },
    ];

    service.getAllArchivos().subscribe(resp => {
      expect(resp).toEqual(mockArchivos);
    });

    const req = httpMock.expectOne('https://gpcueb.org/detectiaIA/admin/archivos');
    expect(req.request.method).toBe('GET');
    req.flush(mockArchivos);
  });

  it('putEditarNombre() debería hacer PUT a /editarnombre con id y nombre', () => {
    service.putEditarNombre(1, 'nuevo nombre').subscribe(resp => {
      expect(resp).toBe('Nombre actualizado');
    });

    const req = httpMock.expectOne(`${urlBase}/editarnombre?id=1&nombre=nuevo nombre`);
    expect(req.request.method).toBe('PUT');
    req.flush('Nombre actualizado');
  });
});
