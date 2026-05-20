import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

declare var Chart: any;

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css'
})
export class DashboardAdmin implements AfterViewInit {

  metricas = [
    { titulo: 'Usuarios Totales', valor: '1,284', icono: 'fa-users', color: 'naranja' },
    { titulo: 'Análisis Realizados', valor: '45,602', icono: 'fa-magnifying-glass-chart', color: 'purpura' },
    { titulo: 'Uptime Sistema', valor: '99.9%', icono: 'fa-server', color: 'verde' },
    { titulo: 'Modelos Activos', valor: '8', icono: 'fa-microchip', color: 'azul' },
    { titulo: 'Capacidad API', valor: '95%', icono: 'fa-bolt', color: 'amarillo' },
    { titulo: 'Suscripciones Pro', valor: '312', icono: 'fa-gem', color: 'rojo' }
  ];

  ngAfterViewInit() {
    this.inicializarGraficasFreedom();
  }

  inicializarGraficasFreedom() {
    const ctxTrafico = document.getElementById('traficoChart');
    if (ctxTrafico) {
      new Chart(ctxTrafico, {
        type: 'line',
        data: {
          labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
          datasets: [{
            label: 'Peticiones API',
            data: [120, 190, 300, 250, 420, 150, 310],
            borderColor: '#6366f1', // Púrpura Admin
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            fill: true,
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#a1a1aa' } },
            x: { grid: { display: false }, ticks: { color: '#a1a1aa' } }
          }
        }
      });
    }

    const ctxStorage = document.getElementById('storageChart');
    if (ctxStorage) {
      new Chart(ctxStorage, {
        type: 'doughnut',
        data: {
          labels: ['Usado', 'Libre'],
          datasets: [{
            data: [75, 25],
            backgroundColor: [
              '#10b981',
              '#27272a'
            ],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '80%',
          plugins: { legend: { display: false } }
        }
      });
    }

    const ctxRadar = document.getElementById('performanceRadarChart');
    if (ctxRadar) {
      new Chart(ctxRadar, {
        type: 'radar',
        data: {
          labels: ['Velocidad', 'Precisión', 'Capacidad', 'Escalabilidad', 'Eficiencia'],
          datasets: [{
            label: 'Modelo GPT-4 v2',
            data: [85, 95, 80, 90, 88],
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.2)',
          }, {
            label: 'Claude 3 Opus',
            data: [90, 92, 85, 88, 92],
            borderColor: '#10b981',
            backgroundColor: 'rgba(16, 185, 129, 0.2)',
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            r: {
              angleLines: { color: 'rgba(255,255,255,0.1)' },
              grid: { color: 'rgba(255,255,255,0.1)' },
              pointLabels: { color: '#a1a1aa', font: { size: 10 } },
              ticks: { display: false }
            }
          }
        }
      });
    }

    const ctxApiUsage = document.getElementById('apiUsageChart');
    if (ctxApiUsage) {
      new Chart(ctxApiUsage, {
        type: 'line',
        data: {
          labels: Array.from({length: 24}, (_, i) => `${i}h`),
          datasets: [{
            data: Array.from({length: 24}, () => Math.floor(Math.random() * 100)),
            borderColor: '#fbbf24', // Amarillo
            backgroundColor: 'rgba(251, 191, 36, 0.1)',
            fill: true,
            tension: 0.1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#a1a1aa' } },
            x: { grid: { display: false }, ticks: { color: '#a1a1aa' }, display: false }
          }
        }
      });
    }
  }
}
