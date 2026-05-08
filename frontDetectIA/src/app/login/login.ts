import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {

  modo: string = 'login';
  showLoginPass: boolean = false;
  showSignupPass: boolean = false;
  currentSlide = 0;


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
      desc: 'Comparacion entre diferentes Inteligencias Aritificiales para visualizar cual fue el porsentaje de detección.',
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
      desc: 'Accede a todos tus análisis anteriores, compara resultados y exporta reportes en un clic.',
      highlight: 'Ilimitado',
      highlightIcon: 'fa-infinity',
      color: '#a78bfa',
    },
  ];

  constructor(private router: Router) {
  }

  cambiarModo(m: string) {
    this.modo = m;
  }

  irAlSistema() {
    this.router.navigate(['/vista-usuario']);
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
}
