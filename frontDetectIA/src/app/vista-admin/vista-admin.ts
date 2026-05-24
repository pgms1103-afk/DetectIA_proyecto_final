import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarAdmin } from './sidebar-admin/sidebar-admin';
import { TopbarAdmin } from './topbar-admin/topbar-admin';
import { DashboardAdmin } from './dashboard-admin/dashboard-admin';
import { GestionUsuarios } from './gestion-usuarios/gestion-usuarios';
import { AuditoriaAdmin } from './auditoria-admin/auditoria-admin';

/**
 * @component VistaAdmin
 * @description Componente contenedor principal del área administrativa.
 * Gestiona el layout global (Topbar, Sidebar) y controla dinámicamente el contenido
 * principal mediante la variable `vistaActual`.
 */
@Component({
  selector: 'app-vista-admin',
  standalone: true,
  imports: [CommonModule, SidebarAdmin, DashboardAdmin, TopbarAdmin, GestionUsuarios, AuditoriaAdmin],
  templateUrl: './vista-admin.html',
  styleUrl: './vista-admin.css'
})
export class VistaAdmin {

  /** * Define la sección actualmente renderizada en el cuerpo principal.
   * Por defecto se inicializa en 'dashboard'.
   */
  vistaActual: 'dashboard' | 'usuarios' | 'auditoria' = 'dashboard';

  /**
   * @method cambiarVista
   * @param vista El nombre de la vista a activar.
   * Actualiza el estado local para conmutar dinámicamente el componente central.
   */
  cambiarVista(vista: 'dashboard' | 'usuarios' | 'auditoria') {
    this.vistaActual = vista;
  }
}
