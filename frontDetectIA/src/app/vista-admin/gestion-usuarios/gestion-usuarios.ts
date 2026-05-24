import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioModel } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { Role } from '../../models/role.enum';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

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
  private toastr: ToastrService = inject(ToastrService);

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
      error: (err) => {
        this.toastr.error(err.error|| "No se pudo cargar el usuario", 'Error') },
    });
  }

  /** Prepara el formulario en modo 'crear' y abre el modal */
  abrirModalCrear() {
    this.modoModal = 'crear';
    this.usuarioNuevo = { nombreUsuario: '', correo: '', contrasena: '', role: Role.USER, totalArchivos: 0 };
    this.mostrarModal = true;
  }

  /** Carga los datos de un usuario en el formulario y abre el modal en modo 'editar' */
  abrirModalEditar(user: UsuarioModel) {
    this.modoModal = 'editar';
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

    if(!this.usuarioNuevo.nombreUsuario || !this.usuarioNuevo.correo || (!this.usuarioNuevo.contrasena && this.modoModal === 'crear')){
      this.toastr.warning('Debe completar todos los campos obligatorios.', 'Advertencia') ;
      return;
    }

    if(this.modoModal === 'crear'){
      this.usuarioService.postCrearUsuario(this.usuarioNuevo).subscribe({
        next: () => { this.toastr.success("Usuario creado correctamente", 'Éxito')
          this.cargarUsuarios(); this.cerrarModal(); },
        error: (err) => {
          this.toastr.error(err.error|| "No se pudo crear el usuario", 'Error')
        },
      });
    } else {
      if (this.id === undefined) {
        this.mensajeError = 'No se ha seleccionado ningún usuario para editar.';
        return;
      }
      this.usuarioService.putActualizarUsuario(this.id, this.usuarioNuevo).subscribe({
        next: () => {
          this.toastr.success("Usuario actualizado correctamente", 'Éxito')
          this.cargarUsuarios(); this.cerrarModal(); },
        error: (err) => {
          this.toastr.error(err.error|| "No se pudo actualizar el usuario", 'Error')
        }
      });
    }
  }

  /** Elimina un usuario por su ID y recarga el listado */
  eliminarUsuario(user: any) {
    this.usuarioService.deleteUsuarios(user.id).subscribe({
      next: () => { this.cargarUsuarios(); },
      error: (err) => {}
    });
  }
}
