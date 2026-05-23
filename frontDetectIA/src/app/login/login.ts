import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  modo: string = 'login';
  showLoginPass: boolean = false;
  showSignupPass: boolean = false;
  currentSlide = 0;

  mensajeError = '';
  mensajeExito = '';

  registroNombreUsuario: string = '';
  registroContrasena: string = '';
  registroCorreo: string = '';

  loginNombreUsuario: string = '';
  loginContrasena: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

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

  cambiarModo(m: string) {
    this.modo = m;
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  irAlSistema() {
    this.router.navigate(['/admin']);
  }

  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.slides.length;
  }

  goToSlide(i: number) {
    this.currentSlide = i;
  }

  private static extraerError(err: unknown): string {
    const errorObj = err as { error?: string | { message?: string } };
    if (errorObj?.error) {
      if (typeof errorObj.error === 'string') return errorObj.error;
      if (errorObj.error.message) return errorObj.error.message;
    }
    return 'Ocurrió un error en el servidor.';
  }

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
        this.mensajeError =  Login.extraerError(err);
      }
    })
  }

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
        this.mensajeError =  Login.extraerError(err);
      }
    })
  }
}
