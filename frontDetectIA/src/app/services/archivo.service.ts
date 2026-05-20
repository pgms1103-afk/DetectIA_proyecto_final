import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { ArchivoModel } from '../models/archivo.model';


@Injectable({
  providedIn: 'root',
})

export class ArchivoService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase: String = 'http://localhost:8080/private/archivo'

  private refrescarTabla = new Subject<void>();

  public listaArchivos = new Subject<ArchivoModel[]>();
  listaUsuarios$ = this.listaArchivos.asObservable();

  postAnalizarArchivo(nombre:string, archivo: File){
    const formData = new FormData();
    formData.append('nombre', nombre);
    formData.append('archivo', archivo);
    return this.cliente.post(this.urlbase
    + '/analizar',
    formData
    );}

  postAnalizarUrl(nombre:string, url:string){
    return this.cliente.post(this.urlbase + "/analizarurl?nombre="+ nombre +
      " &url="+url,
      null);
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





}
