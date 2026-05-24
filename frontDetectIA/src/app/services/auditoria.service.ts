import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditoriaLogModel } from '../models/auditoria.model';

/**
 * Servicio encargado de consultar los registros de auditoría del sistema DetectIA.
 * Permite filtrar los logs por correo, acción, módulo y estado de éxito.
 * Solo accesible para usuarios con rol ADMIN.
 */
@Injectable({
  providedIn: 'root',
})
export class AuditoriaService {

  private cliente: HttpClient = inject(HttpClient);

  /** URL base del endpoint de auditoría. */
  private readonly urlbase = 'http://localhost:8080/admin/auditoria';

  /**
   * Obtiene todos los registros de auditoría del sistema.
   * @returns Observable con la lista completa de logs de auditoría.
   */
  getTodos(): Observable<AuditoriaLogModel[]> {
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/todos');
  }

  /**
   * Obtiene los registros de auditoría filtrados por correo electrónico.
   * @param correo Correo del usuario a filtrar.
   * @returns Observable con los logs del usuario indicado.
   */
  getPorCorreo(correo: string): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('correo', correo);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/porcorreo/', { params: parametros });
  }

  /**
   * Obtiene los registros de auditoría filtrados por tipo de acción.
   * @param accion Nombre de la acción a filtrar (ej. 'CREAR_USUARIO', 'ANALISIS').
   * @returns Observable con los logs de la acción indicada.
   */
  getPorAccion(accion: string): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('accion', accion);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/poraccion', { params: parametros });
  }

  /**
   * Obtiene los registros de auditoría filtrados por módulo del sistema.
   * @param modulo Nombre del módulo a filtrar (ej. 'ANALISIS', 'USUARIO').
   * @returns Observable con los logs del módulo indicado.
   */
  getPorModulo(modulo: string): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('modulo', modulo);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/pormodulo', { params: parametros });
  }

  /**
   * Obtiene los registros de auditoría filtrados por estado de éxito.
   * @param exitoso true para mostrar solo acciones exitosas, false para las fallidas.
   * @returns Observable con los logs filtrados por estado.
   */
  getPorExitoso(exitoso: boolean): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('exitoso', exitoso);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/porexitoso', { params: parametros });
  }
}
