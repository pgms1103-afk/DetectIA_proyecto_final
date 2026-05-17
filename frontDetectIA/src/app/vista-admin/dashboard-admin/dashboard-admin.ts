import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css'
})
export class DashboardAdmin {
  // Datos simulados para las nuevas tarjetas
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
}
