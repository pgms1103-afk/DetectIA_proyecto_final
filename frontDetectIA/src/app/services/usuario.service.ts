import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UsuarioModel } from '../models/usuario.model';
import { Observable, Subject } from 'rxjs'

@Injectable({
  providedIn: 'root',
})

export class UsuarioService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase: String = 'http://localhost:8080/admin'

  private refrescarTabla = new Subject<void>();

  public listaUsuarios = new Subject<UsuarioModel[]>();
  listausuarios$ = this.listaUsuarios.asObservable();

  private tipoFiltro = new Subject<string>();
  tipoFiltro$ = this.tipoFiltro.asObservable();

  postCrearUsuario(usuario: UsuarioModel):Observable<string>{
    return this.cliente.post(this.urlbase + '/crearUsuario', usuario, {responseType: 'text'});
  }

  getMostrarUsuarios(): Observable<UsuarioModel[]>{
    return this.cliente.get<UsuarioModel[]>(this.urlbase + '/mostrarusuarios',)
  }

  putActualizarUsuario(id:number, usuario:UsuarioModel): Observable<string>{
    return this.cliente.put(this.urlbase+'/actualizarusuarios', usuario, {responseType: 'text'});
  }

  deleteUsuarios(id:number): Observable<string>{
    const parametros = new HttpParams().set('id', id.toString());
    return this.cliente.delete(`${this.urlbase}/eliminarusuarios`, {
      params: parametros,
      responseType: 'text'
    });
  }
}
