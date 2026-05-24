import { Component, Output, EventEmitter, HostBinding } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

/**
 * @component SidebarAdmin
 * @description Componente de navegación lateral para el panel de administración.
 * Gestiona el cambio de vistas mediante eventos, la navegación de usuario y el cierre de sesión.
 */
@Component({
  selector: 'app-sidebar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.css'
})
export class SidebarAdmin {

  /** Evento emitido al seleccionar una sección principal del dashboard */
  @Output() readonly vistaSeleccionada = new EventEmitter<'dashboard' | 'usuarios' | 'auditoria'>();

  /** Estado de colapso del menú lateral (versión escritorio) */
  isCollapsed = false;

  /** Estado de visibilidad del menú móvil */
  mobileOpen = false;

  @HostBinding('class.sidebar-cerrada') get sidebarCerrada() {
    return !this.mobileOpen;
  }

  constructor(private authService: AuthService, private router: Router) {}

  /** Alterna el estado de colapso del menú lateral */
  toggleSidebar() { this.isCollapsed = !this.isCollapsed; }

  /** Alterna la visibilidad del menú en dispositivos móviles */
  toggleMobileMenu() { this.mobileOpen = !this.mobileOpen; }

  /**
   * Emite la vista seleccionada y cierra el menú móvil si estaba abierto.
   * @param vista El identificador de la vista a navegar.
   */
  seleccionar(vista: 'dashboard' | 'usuarios' | 'auditoria') {
    this.vistaSeleccionada.emit(vista);
    if (this.mobileOpen) this.toggleMobileMenu();
  }

  /** Navega hacia la ruta de gestión de usuario */
  irAVistaUsuario() {
    this.router.navigate(['/usuario']);
  }

  /** Invalida la sesión actual y redirige al login */
  cerrarSesion() {
    this.authService.cerrarSesion();
    window.location.href = '/login';
  }
}
