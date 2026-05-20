import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioModel } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-gestion-usuarios',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-usuarios.html',
  styleUrl: './gestion-usuarios.css',
})
export class GestionUsuarios implements OnInit {

  usuarios: UsuarioModel[] = [];
  usuarioService: UsuarioService = inject(UsuarioService);

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.usuarioService.getMostrarUsuarios().subscribe({
      next: (datos) =>{
        this.usuarios = datos;
        console.log(this.usuarios);
      },
      error: (e) => {
        console.error("Algo fallo");
      }
    })
  }

  listaUsuarios = [
    {
      id: 1,
      nombre: 'Jose Manuel Toro',
      correo: 'jose.toro@unbosque.edu.co',
      rol: 'Admin',
      activo: true,
      fecha: '12 May 2026',
    },
    {
      id: 2,
      nombre: 'Nelson Cipagauta',
      correo: 'nelson.c@email.com',
      rol: 'Usuario',
      activo: true,
      fecha: '10 May 2026',
    },
    {
      id: 3,
      nombre: 'Gloria Toro',
      correo: 'gloria.t@email.com',
      rol: 'Usuario',
      activo: false,
      fecha: '01 May 2026',
    },
    {
      id: 4,
      nombre: 'Yireth Fonseca',
      correo: 'yireth.f@email.com',
      rol: 'Usuario',
      activo: true,
      fecha: '28 Abr 2026',
    },
  ];

  mostrarModal = false;
  modoModal: 'crear' | 'editar' = 'crear';

  abrirModalCrear() {
    this.modoModal = 'crear';
    this.mostrarModal = true;
  }

  abrirModalEditar() {
    this.modoModal = 'editar';
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  simularGuardar() {
    console.log('Aquí se enviará la petición POST/PUT a Spring Boot');
    this.cerrarModal();
  }

  simularEliminar() {
    console.log('Aquí se enviará la petición DELETE a Spring Boot');
  }


}
