import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { ArchivoModel } from '../models/archivo.model';
import { AnalisisModel } from '../models/analisis.model';


@Injectable({
  providedIn: 'root',
})

export class ArchivoService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase: String = 'http://localhost:8080/private/archivo';
  public analisisCompletado$ = new Subject<void>();

  private refrescarTabla = new Subject<void>();

  public listaArchivos = new Subject<ArchivoModel[]>();
  listaUsuarios$ = this.listaArchivos.asObservable();
  public archivoSeleccionado$ = new Subject<ArchivoModel>();

// 🟢 CORREGIDO: Le decimos que retorna un Observable de tipo AnalisisModel
  postAnalizarArchivo(nombre: string, archivo: File): Observable<AnalisisModel> {
    const formData = new FormData();
    formData.append('nombre', nombre);
    formData.append('archivo', archivo);

    // 🟢 CORREGIDO: Le pasamos el tipo <AnalisisModel> al método post
    return this.cliente.post<AnalisisModel>(
      this.urlbase + '/analizar',
      formData
    );
  }

  // 🟢 CORREGIDO TAMBIÉN AQUÍ: Para que tu medidor por URL tampoco tire error
  postAnalizarUrl(nombre: string, url: string): Observable<AnalisisModel> {
    return this.cliente.post<AnalisisModel>(
      this.urlbase + "/analizarurl?nombre=" + nombre + "&url=" + url,
      null
    );
  }

  getMisArchivos() {
    return this.cliente.get<ArchivoModel[]>(this.urlbase + '/mis-archivos',
      {observe: 'response'});
  }

  deleteArchivos(id:number){
    return this.cliente.delete(this.urlbase +
      '/eliminar?id='+id,
    {responseType: 'text'}
    );
  }

  getBuscarArchivosPorNombre(nombreArchivo: string) {
    return this.cliente.get<ArchivoModel[]>(`${this.urlbase}/buscarpornombre`, {
      observe: 'response',
      params: { nombreArchivo }
    });
  }

  getAllArchivos(): Observable<ArchivoModel[]> {
    return this.cliente.get<ArchivoModel[]>('http://localhost:8080/admin/archivos');
  }





}
