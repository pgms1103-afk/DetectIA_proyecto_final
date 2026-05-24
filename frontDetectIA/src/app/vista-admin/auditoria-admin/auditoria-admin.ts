import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditoriaService } from '../../services/auditoria.service';
import { AuditoriaLogModel } from '../../models/auditoria.model';

/**
 * Componente para la administración y visualización de logs de auditoría.
 * Permite filtrar registros por diversos criterios y gestionar la visualización de los mismos.
 */
@Component({
  selector: 'app-auditoria-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auditoria-admin.html',
  styleUrl: './auditoria-admin.css',
})
export class AuditoriaAdmin implements OnInit {
  /** Servicio para realizar peticiones HTTP relacionadas con auditorías. */
  private auditoriaService = inject(AuditoriaService);

  /** Lista de registros de auditoría obtenidos del servicio. */
  auditorias: AuditoriaLogModel[] = [];

  /** Filtro de búsqueda por dirección de correo electrónico. */
  filtroCorreo = '';
  /** Filtro de búsqueda por nombre de la acción realizada. */
  filtroAccion = '';
  /** Filtro de búsqueda por nombre del módulo afectado. */
  filtroModulo = '';
  /** Filtro de búsqueda por éxito o fallo de la operación ('true', 'false' o vacío). */
  filtroExitoso = '';

  /**
   * Ejecuta la búsqueda de auditorías basándose en los filtros activos.
   * La lógica prioriza el filtrado según el orden: Correo -> Acción -> Módulo -> Exitoso.
   * Si no hay filtros aplicados, carga el listado completo.
   */
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

  /**
   * Genera un identificador visual (iniciales) a partir de un nombre.
   * @param nombre El nombre completo del usuario.
   * @returns Las iniciales en mayúsculas (máximo 2 caracteres) o '?' si el nombre está vacío.
   */
  obtenerIniciales(nombre: string): string {
    if (!nombre) return '?';
    return nombre
      .split(' ')
      .map(palabra => palabra.charAt(0).toUpperCase())
      .slice(0, 2)
      .join('');
  }

  /**
   * Reinicia todos los filtros de búsqueda a sus valores iniciales
   * y recarga el listado completo de auditorías.
   */
  limpiarFiltros() {
    this.filtroCorreo = '';
    this.filtroAccion = '';
    this.filtroModulo = '';
    this.filtroExitoso = '';
    this.cargarAuditorias();
  }

  /**
   * Obtiene todos los registros de auditoría disponibles a través del servicio.
   */
  cargarAuditorias() {
    this.auditoriaService.getTodos().subscribe({
      next: (datos) => this.auditorias = datos,
      error: (err) => console.error('Error cargando auditorías:', err)
    });
  }

  /**
   * Ciclo de vida de Angular: se ejecuta al inicializar el componente.
   * Realiza la carga inicial de todas las auditorías.
   */
  ngOnInit(): void {
    this.cargarAuditorias();
  }
}
