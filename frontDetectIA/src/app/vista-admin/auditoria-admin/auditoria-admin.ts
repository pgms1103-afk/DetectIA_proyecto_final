import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditoriaService } from '../../services/auditoria.service';
import { AuditoriaLogModel } from '../../models/auditoria.model';

@Component({
  selector: 'app-auditoria-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auditoria-admin.html',
  styleUrl: './auditoria-admin.css',
})
export class AuditoriaAdmin implements OnInit {
  private auditoriaService = inject(AuditoriaService);

  auditorias: AuditoriaLogModel[] = [];
  filtroCorreo = '';
  filtroAccion = '';
  filtroModulo = '';
  filtroExitoso = '';

  buscar() {
    if (this.filtroCorreo.trim()) {
      this.auditoriaService.getPorCorreo(this.filtroCorreo).subscribe({
        next: (datos) => this.auditorias = datos,
        error: (err) => console.error(err)
      });
    } else if (this.filtroAccion.trim()) {
      this.auditoriaService.getPorAccion(this.filtroAccion).subscribe({
        next: (datos) => this.auditorias = datos,
        error: (err) => console.error(err)
      });
    } else if (this.filtroModulo.trim()) {
      this.auditoriaService.getPorModulo(this.filtroModulo).subscribe({
        next: (datos) => this.auditorias = datos,
        error: (err) => console.error(err)
      });
    } else if (this.filtroExitoso !== '') {
      this.auditoriaService.getPorExitoso(this.filtroExitoso === 'true').subscribe({
        next: (datos) => this.auditorias = datos,
        error: (err) => console.error(err)
      });
    } else {
      this.cargarAuditorias();
    }
  }

  limpiarFiltros() {
    this.filtroCorreo = '';
    this.filtroAccion = '';
    this.filtroModulo = '';
    this.filtroExitoso = '';
    this.cargarAuditorias();
  }

  cargarAuditorias() {
    this.auditoriaService.getTodos().subscribe({
      next: (datos) => this.auditorias = datos,
      error: (err) => console.error('Error cargando auditorías:', err)
    });
  }

  ngOnInit(): void {
    this.cargarAuditorias();
  }

}
