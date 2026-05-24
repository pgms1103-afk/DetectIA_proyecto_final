import { Component, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule} from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { ToastrService } from 'ngx-toastr';

/**
 * @component TopbarAdmin
 * @description Barra superior del panel administrativo.
 * Se encarga de mostrar la información del usuario autenticado (nombre, email, archivos)
 * y gestionar los eventos de navegación y menú de perfil.
 */
@Component({
  selector: 'app-topbar-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar-admin.html',
  styleUrl: './topbar-admin.css',
})
export class TopbarAdmin implements OnInit {
  usuarioService: UsuarioService = inject(UsuarioService);
  private toastr: ToastrService = inject(ToastrService);

  /** Evento para disparar la apertura/cierre del menú lateral (sidebar) */
  @Output() menuToggle = new EventEmitter<void>();

  /** Controla la visibilidad del menú desplegable de perfil */

  abrirMenuMovil() {
    this.menuToggle.emit();
  }


  showProfileMenu = false;

  /** Objeto que almacena los datos visuales del usuario en la topbar */
  user = {
    name: 'cargando...',
    email: 'cargando...',
    files: 0,
  };

  ngOnInit(): void {
    this.mostrarMisDatos();
  }

  /** Emite el evento para alternar el menú lateral */
  abrirMenu() {
    this.menuToggle.emit();
  }

  /** Alterna la visibilidad del menú contextual de perfil */
  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  /** * @method mostrarMisDatos
   * Obtiene la información del usuario logueado mediante el servicio
   * y actualiza el estado local del componente.
   */
  mostrarMisDatos() {
    this.usuarioService.getDatosUsuarioRegistrado().subscribe({
      next: (resultado) => {
        this.user.name = resultado.nombreUsuario;
        this.user.email = resultado.correo;
        this.user.files = resultado.totalArchivos;
      },
      error: (e) => {
        this.toastr.error(e.error || 'No se trajeron los datos', 'Error');
      },
    });
  }
}
