import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UsuarioModel } from '../models/usuario.model';
import { Observable, Subject } from 'rxjs';

/**
 * Servicio encargado de gestionar las operaciones CRUD de usuarios en DetectIA.
 * Incluye creación, consulta, actualización y eliminación de cuentas de usuario.
 */
@Injectable({
  providedIn: 'root',
})
export class UsuarioService {

  private cliente: HttpClient = inject(HttpClient);

  /** URL base del endpoint de administración de usuarios. */
  private readonly urlbase: String = 'https://gpcueb.org/detectiaIA/admin';

  /** Subject interno para señalizar el refresco de la tabla de usuarios. */
  private refrescarTabla = new Subject<void>();

  /** Subject que emite la lista actualizada de usuarios. */
  public listaUsuarios = new Subject<UsuarioModel[]>();

  /** Observable de la lista de usuarios. */
  listausuarios$ = this.listaUsuarios.asObservable();

  /** Subject que emite el tipo de filtro activo en la tabla. */
  private tipoFiltro = new Subject<string>();

  /** Observable del tipo de filtro activo. */
  tipoFiltro$ = this.tipoFiltro.asObservable();

  /**
   * Crea un nuevo usuario en el sistema.
   * @param usuario Datos del usuario a crear.
   * @returns Observable con la respuesta en texto plano del servidor.
   */
  postCrearUsuario(usuario: UsuarioModel): Observable<string> {
    return this.cliente.post(this.urlbase + '/crearusuario', usuario, { responseType: 'text' });
  }

  /**
   * Obtiene la lista completa de usuarios registrados en el sistema.
   * @returns Observable con la lista de usuarios.
   */
  getMostrarUsuarios(): Observable<UsuarioModel[]> {
    return this.cliente.get<UsuarioModel[]>(this.urlbase + '/mostrarusuarios');
  }

  /**
   * Actualiza los datos de un usuario existente.
   * @param id ID del usuario a actualizar.
   * @param usuario Nuevos datos del usuario.
   * @returns Observable con la respuesta en texto plano del servidor.
   */
  putActualizarUsuario(id: number, usuario: UsuarioModel): Observable<string> {
    const parametros = new HttpParams().set('id', id.toString());
    return this.cliente.put(`${this.urlbase}/actualizarusuarios`, usuario, {
      params: parametros,
      responseType: 'text'
    });
  }

  /**
   * Elimina un usuario del sistema por su ID.
   * @param id ID del usuario a eliminar.
   * @returns Observable con la respuesta en texto plano del servidor.
   */
  deleteUsuarios(id: number): Observable<string> {
    const parametros = new HttpParams().set('id', id.toString());
    return this.cliente.delete(`${this.urlbase}/eliminarusuarios`, {
      params: parametros,
      responseType: 'text'
    });
  }

  /**
   * Obtiene los datos del usuario actualmente autenticado.
   * @returns Observable con los datos del usuario registrado.
   */
  getDatosUsuarioRegistrado(): Observable<UsuarioModel> {
    return this.cliente.get<UsuarioModel>('https://gpcueb.org/detectiaIA/private/user/misdatos');
  }
}
