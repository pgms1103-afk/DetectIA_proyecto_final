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
    { titulo: 'Usuarios Totales', valor: '1,284', sub: '+12% esta semana', icono: 'fa-users', color: 'naranja' },
    { titulo: 'Análisis Realizados', valor: '45,602', sub: '+5.2k hoy', icono: 'fa-magnifying-glass-chart', color: 'purpura' },
    { titulo: 'Uptime Sistema', valor: '99.9%', sub: 'Estable', icono: 'fa-server', color: 'verde' },
    { titulo: 'Modelos Activos', valor: '8', sub: '2 en mantenimiento', icono: 'fa-microchip', color: 'azul' },
    { titulo: 'Peticiones API', valor: '128k', sub: 'Últimas 24h', icono: 'fa-bolt', color: 'amarillo' },
    { titulo: 'Almacenamiento', valor: '24.5 GB', sub: '68% de capacidad', icono: 'fa-database', color: 'rojo' }
  ];

  actividadReciente = [
    { evento: 'Detección crítica', user: 'Carlos Ruiz', hora: 'Hace 5 min', tipo: 'alert' },
    { evento: 'Nuevo registro', user: 'Ana Maria', hora: 'Hace 12 min', tipo: 'info' },
    { evento: 'Actualización modelo', user: 'Sistema', hora: 'Hace 1 hora', tipo: 'sys' }
  ];

  ngAfterViewInit() {
    this.inicializarGraficasAdmin();
  }

  inicializarGraficasAdmin() {
    // 1. Gráfica de Barras (Tráfico)
    const ctxTrafico = document.getElementById('traficoChart');
    if (ctxTrafico) {
      new Chart(ctxTrafico, {
        type: 'bar',
        data: {
          labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
          datasets: [{
            label: 'Peticiones API',
            data: [120, 190, 300, 250, 420, 150, 310],
            backgroundColor: '#6366f1',
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: {
              grid: { color: 'rgba(255,255,255,0.05)' },
              ticks: { color: '#a1a1aa' }
            },
            x: {
              grid: { display: false },
              ticks: { color: '#a1a1aa' }
            }
          }
        }
      });
    }

    // 2. Gráfica Circular (Detecciones)
    const ctxModelos = document.getElementById('estadoModelosChart');
    if (ctxModelos) {
      new Chart(ctxModelos, {
        type: 'doughnut',
        data: {
          labels: ['IA Detectada', 'Humano', 'Mixto'],
          datasets: [{
            data: [45, 40, 15],
            backgroundColor: [
              '#ef4444',
              '#10b981',
              '#f59e0b'
            ],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '75%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: '#a1a1aa', padding: 20, font: { size: 11 } }
            }
          }
        }
      });
    }
  }
}
