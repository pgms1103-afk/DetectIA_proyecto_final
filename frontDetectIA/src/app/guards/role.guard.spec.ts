import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';
import { adminGuard, usuarioGuard } from './role.guard';

describe('adminGuard', () => {
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router }
      ]
    });
    localStorage.clear();
  });

  it('debería permitir acceso si el rol es ADMIN', () => {
    localStorage.setItem('rol_diario', 'ADMIN');
    const resultado = TestBed.runInInjectionContext(() =>
      adminGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería permitir acceso si el rol es ROLE_ADMIN', () => {
    localStorage.setItem('rol_diario', 'ROLE_ADMIN');
    const resultado = TestBed.runInInjectionContext(() =>
      adminGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería redirigir al login si no es admin', () => {
    localStorage.setItem('rol_diario', 'USER');
    const resultado = TestBed.runInInjectionContext(() =>
      adminGuard({} as any, {} as any)
    );
    expect(resultado).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('debería redirigir al login si no hay rol', () => {
    const resultado = TestBed.runInInjectionContext(() =>
      adminGuard({} as any, {} as any)
    );
    expect(resultado).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});

describe('usuarioGuard', () => {
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router }
      ]
    });
    localStorage.clear();
  });

  it('debería permitir acceso si el rol es USER', () => {
    localStorage.setItem('rol_diario', 'USER');
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería permitir acceso si el rol es ROLE_USER', () => {
    localStorage.setItem('rol_diario', 'ROLE_USER');
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería permitir acceso si el rol es ADMIN', () => {
    localStorage.setItem('rol_diario', 'ADMIN');
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería permitir acceso si el rol es USUARIO', () => {
    localStorage.setItem('rol_diario', 'USUARIO');
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeTrue();
  });

  it('debería redirigir al login si no hay rol', () => {
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('debería redirigir al login si el rol es inválido', () => {
    localStorage.setItem('rol_diario', 'INVALIDO');
    const resultado = TestBed.runInInjectionContext(() =>
      usuarioGuard({} as any, {} as any)
    );
    expect(resultado).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
