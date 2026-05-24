import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TopbarAdmin } from './topbar-admin';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { Role } from '../../models/role.enum';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

describe('TopbarAdmin', () => {
  let component: TopbarAdmin;
  let fixture: ComponentFixture<TopbarAdmin>;
  let usuarioServiceSpy: jasmine.SpyObj<UsuarioService>;
  let toastrSpy: jasmine.SpyObj<ToastrService>;

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UsuarioService', ['getDatosUsuarioRegistrado']);
    const tSpy = jasmine.createSpyObj('ToastrService', ['error']);

    await TestBed.configureTestingModule({
      imports: [TopbarAdmin],
      providers: [
        { provide: UsuarioService, useValue: userSpy },
        { provide: ToastrService, useValue: tSpy }
      ]
    }).compileComponents();

    usuarioServiceSpy = TestBed.inject(UsuarioService) as jasmine.SpyObj<UsuarioService>;
    toastrSpy = TestBed.inject(ToastrService) as jasmine.SpyObj<ToastrService>;

    fixture = TestBed.createComponent(TopbarAdmin);
    component = fixture.componentInstance;
  });

  it('debería cargar los datos del usuario al iniciar', () => {
    // Asegúrate de incluir 'role' para cumplir con la interfaz UsuarioModel
    const mockUser = {
      nombreUsuario: 'Admin',
      correo: 'admin@test.com',
      totalArchivos: 5,
      role: Role.ADMIN // Usa el enum real, no un string
    };

    usuarioServiceSpy.getDatosUsuarioRegistrado.and.returnValue(of(mockUser));

    component.ngOnInit();

    expect(component.user.name).toBe('Admin');
    expect(component.user.files).toBe(5);
  });

  it('debería mostrar un toastr error al fallar la carga de datos', () => {
    usuarioServiceSpy.getDatosUsuarioRegistrado.and.returnValue(throwError(() => ({ error: 'Fallo total' })));

    component.mostrarMisDatos();

    expect(toastrSpy.error).toHaveBeenCalledWith('Fallo total', 'Error');
  });

  it('debería emitir menuToggle al llamar abrirMenu', () => {
    spyOn(component.menuToggle, 'emit');
    component.abrirMenu();
    expect(component.menuToggle.emit).toHaveBeenCalled();
  });
});
