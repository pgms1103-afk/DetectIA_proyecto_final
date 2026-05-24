import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { adminGuard, usuarioGuard } from './role.guard';

describe('Role Guards', () => {
  let router: jasmine.SpyObj<Router>;

  const mockRoute = {} as ActivatedRouteSnapshot;
  const mockState = { url: '/' } as RouterStateSnapshot;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router }
      ]
    });
    localStorage.clear();
  });

  describe('adminGuard', () => {
    it('debería permitir acceso si el rol es ROLE_ADMIN', () => {
      localStorage.setItem('rol_diario', 'ROLE_ADMIN');
      const resultado = TestBed.runInInjectionContext(() =>
        adminGuard(mockRoute, mockState)
      );
      expect(resultado).toBeTrue();
    });

    it('debería redirigir al login si no es admin', () => {
      localStorage.setItem('rol_diario', 'USER');
      const resultado = TestBed.runInInjectionContext(() =>
        adminGuard(mockRoute, mockState)
      );
      expect(resultado).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('debería redirigir al login si no hay rol', () => {
      const resultado = TestBed.runInInjectionContext(() =>
        adminGuard(mockRoute, mockState)
      );
      expect(resultado).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('usuarioGuard', () => {
    it('debería permitir acceso si el rol es USER', () => {
      localStorage.setItem('rol_diario', 'USER');
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeTrue();
    });

    it('debería permitir acceso si el rol es ROLE_USER', () => {
      localStorage.setItem('rol_diario', 'ROLE_USER');
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeTrue();
    });

    it('debería permitir acceso si el rol es ADMIN', () => {
      localStorage.setItem('rol_diario', 'ADMIN');
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeTrue();
    });

    it('debería permitir acceso si el rol es USUARIO', () => {
      localStorage.setItem('rol_diario', 'USUARIO');
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeTrue();
    });

    it('debería redirigir al login si no hay rol', () => {
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('debería redirigir al login si el rol es inválido', () => {
      localStorage.setItem('rol_diario', 'INVALIDO');
      const resultado = TestBed.runInInjectionContext(() =>
        usuarioGuard(mockRoute, mockState)
      );
      expect(resultado).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });
});
