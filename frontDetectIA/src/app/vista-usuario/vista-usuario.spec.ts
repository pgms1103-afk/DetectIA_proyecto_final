import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { VistaUsuario } from './vista-usuario';
import { ArchivoService } from '../services/archivo.service';
import { ResultadoIAService } from '../services/resultadoIA.service';
import { UsuarioService } from '../services/usuario.service';
import { AuthService } from '../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { of, Subject } from 'rxjs';
import { ArchivoModel } from '../models/archivo.model';

/**
 * Suite de pruebas unitarias para {@link VistaUsuario}.
 *
 * Verifica la lógica de cambio de herramienta en la vista principal del usuario.
 */
declare global {
  interface Window {
    Chart: jasmine.Spy;
  }
}

describe('VistaUsuario', () => {
  let component: VistaUsuario;
  let fixture: ComponentFixture<VistaUsuario>;

  const toastrSpy = jasmine.createSpyObj('ToastrService', [
    'success',
    'error',
    'warning',
  ]);
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['cerrarSesion']);
  const usuarioServiceSpy = jasmine.createSpyObj('UsuarioService', [
    'getDatosUsuarioRegistrado',
  ]);
  const archivoServiceSpy = {
    getMisArchivos: jasmine.createSpy('getMisArchivos').and.returnValue(
      of({ body: [] })
    ),
    getBuscarArchivosPorNombre: jasmine.createSpy().and.returnValue(of({ body: [] })),
    putEditarNombre: jasmine.createSpy().and.returnValue(of('OK')),
    deleteArchivos: jasmine.createSpy().and.returnValue(of('OK')),
    analisisCompletado$: new Subject<void>(),
    archivoSeleccionado$: new Subject<ArchivoModel>(),
    postAnalizarArchivo: jasmine.createSpy().and.returnValue(of({})),
    postAnalizarUrl: jasmine.createSpy().and.returnValue(of({})),
    postAnalizarTexto: jasmine.createSpy().and.returnValue(of({})),
  };
  const resultadoServiceSpy = {
    getMostrarResultadosPorId: jasmine.createSpy().and.returnValue(of([])),
    getMostrarAnalisisPorId: jasmine.createSpy().and.returnValue(of([])),
  };

  beforeEach(async () => {
    usuarioServiceSpy.getDatosUsuarioRegistrado.and.returnValue(
      of({ nombreUsuario: 'user', correo: 'u@b.com', totalArchivos: 0 })
    );

    window.Chart = jasmine
      .createSpy('Chart')
      .and.callFake(() => ({
        update: () => undefined,
        destroy: () => undefined,
        data: { datasets: [{ data: [] }] }
      }));
    spyOn(document, 'getElementById').and.returnValue(null);

    await TestBed.configureTestingModule({
      imports: [VistaUsuario, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ToastrService, useValue: toastrSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: UsuarioService, useValue: usuarioServiceSpy },
        { provide: ArchivoService, useValue: archivoServiceSpy },
        { provide: ResultadoIAService, useValue: resultadoServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaUsuario);
    component = fixture.componentInstance;
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('herramientaActual debería ser "texto" por defecto', () => {
    expect(component.herramientaActual).toBe('texto');
  });

  it('cambiarHerramienta("imagen") debería actualizar herramientaActual a "imagen"', () => {
    component.cambiarHerramienta('imagen');
    expect(component.herramientaActual).toBe('imagen');
  });

  it('cambiarHerramienta("video") debería actualizar herramientaActual a "video"', () => {
    component.cambiarHerramienta('video');
    expect(component.herramientaActual).toBe('video');
  });

  it('cambiarHerramienta("audio") debería actualizar herramientaActual a "audio"', () => {
    component.cambiarHerramienta('audio');
    expect(component.herramientaActual).toBe('audio');
  });

  it('cambiarHerramienta("texto") debería restablecer herramientaActual a "texto"', () => {
    component.cambiarHerramienta('video');
    component.cambiarHerramienta('texto');
    expect(component.herramientaActual).toBe('texto');
  });
});
