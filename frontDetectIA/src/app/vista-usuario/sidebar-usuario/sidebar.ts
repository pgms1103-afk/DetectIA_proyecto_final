import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  @Output() herramientaSeleccionada = new EventEmitter<string>();

  private authService = inject(AuthService);
  private router = inject(Router);

  showProfileMenu: boolean = false;
  user = {
    name: 'Jose Manuel',
    email: 'jose.manuel@elbosque.edu.co',
    time: '14h 20m',
    files: 28
  };

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  isCollapsed = false;
  mobileOpen = false;
  herramientaActual = 'texto';

  toggleSidebar() { this.isCollapsed = !this.isCollapsed; }
  toggleMobileMenu() { this.mobileOpen = !this.mobileOpen; }

  seleccionarHerramienta(herramienta: string) {
    this.herramientaActual = herramienta;
    this.herramientaSeleccionada.emit(herramienta);
    if (this.mobileOpen) this.toggleMobileMenu();
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}
