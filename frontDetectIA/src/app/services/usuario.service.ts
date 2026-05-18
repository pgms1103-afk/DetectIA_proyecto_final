import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UsuarioModel } from '../models/usuario.model';
import { Subject } from 'rxjs'

@Injectable({
  providedIn: 'root',
})

export class UsuarioService {

  private cliente: HttpClient = inject(HttpClient);
  private readonly urlbase: String = 'http://localhost:8080/admin'
}
