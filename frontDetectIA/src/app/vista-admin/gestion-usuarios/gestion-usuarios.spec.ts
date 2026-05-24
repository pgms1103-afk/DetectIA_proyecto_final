import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GestionUsuarios } from './gestion-usuarios';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Role } from '../../models/role.enum';

describe('GestionUsuarios', () => {
  let component: GestionUsuarios;
  let fixture: ComponentFixture<GestionUsuarios>;

  // Mocks de los servicios
  let usuarioServiceSpy: jasmine.SpyObj<UsuarioService>;
  let toastrServiceSpy: jasmine.SpyObj<ToastrService>;

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UsuarioService', [
      'getMostrarUsuarios',
      'postCrearUsuario',
      'putActualizarUsuario',
      'deleteUsuarios'
    ]);
    const toastSpy = jasmine.createSpyObj('ToastrService', ['success', 'error', 'warning']);

    await TestBed.configureTestingModule({
      imports: [GestionUsuarios, FormsModule],
      providers: [
        { provide: UsuarioService, useValue: userSpy },
        { provide: ToastrService, useValue: toastSpy }
      ]
    }).compileComponents();

    usuarioServiceSpy = TestBed.inject(UsuarioService) as jasmine.SpyObj<UsuarioService>;
    toastrServiceSpy = TestBed.inject(ToastrService) as jasmine.SpyObj<ToastrService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GestionUsuarios);
    component = fixture.componentInstance;
    // Mock inicial para que ngOnInit no falle al cargar
    usuarioServiceSpy.getMostrarUsuarios.and.returnValue(of([]));
    fixture.detectChanges();
  });

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar usuarios en el ngOnInit', () => {
    expect(usuarioServiceSpy.getMostrarUsuarios).toHaveBeenCalled();
  });

  it('debería mostrar error si falla la carga de usuarios', () => {
    const errorMsg = 'Error de servidor';
    usuarioServiceSpy.getMostrarUsuarios.and.returnValue(throwError(() => ({ error: errorMsg })));

    component.cargarUsuarios();

    expect(toastrServiceSpy.error).toHaveBeenCalledWith(errorMsg, 'Error');
  });

  it('debería validar campos obligatorios antes de crear', () => {
    component.usuarioNuevo.nombreUsuario = ''; // Vacío
    component.crearOactualizar();
    expect(toastrServiceSpy.warning).toHaveBeenCalled();
  });

  it('debería llamar al servicio de creación y mostrar éxito', () => {
    component.modoModal = 'crear';
    component.usuarioNuevo = {
      nombreUsuario: 'test',
      correo: 'test@test.com',
      contrasena: '123',
      role: Role.USER,
      totalArchivos: 0
    };
    usuarioServiceSpy.postCrearUsuario.and.returnValue(of('usuario'));

    component.crearOactualizar();

    expect(usuarioServiceSpy.postCrearUsuario).toHaveBeenCalled();
    expect(toastrServiceSpy.success).toHaveBeenCalled();
  });
});
