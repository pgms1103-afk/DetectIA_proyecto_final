import { Component, Output, EventEmitter, HostBinding } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

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

  @HostBinding('class.sidebar-cerrada') get sidebarCerrada() {
    return !this.mobileOpen;
  }

  constructor(private authService: AuthService, private router: Router) {}

  toggleSidebar() { this.isCollapsed = !this.isCollapsed; }
  toggleMobileMenu() {
    this.mobileOpen = !this.mobileOpen;
  }

  seleccionar(vista: 'dashboard' | 'usuarios' | 'auditoria') {
    this.vistaSeleccionada.emit(vista);
    if (this.mobileOpen) this.toggleMobileMenu();
  }

  irAVistaUsuario() {
    this.router.navigate(['/usuario']);
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    window.location.href = '/login';
  }
}
