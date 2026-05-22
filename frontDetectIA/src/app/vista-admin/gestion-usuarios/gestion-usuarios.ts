import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioModel } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { Role } from '../../models/role.enum';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-gestion-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-usuarios.html',
  styleUrl: './gestion-usuarios.css',
})
export class GestionUsuarios implements OnInit {
  public usuarios: UsuarioModel[] = [];
  private usuarioService: UsuarioService = inject(UsuarioService);
  public id: number | undefined = undefined;

  usuarioNuevo: UsuarioModel = {
    nombreUsuario: '',
    correo: '',
    contrasena: '',
    role: Role.USER,
  };

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.usuarioService.getMostrarUsuarios().subscribe({
      next: (datos) => {
        this.usuarios = datos;
      },
      error: (e) => {
        console.error('Algo fallo');
      },
    });
  }

  mostrarModal = false;
  modoModal: 'crear' | 'editar' = 'crear';

  abrirModalCrear() {
    this.modoModal = 'crear';
    this.mostrarModal = true;
  }

  abrirModalEditar(user: UsuarioModel) {
    this.modoModal = 'editar';
    this.mostrarModal = true;
    this.id = user.id;
    this.usuarioNuevo = {
      id: user.id,
      nombreUsuario: user.nombreUsuario,
      correo: user.correo,
      contrasena: '', // La contraseña se deja vacía por seguridad en la edición
      role: user.role
    };
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  crearOactualizar(){
    if(this.modoModal === 'crear'){
      this.usuarioService.postCrearUsuario(this.usuarioNuevo).subscribe({
        next: (datos) => {
          console.log('Se creo el usuario');
          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
        },
        error: (e) => {
          console.error('No se creo el usuario');
        },
      });
    }else{
      if (this.id === undefined) {
        alert('No se ha seleccionado ningún usuario para editar.');
        return;
      }
      this.usuarioService.putActualizarUsuario(this.id, this.usuarioNuevo).subscribe({
        next: (datos) => {
          console.log('Se actualizó correctamente');
          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
        }, error: (e) => {
          console.error('No se actualizó');
        }
      })
    }
  }

  eliminarUsuario(user: any) {

    this.usuarioService.deleteUsuarios(user.id).subscribe({
      next: (datos) => {
        console.log('Se eliminó el usuario');
        this.cargarUsuarios();
      },
      error: (e) => {
        console.error("No se pudo eliminar el usuario", e);
      }
    });
  }
}
