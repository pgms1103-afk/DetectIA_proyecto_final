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

  mensajeError = '';
  mensajeExito = '';

  usuarioNuevo: UsuarioModel = {
    nombreUsuario: '',
    correo: '',
    contrasena: '',
    role: Role.USER,
    totalArchivos: 0,
  };

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  private static extraerError(err: unknown): string {
    const errorObj = err as { error?: string | { message?: string } };
    if (errorObj?.error) {
      if (typeof errorObj.error === 'string') return errorObj.error;
      if (errorObj.error.message) return errorObj.error.message;
    }
    return 'Ocurrió un error en el servidor.';
  }

  cargarUsuarios() {
    this.usuarioService.getMostrarUsuarios().subscribe({
      next: (datos) => {
        this.usuarios = datos;
      },
      error: (e) => {

      },
    });
  }

  mostrarModal = false;
  modoModal: 'crear' | 'editar' = 'crear';

  abrirModalCrear() {
    this.modoModal = 'crear';
    this.mensajeError = '';
    this.mensajeExito = '';
    this.usuarioNuevo = { nombreUsuario: '', correo: '', contrasena: '', role: Role.USER, totalArchivos: 0 };
    this.mostrarModal = true;
  }

  abrirModalEditar(user: UsuarioModel) {
    this.modoModal = 'editar';
    this.mensajeError = '';
    this.mensajeExito = '';
    this.mostrarModal = true;
    this.id = user.id;
    this.usuarioNuevo = {
      id: user.id,
      nombreUsuario: user.nombreUsuario,
      correo: user.correo,
      contrasena: '',
      role: user.role,
      totalArchivos: user.totalArchivos
    };
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  crearOactualizar(){
    this.mensajeError = '';
    this.mensajeExito = '';

    if(!this.usuarioNuevo.nombreUsuario || !this.usuarioNuevo.correo || (!this.usuarioNuevo.contrasena && this.modoModal === 'crear')){
      this.mensajeError = 'Debe completar todos los campos obligatorios.';
      return;
    }

    if(this.modoModal === 'crear'){
      this.usuarioService.postCrearUsuario(this.usuarioNuevo).subscribe({
        next: (datos) => {

          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
          this.cerrarModal();
        },
        error: (e) => {

        },
      });
    }else{
      if (this.id === undefined) {
        this.mensajeError = 'No se ha seleccionado ningún usuario para editar.';
        return;
      }
      this.usuarioService.putActualizarUsuario(this.id, this.usuarioNuevo).subscribe({
        next: (datos) => {

          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
          this.cerrarModal();
        }, error: (e) => {

        }
      });
    }
  }

  eliminarUsuario(user: any) {

    this.usuarioService.deleteUsuarios(user.id).subscribe({
      next: (datos) => {

        this.cargarUsuarios();
      },
      error: (e) => {

      }
    });
  }
}
