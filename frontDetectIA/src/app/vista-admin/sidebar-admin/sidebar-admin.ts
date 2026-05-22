import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.css'
})
export class SidebarAdmin {
  @Output() vistaSeleccionada = new EventEmitter<'dashboard' | 'usuarios'>();

  isCollapsed = false;
  mobileOpen = false;

  toggleSidebar() { this.isCollapsed = !this.isCollapsed; }
  toggleMobileMenu() { this.mobileOpen = !this.mobileOpen; }

  seleccionar(vista: 'dashboard' | 'usuarios') {
    this.vistaSeleccionada.emit(vista);
    if (this.mobileOpen) this.toggleMobileMenu();
  }
}
