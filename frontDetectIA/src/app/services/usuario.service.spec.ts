import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UsuarioService } from './usuario.service';
import { UsuarioModel } from '../models/usuario.model';
import { Role } from '../models/role.enum';

/**
 * Suite de pruebas unitarias para {@link UsuarioService}.
 *
 * Verifica que cada método realice la petición HTTP correcta al backend
 * con los parámetros y datos esperados.
 */
describe('UsuarioService', () => {
  let service: UsuarioService;
  let httpMock: HttpTestingController;

  const urlBase = 'http://localhost:8080/admin';

  const mockUsuario: UsuarioModel = {
    id: 1,
    nombreUsuario: 'martin',
    correo: 'martin@test.com',
    contrasena: 'pass123',
    role: Role.USER,
    totalArchivos: 5
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UsuarioService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UsuarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('postCrearUsuario() debería hacer POST a /crearusuario con los datos del usuario', () => {
    service.postCrearUsuario(mockUsuario).subscribe(resp => {
      expect(resp).toBe('Usuario creado correctamente');
    });

    const req = httpMock.expectOne(`${urlBase}/crearusuario`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockUsuario);
    req.flush('Usuario creado correctamente');
  });

  it('getMostrarUsuarios() debería hacer GET a /mostrarusuarios', () => {
    service.getMostrarUsuarios().subscribe(resp => {
      expect(resp).toEqual([mockUsuario]);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrarusuarios`);
    expect(req.request.method).toBe('GET');
    req.flush([mockUsuario]);
  });

  it('putActualizarUsuario() debería hacer PUT a /actualizarusuarios con id y datos correctos', () => {
    service.putActualizarUsuario(1, mockUsuario).subscribe(resp => {
      expect(resp).toBe('Usuario actualizado correctamente');
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/actualizarusuarios` &&
      r.params.get('id') === '1'
    );
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockUsuario);
    req.flush('Usuario actualizado correctamente');
  });

  it('deleteUsuarios() debería hacer DELETE a /eliminarusuarios con el id correcto', () => {
    service.deleteUsuarios(1).subscribe(resp => {
      expect(resp).toBe('Usuario eliminado correctamente');
    });

    const req = httpMock.expectOne(r =>
      r.url === `${urlBase}/eliminarusuarios` &&
      r.params.get('id') === '1'
    );
    expect(req.request.method).toBe('DELETE');
    req.flush('Usuario eliminado correctamente');
  });

  it('getDatosUsuarioRegistrado() debería hacer GET a /private/user/misdatos', () => {
    service.getDatosUsuarioRegistrado().subscribe(resp => {
      expect(resp).toEqual(mockUsuario);
    });

    const req = httpMock.expectOne('http://localhost:8080/private/user/misdatos');
    expect(req.request.method).toBe('GET');
    req.flush(mockUsuario);
  });

  it('listausuarios$ debería emitir cuando se llama a listaUsuarios.next()', () => {
    const lista = [mockUsuario];
    service.listausuarios$.subscribe(resp => {
      expect(resp).toEqual(lista);
    });
    service.listaUsuarios.next(lista);
  });
});
