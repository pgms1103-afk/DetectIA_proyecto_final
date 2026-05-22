import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioModel } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { Role } from '../../models/role.enum';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

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
  private toastr: ToastrService = inject(ToastrService);
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
        this.toastr.error(e.error ||'Error al cargar los usuarios', 'Error');
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
          this.toastr.success('Usuario creado con exito', 'Exito');
          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
          this.cerrarModal();
        },
        error: (e) => {
          this.toastr.error(e.error || 'No se pudo crear el usuario', 'Error');
        },
      });
    }else{
      if (this.id === undefined) {
        alert('No se ha seleccionado ningún usuario para editar.');
        return;
      }
      this.usuarioService.putActualizarUsuario(this.id, this.usuarioNuevo).subscribe({
        next: (datos) => {
          this.toastr.success('Se actualizó correctamente el usuario', 'Exito');
          this.cargarUsuarios();//Es para refresar la tabla cuando se realiza la accion, no la borren
          this.cerrarModal();
        }, error: (e) => {
          this.toastr.error(e.error ||'No se pudo actualizar el usuario', 'Error');
        }
      })
    }
  }

  eliminarUsuario(user: any) {

    this.usuarioService.deleteUsuarios(user.id).subscribe({
      next: (datos) => {
        this.toastr.success('Se eliminó correctamene el usuario', 'Exito');
        this.cargarUsuarios();
      },
      error: (e) => {
        this.toastr.error(e.error ||"No se pudo eliminar el usuario", 'Error');
      }
    });
  }
}
