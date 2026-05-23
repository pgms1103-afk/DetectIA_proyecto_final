import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sidebar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.css'
})
export class SidebarAdmin {
  @Output() vistaSeleccionada = new EventEmitter<'dashboard' | 'usuarios' | 'auditoria'>();

  isCollapsed = false;
  mobileOpen = false;

  constructor(private authService: AuthService) {}

  toggleSidebar() { this.isCollapsed = !this.isCollapsed; }
  toggleMobileMenu() { this.mobileOpen = !this.mobileOpen; }

  seleccionar(vista: 'dashboard' | 'usuarios' | 'auditoria') {
    this.vistaSeleccionada.emit(vista);
    if (this.mobileOpen) this.toggleMobileMenu();
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    window.location.href = '/login';
  }
}
