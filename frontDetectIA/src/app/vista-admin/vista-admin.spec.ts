import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VistaAdmin } from './vista-admin';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations';

describe('VistaAdmin', () => {
  let component: VistaAdmin;
  let fixture: ComponentFixture<VistaAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaAdmin],
      providers: [
        provideToastr(),
        provideAnimations(),
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(VistaAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Verifica que vistaActual se inicialice en 'dashboard'.
   */
  it('debería inicializarse con vistaActual en "dashboard"', () => {
    expect(component.vistaActual).toBe('dashboard');
  });

  /**
   * Verifica que cambiarVista actualice correctamente vistaActual.
   */
  it('debería cambiar la vistaActual al ejecutar cambiarVista', () => {
    component.cambiarVista('usuarios');
    expect(component.vistaActual).toBe('usuarios');

    component.cambiarVista('auditoria');
    expect(component.vistaActual).toBe('auditoria');
  });
});
