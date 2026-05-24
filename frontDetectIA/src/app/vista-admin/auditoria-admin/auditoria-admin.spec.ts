import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuditoriaAdmin } from './auditoria-admin';
import { AuditoriaService } from '../../services/auditoria.service';
import { of, throwError } from 'rxjs';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

describe('AuditoriaAdmin', () => {
  let component: AuditoriaAdmin;
  let fixture: ComponentFixture<AuditoriaAdmin>;
  let auditoriaServiceSpy: jasmine.SpyObj<AuditoriaService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('AuditoriaService', [
      'getTodos', 'getPorCorreo', 'getPorAccion', 'getPorModulo', 'getPorExitoso'
    ]);

    await TestBed.configureTestingModule({
      imports: [AuditoriaAdmin],
      providers: [{ provide: AuditoriaService, useValue: spy }]
    }).compileComponents();

    auditoriaServiceSpy = TestBed.inject(AuditoriaService) as jasmine.SpyObj<AuditoriaService>;
    fixture = TestBed.createComponent(AuditoriaAdmin);
    component = fixture.componentInstance;
  });

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería llamar a cargarAuditorias al inicializar (ngOnInit)', () => {
    auditoriaServiceSpy.getTodos.and.returnValue(of([]));
    component.ngOnInit();
    expect(auditoriaServiceSpy.getTodos).toHaveBeenCalled();
  });

  it('debería limpiar los filtros y cargar auditorías en limpiarFiltros()', () => {
    component.filtroCorreo = 'test@test.com';
    auditoriaServiceSpy.getTodos.and.returnValue(of([]));

    component.limpiarFiltros();

    expect(component.filtroCorreo).toBe('');
    expect(auditoriaServiceSpy.getTodos).toHaveBeenCalled();
  });

  it('debería filtrar por correo si el campo tiene valor', () => {
    component.filtroCorreo = 'admin@example.com';
    auditoriaServiceSpy.getPorCorreo.and.returnValue(of([]));

    component.buscar();

    expect(auditoriaServiceSpy.getPorCorreo).toHaveBeenCalledWith('admin@example.com');
  });

  it('debería obtener iniciales correctamente', () => {
    expect(component.obtenerIniciales('Juan Perez')).toBe('JP');
    expect(component.obtenerIniciales('Usuario')).toBe('U');
    expect(component.obtenerIniciales('')).toBe('?');
  });

  it('debería manejar errores en cargarAuditorias', () => {
    spyOn(console, 'error');
    auditoriaServiceSpy.getTodos.and.returnValue(throwError(() => new Error('Error')));

    component.cargarAuditorias();

    expect(console.error).toHaveBeenCalled();
  });
});
