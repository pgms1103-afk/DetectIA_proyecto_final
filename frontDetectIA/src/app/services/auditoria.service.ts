import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditoriaLogModel } from '../models/auditoria.model';

@Injectable({
  providedIn: 'root',
})
export class AuditoriaService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase = 'http://localhost:8080/admin/auditoria';

  getTodos(): Observable<AuditoriaLogModel[]> {
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/todos');
  }

  getPorAccion(accion: string): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('accion', accion);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/poraccion', { params: parametros });
  }

  getPorModulo(modulo: string): Observable<AuditoriaLogModel[]> {
    const parametros = new HttpParams().set('modulo', modulo);
    return this.cliente.get<AuditoriaLogModel[]>(this.urlbase + '/pormodulo', { params: parametros });
  }
}
