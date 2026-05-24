import {
  Component,
  Output,
  EventEmitter,
  inject,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';

/**
 * Componente Topbar encargado de:
 * - Mostrar información del usuario.
 * - Controlar el menú de perfil.
 * - Emitir eventos para abrir el sidebar.
 */
@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar implements OnInit {

  /**
   * Evento emitido al abrir el menú lateral.
   */
  @Output() readonly menuToggle = new EventEmitter<void>();

  /**
   * Servicio encargado de obtener información del usuario.
   */
  usuarioService: UsuarioService = inject(UsuarioService);

  /**
   * Servicio de notificaciones.
   */
  private toastr: ToastrService = inject(ToastrService);

  /**
   * Controla la visibilidad del menú de perfil.
   */
  showProfileMenu = false;

  /**
   * Información básica del usuario mostrada en pantalla.
   */
  user = {
    name: 'cargando...',
    email: 'cargando...',
    files: 0,
  };

  /**
   * Inicializa el componente.
   */
  ngOnInit(): void {
    this.mostrarMisDatos();
  }

  /**
   * Emite el evento para abrir el menú lateral.
   */
  abrirMenu() {
    this.menuToggle.emit();
  }

  /**
   * Muestra u oculta el menú de perfil.
   */
  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  /**
   * Obtiene los datos del usuario autenticado.
   */
  mostrarMisDatos() {

    this.usuarioService
      .getDatosUsuarioRegistrado()
      .subscribe({

        next: (resultado) => {

          this.user.name = resultado.nombreUsuario;
          this.user.email = resultado.correo;
          this.user.files = resultado.totalArchivos;
        },

        error: (e) => {

          this.toastr.error(
            e.error || 'No se trajeron los datos',
            'Error'
          );
        },
      });
  }
}
