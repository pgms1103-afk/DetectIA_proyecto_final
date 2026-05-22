import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Subject } from 'rxjs';
import { ResultadoIAModel } from '../models/resultadoIA.model';
import { AnalisisModel } from '../models/analisis.model';


@Injectable({
  providedIn: 'root',
})

export class ResultadoIAService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase: String = 'http://localhost:8080/private/resultadoporia';
  public listaResultados = new Subject<ResultadoIAModel[]>();
  listaResultados$ = this.listaResultados.asObservable();

  getMostrarResultadosPorCorreo(){
    return this.cliente.get<ResultadoIAModel[]>(this.urlbase + '/mostrarresultadosporcorreo',
      {observe: 'response'});
  }

  getMostrarAnalisis(){
    return this.cliente.get<AnalisisModel[]>(this.urlbase + '/mostraranalisis',
      {observe: 'response'});
  }

  getMostrarResultadosPorId(id: number){
    const parametros = new HttpParams().set('id',id.toString());
    return this.cliente.get<ResultadoIAModel[]>(`${this.urlbase}/mostrarresultadoporarchivo`,{
      params: parametros,
    });
  }

  getMostrarAnalisisPorId(id: number){
    const parametros = new HttpParams().set('id',id.toString());
    return this.cliente.get<AnalisisModel[]>(`${this.urlbase}/mostraranalisisporarchivo`,{
      params: parametros,
    });
  }
}
