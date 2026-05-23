import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarAdmin } from './sidebar-admin/sidebar-admin';
import { TopbarAdmin } from './topbar-admin/topbar-admin';
import { DashboardAdmin } from './dashboard-admin/dashboard-admin';
import { GestionUsuarios } from './gestion-usuarios/gestion-usuarios';
import { AuditoriaAdmin } from './auditoria-admin/auditoria-admin';

@Component({
  selector: 'app-vista-admin',
  standalone: true,
  imports: [CommonModule, SidebarAdmin, DashboardAdmin, TopbarAdmin, GestionUsuarios, AuditoriaAdmin],
  templateUrl: './vista-admin.html',
  styleUrl: './vista-admin.css'
})
export class VistaAdmin {
  vistaActual: 'dashboard' | 'usuarios' | 'auditoria' = 'dashboard';

  cambiarVista(vista: 'dashboard' | 'usuarios' | 'auditoria') {
    this.vistaActual = vista;
  }
}
