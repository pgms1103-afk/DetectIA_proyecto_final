import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GestionUsuarios } from './gestion-usuarios';
import { UsuarioService } from '../../services/usuario.service';
import { of } from 'rxjs';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

describe('GestionUsuarios', () => {
  let component: GestionUsuarios;
  let fixture: ComponentFixture<GestionUsuarios>;
  let usuarioServiceSpy: jasmine.SpyObj<UsuarioService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('UsuarioService', [
      'getMostrarUsuarios', 'postCrearUsuario', 'putActualizarUsuario', 'deleteUsuarios'
    ]);

    await TestBed.configureTestingModule({
      imports: [GestionUsuarios],
      providers: [{ provide: UsuarioService, useValue: spy }]
    }).compileComponents();

    usuarioServiceSpy = TestBed.inject(UsuarioService) as jasmine.SpyObj<UsuarioService>;
    fixture = TestBed.createComponent(GestionUsuarios);
    component = fixture.componentInstance;
  });

  it('debería inicializar cargando los usuarios', () => {
    usuarioServiceSpy.getMostrarUsuarios.and.returnValue(of([]));
    component.ngOnInit();
    expect(usuarioServiceSpy.getMostrarUsuarios).toHaveBeenCalled();
  });

  it('debería abrir el modal en modo crear con campos limpios', () => {
    component.abrirModalCrear();
    expect(component.mostrarModal).toBeTrue();
    expect(component.modoModal).toBe('crear');
    expect(component.usuarioNuevo.nombreUsuario).toBe('');
  });

  it('no debería guardar si faltan campos obligatorios', () => {
    component.modoModal = 'crear';
    component.crearOactualizar();
    expect(component.mensajeError).toBe('Debe completar todos los campos obligatorios.');
  });

  it('debería llamar a eliminarUsuario y recargar lista', () => {
    usuarioServiceSpy.deleteUsuarios.and.returnValue(of('Usuario eliminado'));
    usuarioServiceSpy.getMostrarUsuarios.and.returnValue(of([]));

    component.eliminarUsuario({ id: 1 });

    expect(usuarioServiceSpy.deleteUsuarios).toHaveBeenCalledWith(1);
    expect(usuarioServiceSpy.getMostrarUsuarios).toHaveBeenCalled();
  });
});
