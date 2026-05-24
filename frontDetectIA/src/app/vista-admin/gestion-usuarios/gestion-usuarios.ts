import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioModel } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { Role } from '../../models/role.enum';
import { FormsModule } from '@angular/forms';

/**
 * @component GestionUsuarios
 * @description Componente administrativo para el CRUD de usuarios.
 * Permite listar, crear, editar y eliminar usuarios del sistema.
 */
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

  /** Objeto temporal para el formulario de creación/edición */
  usuarioNuevo: UsuarioModel = {
    nombreUsuario: '',
    correo: '',
    contrasena: '',
    role: Role.USER,
    totalArchivos: 0,
  };

  mostrarModal = false;
  modoModal: 'crear' | 'editar' = 'crear';

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  /**
   * @method extraerError
   * @description Método estático para normalizar mensajes de error provenientes del servidor.
   */
  private static extraerError(err: unknown): string {
    const errorObj = err as { error?: string | { message?: string } };
    if (errorObj?.error) {
      if (typeof errorObj.error === 'string') return errorObj.error;
      if (errorObj.error.message) return errorObj.error.message;
    }
    return 'Ocurrió un error en el servidor.';
  }

  /** Obtiene el listado actualizado de usuarios desde el servicio */
  cargarUsuarios() {
    this.usuarioService.getMostrarUsuarios().subscribe({
      next: (datos) => { this.usuarios = datos; },
      error: () => { /* Manejo de error silencioso según lógica original */ },
    });
  }

  /** Prepara el formulario en modo 'crear' y abre el modal */
  abrirModalCrear() {
    this.modoModal = 'crear';
    this.mensajeError = '';
    this.mensajeExito = '';
    this.usuarioNuevo = { nombreUsuario: '', correo: '', contrasena: '', role: Role.USER, totalArchivos: 0 };
    this.mostrarModal = true;
  }

  /** Carga los datos de un usuario en el formulario y abre el modal en modo 'editar' */
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

  /** Cierra el modal de gestión */
  cerrarModal() {
    this.mostrarModal = false;
  }

  /** Ejecuta la lógica de creación o actualización según el estado del modal */
  crearOactualizar(){
    this.mensajeError = '';
    this.mensajeExito = '';

    if(!this.usuarioNuevo.nombreUsuario || !this.usuarioNuevo.correo || (!this.usuarioNuevo.contrasena && this.modoModal === 'crear')){
      this.mensajeError = 'Debe completar todos los campos obligatorios.';
      return;
    }

    if(this.modoModal === 'crear'){
      this.usuarioService.postCrearUsuario(this.usuarioNuevo).subscribe({
        next: () => { this.cargarUsuarios(); this.cerrarModal(); },
        error: () => {},
      });
    } else {
      if (this.id === undefined) {
        this.mensajeError = 'No se ha seleccionado ningún usuario para editar.';
        return;
      }
      this.usuarioService.putActualizarUsuario(this.id, this.usuarioNuevo).subscribe({
        next: () => { this.cargarUsuarios(); this.cerrarModal(); },
        error: () => {}
      });
    }
  }

  /** Elimina un usuario por su ID y recarga el listado */
  eliminarUsuario(user: any) {
    this.usuarioService.deleteUsuarios(user.id).subscribe({
      next: () => { this.cargarUsuarios(); },
      error: () => {}
    });
  }
}
