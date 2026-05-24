import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from './login';
import { AuthService } from '../services/auth.service';

/**
 * Suite de pruebas unitarias para {@link Login}.
 *
 * Verifica el flujo de inicio de sesión, registro de usuario, navegación por
 * roles y la navegación del carrusel de presentación.
 */
describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'iniciarSesion',
      'registrarUsuario',
    ]);

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideRouter([])
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('debería crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debería iniciar con modo "login" por defecto', () => {
    expect(component.modo).toBe('login');
  });

  it('cambiarModo("signup") debería cambiar el modo y limpiar mensajes', () => {
    component.mensajeError = 'error previo';
    component.mensajeExito = 'exito previo';
    component.cambiarModo('signup');
    expect(component.modo).toBe('signup');
    expect(component.mensajeError).toBe('');
    expect(component.mensajeExito).toBe('');
  });

  it('login() debería mostrar error si los campos están vacíos', () => {
    component.loginNombreUsuario = '';
    component.loginContrasena = '';
    component.login();
    expect(component.mensajeError).toBe('Debe ingresar usuario y contraseña');
    expect(authServiceSpy.iniciarSesion).not.toHaveBeenCalled();
  });

  it('login() debería navegar a /admin si el rol es ADMIN', () => {
    authServiceSpy.iniciarSesion.and.returnValue(
      of({ token: 'jwt', role: 'ADMIN' })
    );
    spyOn(router, 'navigate');
    component.loginNombreUsuario = 'admin';
    component.loginContrasena = 'pass';
    component.login();
    expect(router.navigate).toHaveBeenCalledWith(['/admin']);
  });

  it('login() debería navegar a /usuario si el rol es USER', () => {
    authServiceSpy.iniciarSesion.and.returnValue(
      of({ token: 'jwt', role: 'USER' })
    );
    spyOn(router, 'navigate');
    component.loginNombreUsuario = 'usuario';
    component.loginContrasena = 'pass';
    component.login();
    expect(router.navigate).toHaveBeenCalledWith(['/usuario']);
  });

  it('login() debería mostrar mensajeError si el servidor devuelve error string', () => {
    authServiceSpy.iniciarSesion.and.returnValue(
      throwError(() => ({ error: 'Credenciales inválidas' }))
    );
    component.loginNombreUsuario = 'usuario';
    component.loginContrasena = 'pass';
    component.login();
    expect(component.mensajeError).toBe('Credenciales inválidas');
  });

  it('registrar() debería mostrar error si algún campo está vacío', () => {
    component.registroNombreUsuario = 'user';
    component.registroCorreo = '';
    component.registroContrasena = 'pass';
    component.registrar();
    expect(component.mensajeError).toBe('Debe completar todos los campos.');
    expect(authServiceSpy.registrarUsuario).not.toHaveBeenCalled();
  });

  it('registrar() debería llamar a authService.registrarUsuario con los datos correctos', () => {
    authServiceSpy.registrarUsuario.and.returnValue(of('OK'));
    component.registroNombreUsuario = 'nuevoUser';
    component.registroCorreo = 'nuevo@test.com';
    component.registroContrasena = 'pass123';
    component.registrar();
    expect(authServiceSpy.registrarUsuario).toHaveBeenCalledWith(
      'nuevoUser',
      'nuevo@test.com',
      'pass123'
    );
  });

  it('registrar() exitoso debería limpiar los campos y mostrar mensajeExito', () => {
    authServiceSpy.registrarUsuario.and.returnValue(of('OK'));
    component.registroNombreUsuario = 'nuevoUser';
    component.registroCorreo = 'nuevo@test.com';
    component.registroContrasena = 'pass123';
    component.registrar();
    expect(component.mensajeExito).toBe('Registrado correctamente, puede ingresar.');
    expect(component.registroNombreUsuario).toBe('');
    expect(component.registroCorreo).toBe('');
    expect(component.registroContrasena).toBe('');
  });

  it('registrar() debería mostrar mensajeError si el servidor falla', () => {
    authServiceSpy.registrarUsuario.and.returnValue(
      throwError(() => ({ error: 'El usuario ya existe' }))
    );
    component.registroNombreUsuario = 'user';
    component.registroCorreo = 'u@c.com';
    component.registroContrasena = 'pass';
    component.registrar();
    expect(component.mensajeError).toBe('El usuario ya existe');
  });

  it('nextSlide() debería avanzar al siguiente slide', () => {
    component.currentSlide = 0;
    component.nextSlide();
    expect(component.currentSlide).toBe(1);
  });

  it('nextSlide() debería volver al primer slide al llegar al final', () => {
    component.currentSlide = component.slides.length - 1;
    component.nextSlide();
    expect(component.currentSlide).toBe(0);
  });

  it('prevSlide() debería retroceder al slide anterior', () => {
    component.currentSlide = 2;
    component.prevSlide();
    expect(component.currentSlide).toBe(1);
  });

  it('prevSlide() desde el primer slide debería ir al último', () => {
    component.currentSlide = 0;
    component.prevSlide();
    expect(component.currentSlide).toBe(component.slides.length - 1);
  });

  it('goToSlide(2) debería establecer currentSlide en 2', () => {
    component.goToSlide(2);
    expect(component.currentSlide).toBe(2);
  });
});
