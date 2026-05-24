import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { ResultadoIAModel } from '../models/resultadoIA.model';
import { AnalisisModel } from '../models/analisis.model';

/**
 * Servicio encargado de gestionar los resultados de detección de IA
 * y los análisis realizados en DetectIA.
 */
@Injectable({
  providedIn: 'root',
})
export class ResultadoIAService {

  private cliente: HttpClient = inject(HttpClient);

  /** URL base del endpoint privado de resultados de IA. */
  private readonly urlbase: String = 'http://localhost:8080/private/resultadoporia';

  /** Subject que emite la lista actualizada de resultados de IA. */
  public listaResultados = new Subject<ResultadoIAModel[]>();

  /** Observable de la lista de resultados de IA. */
  listaResultados$ = this.listaResultados.asObservable();

  /**
   * Obtiene todos los resultados de IA del usuario autenticado.
   * @returns Observable con la respuesta HTTP completa que contiene los resultados.
   */
  getMostrarResultadosPorCorreo() {
    return this.cliente.get<ResultadoIAModel[]>(this.urlbase + '/mostrarresultadosporcorreo',
      { observe: 'response' });
  }

  /**
   * Obtiene todos los análisis del usuario autenticado.
   * @returns Observable con la respuesta HTTP completa que contiene los análisis.
   */
  getMostrarAnalisis() {
    return this.cliente.get<AnalisisModel[]>(this.urlbase + '/mostraranalisis',
      { observe: 'response' });
  }

  /**
   * Obtiene los resultados de IA de un archivo específico por su ID.
   * @param id ID del archivo cuyos resultados se desean obtener.
   * @returns Observable con la lista de resultados del archivo.
   */
  getMostrarResultadosPorId(id: number) {
    const parametros = new HttpParams().set('id', id.toString());
    return this.cliente.get<ResultadoIAModel[]>(`${this.urlbase}/mostrarresultadoporarchivo`, {
      params: parametros,
    });
  }

  /**
   * Obtiene los análisis de un archivo específico por su ID.
   * @param id ID del archivo cuyos análisis se desean obtener.
   * @returns Observable con la lista de análisis del archivo.
   */
  getMostrarAnalisisPorId(id: number) {
    const parametros = new HttpParams().set('id', id.toString());
    return this.cliente.get<AnalisisModel[]>(`${this.urlbase}/mostraranalisisporarchivo`, {
      params: parametros,
    });
  }

  /**
   * Obtiene todos los resultados de IA del sistema (solo para administradores).
   * @returns Observable con la lista completa de resultados.
   */
  getAllResultados(): Observable<ResultadoIAModel[]> {
    return this.cliente.get<ResultadoIAModel[]>('http://localhost:8080/admin/resultados');
  }
}
