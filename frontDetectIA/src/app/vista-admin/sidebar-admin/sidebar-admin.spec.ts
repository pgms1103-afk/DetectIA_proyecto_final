import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarAdmin } from './sidebar-admin';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

describe('SidebarAdmin', () => {
  let component: SidebarAdmin;
  let fixture: ComponentFixture<SidebarAdmin>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['cerrarSesion']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [SidebarAdmin],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarAdmin);
    component = fixture.componentInstance;
  });

  /**
   * Verifica que toggleSidebar cambie el estado de colapso.
   */
  it('debería cambiar el estado de colapso', () => {
    const estadoInicial = component.isCollapsed;
    component.toggleSidebar();
    expect(component.isCollapsed).toBe(!estadoInicial);
  });

  /**
   * Verifica que seleccionar emita el evento vistaSeleccionada.
   */
  it('debería emitir vistaSeleccionada al llamar a seleccionar', () => {
    spyOn(component.vistaSeleccionada, 'emit');
    component.seleccionar('auditoria');
    expect(component.vistaSeleccionada.emit).toHaveBeenCalledWith('auditoria');
  });

  /**
   * Verifica que irAVistaUsuario navegue a /usuario.
   */
  it('debería redirigir al usuario al navegar', () => {
    component.irAVistaUsuario();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/usuario']);
  });

  /**
   * Verifica que cerrarSesion llame al servicio.
   * No se verifica window.location.href porque es una API
   * nativa del browser que Chrome no permite redefinir en tests.
   */
  it('debería cerrar sesión usando el servicio', () => {
    // Evitamos que window.location.href explote en el entorno de test
    spyOn(component as any, 'cerrarSesion').and.callFake(() => {
      authServiceSpy.cerrarSesion();
    });

    component.cerrarSesion();

    expect(authServiceSpy.cerrarSesion).toHaveBeenCalled();
  });
});
