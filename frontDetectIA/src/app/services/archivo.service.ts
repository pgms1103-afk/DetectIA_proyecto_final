import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { ArchivoModel } from '../models/archivo.model';
import { AnalisisModel } from '../models/analisis.model';

/**
 * Servicio encargado de gestionar todas las operaciones relacionadas con archivos
 * en DetectIA, incluyendo análisis de contenido, historial y edición.
 */
@Injectable({
  providedIn: 'root',
})
export class ArchivoService {

  private cliente: HttpClient = inject(HttpClient);

  /** URL base del endpoint privado de archivos. */
  private readonly urlbase: String = 'https://gpcueb.org/detectiaIA/private/archivo';

  /** Subject que emite cuando un análisis se completa, para refrescar el historial. */
  public analisisCompletado$ = new Subject<void>();

  /** Subject interno para refrescar la tabla de archivos. */
  private refrescarTabla = new Subject<void>();

  /** Subject que emite la lista actualizada de archivos. */
  public listaArchivos = new Subject<ArchivoModel[]>();

  /** Observable de la lista de archivos. */
  listaUsuarios$ = this.listaArchivos.asObservable();

  /** Subject que emite el archivo seleccionado desde el historial del sidebar. */
  public archivoSeleccionado$ = new Subject<ArchivoModel>();

  /**
   * Envía un archivo local al backend para su análisis de IA.
   * @param nombre Nombre descriptivo del análisis.
   * @param archivo Archivo a analizar.
   * @returns Observable con el resultado del análisis.
   */
  postAnalizarArchivo(nombre: string, archivo: File): Observable<AnalisisModel> {
    const formData = new FormData();
    formData.append('nombre', nombre);
    formData.append('archivo', archivo);
    return this.cliente.post<AnalisisModel>(`${this.urlbase}/analizar`, formData);
  }

  /**
   * Envía una URL de imagen o audio al backend para su análisis de IA.
   * @param nombre Nombre descriptivo del análisis.
   * @param url URL pública del archivo a analizar.
   * @returns Observable con el resultado del análisis.
   */
  postAnalizarUrl(nombre: string, url: string): Observable<AnalisisModel> {
    const params = new HttpParams()
      .set('nombre', nombre)
      .set('url', url);
    return this.cliente.post<AnalisisModel>(
      this.urlbase + '/analizarimagenurl',
      null,
      { params }
    );
  }

  /**
   * Envía un texto al backend para su análisis de IA.
   * @param nombre Nombre descriptivo del análisis.
   * @param texto Texto a analizar.
   * @returns Observable con el resultado del análisis.
   */
  postAnalizarTexto(nombre: string, texto: string): Observable<AnalisisModel> {
    const body = new URLSearchParams();
    body.set('nombre', nombre);
    body.set('texto', texto);
    return this.cliente.post<AnalisisModel>(
      `${this.urlbase}/analizartexto`,
      body.toString(),
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    );
  }

  /**
   * Obtiene los archivos analizados del usuario autenticado.
   * @returns Observable con la respuesta HTTP completa que contiene la lista de archivos.
   */
  getMisArchivos() {
    return this.cliente.get<ArchivoModel[]>(this.urlbase + '/mis-archivos',
      { observe: 'response' });
  }

  /**
   * Elimina un archivo por su ID.
   * @param id ID del archivo a eliminar.
   * @returns Observable con la respuesta en texto plano.
   */
  deleteArchivos(id: number) {
    return this.cliente.delete(`${this.urlbase}/eliminar?id=${id}`, {
      responseType: 'text'
    });
  }

  /**
   * Busca archivos del usuario autenticado por nombre.
   * @param nombreArchivo Nombre a buscar.
   * @returns Observable con la respuesta HTTP completa que contiene los archivos encontrados.
   */
  getBuscarArchivosPorNombre(nombreArchivo: string) {
    return this.cliente.get<ArchivoModel[]>(`${this.urlbase}/buscarpornombre`, {
      observe: 'response',
      params: { nombreArchivo }
    });
  }

  /**
   * Obtiene todos los archivos del sistema (solo para administradores).
   * @returns Observable con la lista completa de archivos.
   */
  getAllArchivos(): Observable<ArchivoModel[]> {
    return this.cliente.get<ArchivoModel[]>('https://gpcueb.org/detectiaIA/admin/archivos');
  }

  /**
   * Actualiza el nombre de un archivo por su ID.
   * @param id ID del archivo a editar.
   * @param nombre Nuevo nombre del archivo.
   * @returns Observable con la respuesta en texto plano.
   */
  putEditarNombre(id: number, nombre: string) {
    return this.cliente.put(
      `${this.urlbase}/editarnombre?id=${id}&nombre=${nombre}`,
      null,
      { responseType: 'text' }
    );
  }
}
