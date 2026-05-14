import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  @Output() herramientaSeleccionada = new EventEmitter<string>();

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
}
