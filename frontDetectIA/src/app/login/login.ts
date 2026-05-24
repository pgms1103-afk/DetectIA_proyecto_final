import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * Componente principal de autenticación de DetectIA.
 *
 * Gestiona el inicio de sesión y registro de usuarios, la navegación por roles
 * (ADMIN → /admin, USER → /usuario) y el carrusel de presentación de características.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  /** Modo activo del formulario: 'login' o 'signup'. */
  modo: string = 'login';

  /** Controla la visibilidad de la contraseña en el formulario de login. */
  showLoginPass: boolean = false;

  /** Controla la visibilidad de la contraseña en el formulario de registro. */
  showSignupPass: boolean = false;

  /** Índice del slide actualmente visible en el carrusel. */
  currentSlide = 0;

  /** Mensaje de error que se muestra en el formulario. */
  mensajeError = '';

  /** Mensaje de éxito que se muestra tras una acción exitosa. */
  mensajeExito = '';

  /** Nombre de usuario ingresado en el formulario de registro. */
  registroNombreUsuario: string = '';

  /** Contraseña ingresada en el formulario de registro. */
  registroContrasena: string = '';

  /** Correo electrónico ingresado en el formulario de registro. */
  registroCorreo: string = '';

  /** Nombre de usuario ingresado en el formulario de login. */
  loginNombreUsuario: string = '';

  /** Contraseña ingresada en el formulario de login. */
  loginContrasena: string = '';

  /**
   * @param authService Servicio de autenticación para login y registro.
   * @param router Servicio de enrutamiento para navegación entre vistas.
   */
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  /** Lista de slides del carrusel de características de DetectIA. */
  slides = [
    {
      icon: 'fa-magnifying-glass-chart',
      tag: 'DETECCIÓN IA',
      title: 'Detecta texto generado por IA',
      desc: 'Analiza cualquier fragmento de texto y obtén una puntuación precisa de probabilidad de autoría artificial.',
      highlight: '98.4% precisión',
      highlightIcon: 'fa-bullseye',
      color: '#f59e0b',
    },
    {
      icon: 'fa-chart-pie',
      tag: 'COMPARACIÓN',
      title: "Comparacion entre IA'S",
      desc: 'Comparacion entre diferentes Inteligencias Aritificiales para visualizar cual fue el porcentaje de detección.',
      highlight: '8 inteligencias diferentes',
      highlightIcon: 'fa-layer-group',
      color: '#10b981',
    },
    {
      icon: 'fa-file-arrow-up',
      tag: 'FORMATOS',
      title: 'Sube cualquier archivo',
      desc: 'Con diferentes herramientas para analizar textos, videos, imagenes y audios,',
      highlight: '+4 formatos',
      highlightIcon: 'fa-file-lines',
      color: '#3b82f6',
    },
    {
      icon: 'fa-clock-rotate-left',
      tag: 'HISTORIAL',
      title: 'Guarda tu historial',
      desc: 'Accede a todos tus análisis anteriores, compara resultados y eliminalos cuando no los necesites.',
      highlight: 'Ilimitado',
      highlightIcon: 'fa-infinity',
      color: '#a78bfa',
    },
  ];

  /**
   * Cambia el modo del formulario entre 'login' y 'signup'.
   * Limpia los mensajes de error y éxito al cambiar.
   * @param m Modo destino: 'login' o 'signup'.
   */
  cambiarModo(m: string) {
    this.modo = m;
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  /** Navega directamente a la vista de administrador. */
  irAlSistema() {
    this.router.navigate(['/admin']);
  }

  /** Retrocede al slide anterior del carrusel, volviendo al último si está en el primero. */
  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
  }

  /** Avanza al siguiente slide del carrusel, volviendo al primero si está en el último. */
  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.slides.length;
  }

  /**
   * Navega directamente a un slide específico del carrusel.
   * @param i Índice del slide destino.
   */
  goToSlide(i: number) {
    this.currentSlide = i;
  }

  /**
   * Extrae el mensaje de error de una respuesta HTTP fallida.
   * @param err El error recibido del servidor.
   * @returns Mensaje de error legible para el usuario.
   */
  private static extraerError(err: unknown): string {
    const errorObj = err as { error?: string | { message?: string } };
    if (errorObj?.error) {
      if (typeof errorObj.error === 'string') return errorObj.error;
      if (errorObj.error.message) return errorObj.error.message;
    }
    return 'Ocurrió un error en el servidor.';
  }

  /**
   * Ejecuta el proceso de inicio de sesión.
   * Valida los campos, llama al servicio de autenticación y navega según el rol.
   * ADMIN → /admin, USER → /usuario.
   */
  login(){
    this.mensajeError = '';
    this.mensajeExito = '';

    if(!this.loginNombreUsuario || !this.loginContrasena){
      this.mensajeError = 'Debe ingresar usuario y contraseña'
      return;
    }
    this.authService.iniciarSesion(this.loginNombreUsuario, this.loginContrasena).subscribe({
      next: (res) => {
        if(res.role === 'ADMIN')  {
          this.router.navigate(['/admin']);
        }else{
          this.router.navigate(['/usuario']);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.mensajeError = Login.extraerError(err);
      }
    })
  }

  /**
   * Ejecuta el proceso de registro de un nuevo usuario.
   * Valida los campos, llama al servicio de registro y muestra el resultado.
   * En caso de éxito, redirige al modo login tras 2 segundos.
   */
  registrar(){
    this.mensajeError = '';
    this.mensajeExito = '';

    if(!this.registroNombreUsuario || !this.registroContrasena || !this.registroCorreo){
      this.mensajeError = 'Debe completar todos los campos.';
      return;
    }
    this.authService.registrarUsuario(this.registroNombreUsuario,
      this.registroCorreo,
      this.registroContrasena).subscribe({
      next: () => {
        this.mensajeExito = 'Registrado correctamente, puede ingresar.';
        this.registroNombreUsuario = '';
        this.registroCorreo = '';
        this.registroContrasena = '';
        setTimeout(() => {this.cambiarModo('login');}, 2000);
      },
      error: (err: HttpErrorResponse) => {
        this.mensajeError = Login.extraerError(err);
      }
    })
  }
}
